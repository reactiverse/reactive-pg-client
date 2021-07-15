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

public class Decimal32ColumnReader extends ClickhouseColumnReader {
  private final MathContext mc;

  protected Decimal32ColumnReader(int nRows, ClickhouseBinaryColumnDescriptor columnDescriptor, MathContext mc) {
    super(nRows, columnDescriptor);
    this.mc = mc;
  }

  @Override
  protected Object readItems(ClickhouseStreamDataSource in) {
    if (in.readableBytes() >= Decimal32Column.ELEMENT_SIZE * nRows) {
      Numeric[] data = new Numeric[nRows];
      int scale = columnDescriptor.getScale();
      for (int i = 0; i < nRows; ++i) {
        if (nullsMap == null || !nullsMap.get(i)) {
          int item = in.readIntLE();
          data[i] = Numeric.create(new BigDecimal(BigInteger.valueOf(item), scale, mc));
        } else {
          in.skipBytes(Decimal32Column.ELEMENT_SIZE);
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
