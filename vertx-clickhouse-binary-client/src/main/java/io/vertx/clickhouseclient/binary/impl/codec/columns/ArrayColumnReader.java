/*
 *
 *  Copyright (c) 2021 Vladimir Vishnevskii
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 *  which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 */

package io.vertx.clickhouseclient.binary.impl.codec.columns;

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSource;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class ArrayColumnReader extends ClickhouseColumnReader {
  private static final Logger LOG = LoggerFactory.getLogger(ArrayColumnReader.class);

  private final ClickhouseBinaryDatabaseMetadata md;
  private final ClickhouseBinaryColumnDescriptor elementTypeDescr;

  private int[][][] perRowsSlice;

  private Integer curDimension;
  private ClickhouseColumnReader nestedColumnReader;
  private ClickhouseColumn nestedColumn;
  private Class<?> elementClass;
  private Integer nItems;
  private Object statePrefix;

  private Integer curLevelSliceSize;

  public ArrayColumnReader(int nRows, ClickhouseBinaryColumnDescriptor descr, ClickhouseBinaryDatabaseMetadata md) {
    super(nRows, descr);
    this.md = md;
    this.elementTypeDescr = descr.getNestedDescr();
  }

  @Override
  protected Object readStatePrefix(ClickhouseStreamDataSource in) {
    ClickhouseColumnReader statePrefixColumn = ClickhouseColumns.columnForSpec(elementTypeDescr, md).reader(0);
    if (statePrefix == null) {
      statePrefix = statePrefixColumn.readStatePrefix(in);
    }
    return statePrefix;
  }

  @Override
  protected Object readItems(ClickhouseStreamDataSource in) {
    if (nItems == null) {
      curDimension = 0;
      nItems = 0;
    }
    if (statePrefix == null) {
      return null;
    }
    if (curDimension < columnDescriptor.arrayDimensionsCount()) {
      readAsPerRowSlices(in);
      if (curDimension < columnDescriptor.arrayDimensionsCount()) {
        return null;
      }
    }
    if (nestedColumnReader == null) {
      nestedColumn = ClickhouseColumns.columnForSpec(elementTypeDescr, md);
      nestedColumnReader = nestedColumn.reader(nItems);
      elementClass = nestedColumn.nullValue().getClass();
    }
    if (elementTypeDescr.isNullable()) {
      nestedColumnReader.nullsMap = nestedColumnReader.readNullsMap(in);
    }
    if (nItems > 0) {
      assert nItems == nestedColumnReader.nRows;
      if (nestedColumnReader.getClass() == LowCardinalityColumnReader.class) {
        ((LowCardinalityColumnReader) nestedColumnReader).keysSerializationVersion = (Long) statePrefix;
      }
      if (nestedColumnReader.isPartial()) {
        nestedColumnReader.itemsArray = nestedColumnReader.readItems(in);
        if (nestedColumnReader.isPartial()) {
          return null;
        }
      }
      return nestedColumnReader.itemsArray;
    }
    return nestedColumn.emptyArray();
  }

  @Override
  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    desired = maybeUnwrapArrayElementType(desired);
    return resliceIntoArray(nestedColumnReader, perRowsSlice[rowIdx], desired);
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    throw new IllegalArgumentException("not implemented");
  }

  private Class<?> maybeUnwrapArrayElementType(Class<?> desired) {
    if (desired != null) {
      while (desired.isArray() && desired != byte[].class) {
        desired = desired.getComponentType();
      }
    }
    return desired;
  }


  private Object resliceIntoArray(ClickhouseColumnReader reader, int[][] sliceToUse, Class<?> elementClass) {
    int i = sliceToUse.length - 1;
    int[] slices = sliceToUse[i];
    Object[] intermData = reader.slices(slices, elementClass); //resliceArray(dataAccessor, slices, java.lang.reflect.Array.newInstance(elementClass, 0).getClass());

    for (i = sliceToUse.length - 2; i >= 0; --i) {
      slices = sliceToUse[i];
      intermData = resliceArray(intermData, slices, intermData.getClass());
    }
    return intermData[0];
  }

  private Object[] resliceArray(Object[] dataElements, int[] slices, Class upperClass) {
    IntPairIterator paired = PairedIterator.of(slices);
    Object[] newDataList = (Object[]) java.lang.reflect.Array.newInstance(upperClass, slices.length - 1);
    int tmpSliceIdx = 0;
    while (paired.hasNext()) {
      paired.next();
      int newSliceSz = paired.getValue() - paired.getKey();
      Object[] reslicedArray = (Object[]) java.lang.reflect.Array.newInstance(upperClass.getComponentType(), newSliceSz);
      System.arraycopy(dataElements, paired.getKey(), reslicedArray, 0, newSliceSz);
      newDataList[tmpSliceIdx] = reslicedArray;
      ++tmpSliceIdx;
    }
    return newDataList;
  }

  private void readAsPerRowSlices(ClickhouseStreamDataSource in) {
    if (nRows == 0) {
      perRowsSlice = new int[0][][];
      curDimension = columnDescriptor.arrayDimensionsCount();
      return;
    }

    perRowsSlice = new int[nRows][][];
    for (int i = 0; i < nRows; ++i) {
      perRowsSlice[i] = new int[columnDescriptor.arrayDimensionsCount()][];
    }
    curLevelSliceSize = nRows;
    while (curDimension < columnDescriptor.arrayDimensionsCount()) {
      if (in.readableBytes() < curLevelSliceSize * Long.BYTES) {
        return;
      }
      long prevSliceElement = 0;
      for (int rowIdx = 0; rowIdx < nRows; ++rowIdx) {
        int rowSliceElementsToReadAtDimension;
        if (curDimension == 0) {
          rowSliceElementsToReadAtDimension = 1;
        } else {
          int[] rowSliceAtPrevDimension = perRowsSlice[rowIdx][curDimension - 1];
          rowSliceElementsToReadAtDimension = rowSliceAtPrevDimension[rowSliceAtPrevDimension.length - 1] - rowSliceAtPrevDimension[0];
        }
        int[] rowSliceAtDimension = new int[rowSliceElementsToReadAtDimension + 1];
        //offsets at last dimension are absolute
        boolean lastDimension = curDimension == columnDescriptor.arrayDimensionsCount() - 1;
        int firstElementInSlice = (int) prevSliceElement;
        rowSliceAtDimension[0] = (firstElementInSlice - (int)(lastDimension ? 0L : firstElementInSlice));
        for (int i = 0; i < rowSliceElementsToReadAtDimension; ++i) {
          prevSliceElement = in.readLongLE();
          if (prevSliceElement > Integer.MAX_VALUE) {
            throw new IllegalStateException("nested size is too big (" + prevSliceElement + "), max " + Integer.MAX_VALUE);
          }
          rowSliceAtDimension[i + 1] = ((int)(prevSliceElement - (lastDimension ? 0L : firstElementInSlice)));
        }
        perRowsSlice[rowIdx][curDimension] = (rowSliceAtDimension);
      }
      ++curDimension;
      curLevelSliceSize = (int)prevSliceElement;
    }
    nItems = curLevelSliceSize;
  }
}
