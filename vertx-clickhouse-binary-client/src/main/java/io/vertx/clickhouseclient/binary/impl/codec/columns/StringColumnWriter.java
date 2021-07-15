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

public class StringColumnWriter extends ClickhouseColumnWriter {
  private final Charset charset;
  public StringColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, Charset charset, int columnIndex) {
    super(data, columnDescriptor, columnIndex);
    this.charset = charset;
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    byte[] bytes = val.getClass() == byte[].class ? (byte[])val : ((String)val).getBytes(charset);
    sink.writeULeb128(bytes.length);
    sink.writeBytes(bytes);
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    sink.writeULeb128(0);
  }

  protected int elementsSize(int fromRow, int toRow) {
    //max value, usually less
    int sz = (toRow - fromRow) * 4;
    for (int i = fromRow; i < toRow; ++i) {
      Object el = data.get(i).getValue(columnIndex);
      if (el != null) {
        if (el.getClass() == byte[].class) {
          sz += ((byte[])el).length;
        } else {
          if (el.getClass() == String.class) {
            //min value, more for non-ascii chars
            sz += ((String)el).length();
          }
        }
      }
    }
    return sz;
  }
}
