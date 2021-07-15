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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DateColumnWriter extends UInt16ColumnWriter {
  public static final long MAX_VALUE = 65535;

  public DateColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, int columnIndex) {
    super(data, columnDescriptor, columnIndex);
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    LocalDate dateVal = (LocalDate) val;
    long daysDelta = ChronoUnit.DAYS.between(DateColumnReader.MIN_VALUE, dateVal);
    if (daysDelta < 0) {
      throw new IllegalArgumentException("date " + dateVal + " is too small; smallest possible date: " + DateColumnReader.MIN_VALUE);
    }
    if (daysDelta > MAX_VALUE) {
      throw new IllegalArgumentException("date " + dateVal + " is too big; largest possible date: " + DateColumnReader.MAX_VALUE);
    }
    super.serializeDataElement(sink, daysDelta);
  }
}
