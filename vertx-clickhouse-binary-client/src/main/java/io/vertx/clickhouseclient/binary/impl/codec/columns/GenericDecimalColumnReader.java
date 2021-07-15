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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class GenericDecimalColumnReader extends ClickhouseColumnReader {
  private final MathContext mc;

  protected GenericDecimalColumnReader(int nRows, ClickhouseBinaryColumnDescriptor columnDescriptor, MathContext mathContext) {
    super(nRows, columnDescriptor);
    this.mc = mathContext;
  }

  @Override
  protected Object readItems(ClickhouseStreamDataSource in) {
    int elementSize = columnDescriptor.getElementSize();
    if (in.readableBytes() >= elementSize * nRows) {
      Numeric[] data = new Numeric[nRows];
      int scale = columnDescriptor.getScale();
      byte[] readBuffer = new byte[elementSize];
      for (int i = 0; i < nRows; ++i) {
        if (nullsMap == null || !nullsMap.get(i)) {
          in.readBytes(readBuffer);
          BigInteger bi = new BigInteger(ColumnUtils.reverse(readBuffer));
          data[i] = Numeric.create(new BigDecimal(bi, scale, mc));
        } else {
          in.skipBytes(elementSize);
        }
      }
      return data;
    }
    return null;
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    return new Numeric[dim1][dim2];
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    return new Numeric[length];
  }
}
