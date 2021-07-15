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

import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSource;
import java.util.BitSet;

public abstract class ClickhouseColumnReader {

  private static final Object NOP_STATE = new Object();

  protected final int nRows;
  protected final ClickhouseBinaryColumnDescriptor columnDescriptor;
  protected BitSet nullsMap;
  protected Object itemsArray;

  protected ClickhouseColumnReader(int nRows, ClickhouseBinaryColumnDescriptor columnDescriptor) {
    this.columnDescriptor = columnDescriptor;
    this.nRows = nRows;
  }

  public ClickhouseBinaryColumnDescriptor columnDescriptor() {
    return columnDescriptor;
  }

  public void readColumn(ClickhouseStreamDataSource in){
    readStatePrefix(in);
    readData(in);
  }

  public int nRows() {
    return nRows;
  }

  protected Object readStatePrefix(ClickhouseStreamDataSource in) {
    return NOP_STATE;
  }

  protected void readData(ClickhouseStreamDataSource in) {
    if (columnDescriptor.isNullable() && nullsMap == null) {
      nullsMap = readNullsMap(in);
      if (nullsMap == null) {
        return;
      }
    }
    readDataInternal(in);
  }

  protected void readDataInternal(ClickhouseStreamDataSource in) {
    if (itemsArray == null) {
      itemsArray = readItems(in);
      if (itemsArray == null) {
        return;
      }
    }
    afterReadItems(in);
  }

  protected Object[] asObjectsArrayWithGetElement(int startIncluding, int endExcluding, Class<?> desired) {
    Object[] ret = (Object[]) allocateOneDimArray(desired, endExcluding - startIncluding);
    int arrayIdx = 0;
    for (int i = startIncluding; i < endExcluding; ++i) {
      ret[arrayIdx] = getElement(i, desired);
      ++arrayIdx;
    }
    return ret;
  }

  protected Object[] asObjectsArrayWithGetElement(Class<?> desired) {
    return asObjectsArrayWithGetElement(0, nRows, desired);
  }

  protected abstract Object readItems(ClickhouseStreamDataSource in);
  protected void afterReadItems(ClickhouseStreamDataSource in) {
  }

  protected BitSet readNullsMap(ClickhouseStreamDataSource in) {
    if (in.readableBytes() >= nRows) {
      BitSet bSet = new BitSet(nRows);
      for (int i = 0; i < nRows; ++i) {
        byte b = in.readByte();
        if (b != 0) {
          bSet.set(i);
        }
      }
      return bSet;
    }
    return null;
  }

  public boolean isPartial() {
    return itemsArray == null || (columnDescriptor.isNullable() && nullsMap == null);
  }

  public Object getElement(int rowIdx, Class<?> desired) {
    if (nullsMap != null && nullsMap.get(rowIdx)) {
      return null;
    }
    return getElementInternal(rowIdx, desired);
  }

  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    return java.lang.reflect.Array.get(itemsArray, rowIdx);
  }

  protected Object getObjectsArrayElement(int rowIdx) {
    Object[] data = (Object[]) itemsArray;
    return data[rowIdx];
  }

  public Object[] slices(int[] slices, Class<?> desired) {
    IntPairIterator slice = PairedIterator.of(slices);
    int sliceCount = slices.length - 1;
    Object[] ret = allocateTwoDimArray(desired, sliceCount, 0);
    if (desired.isPrimitive()) {
      if (columnDescriptor.isNullable()) {
        throw new IllegalArgumentException("primitive arrays are not supported for nullable columns");
      }
      for (int sliceIdx = 0; sliceIdx < sliceCount; ++sliceIdx) {
        slice.next();
        int len = slice.getValue() - slice.getKey();
        Object tmp = allocateOneDimArray(desired, len);
        System.arraycopy(itemsArray, slice.getKey(), tmp, 0, len);
        ret[sliceIdx] = tmp;
      }
    } else {
      for (int sliceIdx = 0; sliceIdx < sliceCount; ++sliceIdx) {
        slice.next();
        ret[sliceIdx] = asObjectsArrayWithGetElement(slice.getKey(), slice.getValue(), ret.getClass().getComponentType().getComponentType());
      }
    }
    return ret;
  }

  protected abstract Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2);
  protected abstract Object allocateOneDimArray(Class<?> desired, int length);
}
