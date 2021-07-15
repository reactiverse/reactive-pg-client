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
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSink;
import io.vertx.sqlclient.Tuple;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public class DateTime64ColumnWriter extends ClickhouseColumnWriter {
  private final BigInteger invTickSize;
  private final long invTickLong;
  private final ZoneId zoneId;
  private final boolean saturateExtraNanos;

  public DateTime64ColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor descr, Integer precision, ZoneId zoneId, boolean saturateExtraNanos, int columnIndex) {
    super(data, descr, columnIndex);
    this.zoneId = zoneId;
    this.invTickSize = BigInteger.TEN.pow(precision);
    this.invTickLong = invTickSize.longValueExact();
    this.saturateExtraNanos = saturateExtraNanos;
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    OffsetDateTime dt = (OffsetDateTime) val;
    //TODO: maybe check zone offset
    long tickCount = invTickSize.multiply(BigInteger.valueOf(dt.toEpochSecond())).longValue();
    long nanos = dt.getNano();
    if (nanos < invTickLong) {
      tickCount += nanos;
    } else {
      if (saturateExtraNanos) {
        tickCount += invTickLong - 1;
      } else {
        throw new IllegalArgumentException("nano adjustment " + nanos + " is too big, max " + invTickLong);
      }
    }
    sink.writeLongLE(tickCount);
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    sink.writeZero(DateTime64Column.ELEMENT_SIZE);
  }
}
