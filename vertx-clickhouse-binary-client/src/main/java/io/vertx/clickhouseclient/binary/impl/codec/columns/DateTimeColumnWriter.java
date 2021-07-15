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

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public class DateTimeColumnWriter extends ClickhouseColumnWriter {
  public final OffsetDateTime maxValue;

  public DateTimeColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, ZoneId zoneId, int columnIndex) {
    super(data, columnDescriptor, columnIndex);
    this.maxValue = Instant.ofEpochSecond(DateTimeColumnReader.MAX_EPOCH_SECOND).atZone(zoneId).toOffsetDateTime();
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    OffsetDateTime dateTime = (OffsetDateTime) val;
    long epochSecond = dateTime.toInstant().getEpochSecond();
    if (epochSecond > DateTimeColumnReader.MAX_EPOCH_SECOND) {
      throw new IllegalArgumentException("value " + dateTime + " is too big; max epoch seconds: " + maxValue);
    }
    sink.writeIntLE((int) epochSecond);
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    sink.writeIntLE(0);
  }

}
