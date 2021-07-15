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
import io.vertx.sqlclient.data.Numeric;

import java.math.BigInteger;

public class UInt64ColumnReader extends ClickhouseColumnReader {
  public static final int ELEMENT_SIZE = 8;

  public UInt64ColumnReader(int nRows, ClickhouseBinaryColumnDescriptor columnDescriptor) {
    super(nRows, columnDescriptor);
  }

  @Override
  protected Object readItems(ClickhouseStreamDataSource in) {
    //TODO: maybe read elements as soon as they arrive if we have enough data (> ELEMENT_SIZE)
    //TODO: maybe store all fixed-size types within (direct)buffer (+WeakReference + Queue to release buffer)
    if (in.readableBytes() >= ELEMENT_SIZE * nRows) {
      long[] data = new long[nRows];
      for (int i = 0; i < nRows; ++i) {
        if (nullsMap == null || !nullsMap.get(i)) {
          data[i] = in.readLongLE();
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
    long element = ((long[])this.itemsArray)[rowIdx];
    if (columnDescriptor.isUnsigned()) {
      return Numeric.create(unsignedBi(element));
    }
    return element;
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    if (columnDescriptor.isUnsigned()) {
      return new Numeric[dim1][dim2];
    }
    if (desired == long.class) {
      return new long[dim1][dim2];
    }
    return new Long[dim1][dim2];
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    if (columnDescriptor.isUnsigned()) {
      return new Numeric[length];
    }
    if (desired == long.class) {
      return new long[length];
    }
    return new Long[length];
  }

  static BigInteger unsignedBi(long l) {
    return new BigInteger(1, new byte[] {
      (byte) (l >>> 56 & 0xFF),
      (byte) (l >>> 48 & 0xFF),
      (byte) (l >>> 40 & 0xFF),
      (byte) (l >>> 32 & 0xFF),
      (byte) (l >>> 24 & 0xFF),
      (byte) (l >>> 16 & 0xFF),
      (byte) (l >>> 8 & 0xFF),
      (byte) (l & 0xFF)
    });
  }
}
