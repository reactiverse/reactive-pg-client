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

import java.net.Inet6Address;
import java.nio.charset.Charset;
import java.util.List;

public class IPv6ColumnWriter extends FixedStringColumnWriter {
  public IPv6ColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, Charset charset, int columnIndex) {
    super(data, columnDescriptor, charset, columnIndex);
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    Inet6Address address = (Inet6Address) val;
    byte[] bytes = address.getAddress();
    super.serializeDataElement(sink, bytes);
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    sink.writeZero(IPv6Column.ELEMENT_SIZE);
  }
}
