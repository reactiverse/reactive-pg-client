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

public class UInt16ColumnReader extends ClickhouseColumnReader {
  public static final int ELEMENT_SIZE = 2;

  public UInt16ColumnReader(int nRows, ClickhouseBinaryColumnDescriptor columnDescriptor) {
    super(nRows, columnDescriptor);
  }

  @Override
  protected Object readItems(ClickhouseStreamDataSource in) {
    if (in.readableBytes() >= ELEMENT_SIZE * nRows) {
      short[] data = new short[nRows];
      for (int i = 0; i < nRows; ++i) {
        if (nullsMap == null || !nullsMap.get(i)) {
          data[i] = in.readShortLE();
        } else {
          in.skipBytes(ELEMENT_SIZE);
        }
      }
      return data;
    }
    return null;
  }

  @Override
  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    short element = ((short[])this.itemsArray)[rowIdx];
    if (columnDescriptor.isUnsigned()) {
      return Short.toUnsignedInt(element);
    }
    return element;
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    if (columnDescriptor.isUnsigned()) {
      if (desired == int.class) {
        return new int[dim1][dim2];
      }
      return new Integer[dim1][dim2];
    }
    if (desired == short.class) {
      return new short[dim1][dim2];
    }
    return new Short[dim1][dim2];
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    if (columnDescriptor.isUnsigned()) {
      if (desired == int.class) {
        return new int[length];
      }
      return new Integer[length];
    }
    if (desired == short.class) {
      return new short[length];
    }
    return new Short[length];
  }
}
