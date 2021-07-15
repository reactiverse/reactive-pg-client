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

import java.util.List;
import java.util.Map;

public class Enum16ColumnWriter extends UInt16ColumnWriter {
  private final EnumColumnEncoder columnEncoder;

  public Enum16ColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, int columnIndex,
                            Map<? extends Number, String> enumVals, EnumResolutionMethod resolutionMethod) {
    super(data, columnDescriptor, columnIndex);
    this.columnEncoder = new EnumColumnEncoder(enumVals, resolutionMethod);
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    Number idx = columnEncoder.encode(val);
    super.serializeDataElement(sink, idx);
  }
}
