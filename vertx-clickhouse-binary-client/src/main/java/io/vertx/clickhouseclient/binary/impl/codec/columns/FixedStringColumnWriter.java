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

import java.nio.charset.Charset;
import java.util.List;

public class FixedStringColumnWriter extends ClickhouseColumnWriter {
  private final Charset charset;

  public FixedStringColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, Charset charset, int columnIndex) {
    super(data, columnDescriptor, columnIndex);
    this.charset = charset;
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    byte[] bytes = val.getClass() == byte[].class ? (byte[])val : ((String)val).getBytes(charset);
    int elSize = columnDescriptor.getElementSize();
    if (bytes.length > elSize) {
      throw new IllegalArgumentException("fixed string bytes are too long: got " + bytes.length + ", max " + elSize);
    }
    sink.writeBytes(bytes);
    sink.writeZero(elSize - bytes.length);
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    sink.writeZero(columnDescriptor.getElementSize());
  }
}
