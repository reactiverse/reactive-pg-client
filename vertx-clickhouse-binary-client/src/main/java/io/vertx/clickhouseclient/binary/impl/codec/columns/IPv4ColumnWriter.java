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

import java.net.Inet4Address;
import java.util.List;

public class IPv4ColumnWriter extends UInt32ColumnWriter {
  public IPv4ColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, int columnIndex) {
    super(data, columnDescriptor, columnIndex);
  }

  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    Inet4Address addr = (Inet4Address) val;
    super.serializeDataElement(sink, Integer.toUnsignedLong(intFromBytes(addr.getAddress())));
  }

  private static int intFromBytes(byte[] b) {
    return (0xFF000000 & (b[0] << 24)) | (0xFF0000 & (b[1] << 16)) | (0xFF00 & (b[2] << 8)) | (0xFF & (b[3]));
  }
}
