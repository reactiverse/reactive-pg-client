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

import java.math.BigInteger;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class DateTime64ColumnReader extends ClickhouseColumnReader {

  private final ZoneId zoneId;
  private final BigInteger invTickSize;

  public DateTime64ColumnReader(int nRows, ClickhouseBinaryColumnDescriptor descr, Integer precision, ZoneId zoneId) {
    super(nRows, descr);
    this.zoneId = zoneId;
    this.invTickSize = BigInteger.TEN.pow(precision);
  }

  @Override
  protected Object readItems(ClickhouseStreamDataSource in) {
    if (in.readableBytes() >= DateTime64Column.ELEMENT_SIZE * nRows) {
      OffsetDateTime[] data = new OffsetDateTime[nRows];
      for (int i = 0; i < nRows; ++i) {
        if (nullsMap == null || !nullsMap.get(i)) {
          BigInteger bi = UInt64ColumnReader.unsignedBi(in.readLongLE());
          long seconds = bi.divide(invTickSize).longValueExact();
          long nanos = bi.remainder(invTickSize).longValueExact();
          OffsetDateTime dt = Instant.ofEpochSecond(seconds, nanos).atZone(zoneId).toOffsetDateTime();
          data[i] = dt;
        } else {
          in.skipBytes(DateTime64Column.ELEMENT_SIZE);
        }
      }
      return data;
    }
    return null;
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    return new OffsetDateTime[dim1][dim2];
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    return new OffsetDateTime[length];
  }
}
