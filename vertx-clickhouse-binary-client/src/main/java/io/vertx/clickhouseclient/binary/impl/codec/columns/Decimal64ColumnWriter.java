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
import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class Decimal64ColumnWriter extends ClickhouseColumnWriter {
  public Decimal64ColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, int columnIndex) {
    super(data, columnDescriptor, columnIndex);
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    BigDecimal bd = ((Numeric) val).bigDecimalValue();
    if (bd == null) {
      serializeDataNull(sink);
      return;
    }
    ColumnUtils.bigDecimalFitsOrThrow(bd, columnDescriptor);
    BigInteger bi = bd.unscaledValue();
    sink.writeLongLE(bi.longValueExact());
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    sink.writeZero(Decimal64Column.ELEMENT_SIZE);
  }
}
