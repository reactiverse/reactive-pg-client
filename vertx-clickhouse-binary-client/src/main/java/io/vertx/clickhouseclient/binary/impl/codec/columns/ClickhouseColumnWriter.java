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

public abstract class ClickhouseColumnWriter {
  protected final List<Tuple> data;
  protected final ClickhouseBinaryColumnDescriptor columnDescriptor;
  protected final int columnIndex;

  public ClickhouseColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, int columnIndex) {
    this.data = data;
    this.columnDescriptor = columnDescriptor;
    this.columnIndex = columnIndex;
  }

  public void serializeColumn(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    serializeStatePrefix(sink, fromRow, toRow);
    serializeData(sink, fromRow, toRow);
  }

  protected void serializeStatePrefix(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
  }

  protected void serializeData(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    ensureCapacity(sink, fromRow, toRow);
    if (columnDescriptor.isNullable()) {
      serializeNullsMap(sink, fromRow, toRow);
    }
    serializeDataInternal(sink, fromRow, toRow);
  }

  protected int nullsMapSize(int nRows) {
    if (columnDescriptor.isNullable() && !columnDescriptor.isLowCardinality()) {
      return nRows;
    }
    return 0;
  }

  protected int elementsSize(int fromRow, int toRow) {
    if (columnDescriptor.getElementSize() > 0) {
      return  (toRow - fromRow) * columnDescriptor.getElementSize();
    }
    return 0;
  }

  protected void ensureCapacity(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    int requiredSize = 0;
    requiredSize += nullsMapSize(toRow - fromRow);
    requiredSize += elementsSize(fromRow, toRow);
    sink.ensureWritable(requiredSize);
  }

  protected void serializeNullsMap(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    for (int rowNo = fromRow; rowNo < toRow; ++rowNo) {
      Object val = data.get(rowNo).getValue(columnIndex);
      sink.writeBoolean(val == null);
    }
  }

  protected void serializeDataInternal(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    for (int rowNo = fromRow; rowNo < toRow; ++rowNo) {
      Object val = data.get(rowNo).getValue(columnIndex);
      if (val == null) {
        if (columnDescriptor.isNullable()) {
          serializeDataNull(sink);
        } else {
          throw new IllegalArgumentException("can't serialize null for non-nullable column " + columnDescriptor.name() + " at row " + rowNo);
        }
      } else {
        serializeDataElement(sink, val);
      }
    }
  }

  protected abstract void serializeDataElement(ClickhouseStreamDataSink sink, Object val);

  //TODO: maybe skip bytes instead (perform ByteBuf.writerIndex(writerIndex() + elemSize)) (is allocated memory zero-filled ?)
  protected abstract void serializeDataNull(ClickhouseStreamDataSink sink);
}
