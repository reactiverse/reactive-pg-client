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

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSink;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayColumnWriter extends ClickhouseColumnWriter {
  private final ClickhouseBinaryDatabaseMetadata md;
  private final ClickhouseBinaryColumnDescriptor elementTypeDescr;
  private final ClickhouseColumn elementTypeColumn;

  public ArrayColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor descriptor, ClickhouseBinaryDatabaseMetadata md, int columnIndex) {
    super(data, descriptor, columnIndex);
    this.md = md;
    this.elementTypeDescr = descriptor.getNestedDescr();
    this.elementTypeColumn = ClickhouseColumns.columnForSpec(elementTypeDescr, md);
  }

  @Override
  protected void serializeDataInternal(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    writeSizes(sink, false, fromRow, toRow);
    writeNullsInfo(sink, 0, columnDescriptor, data, fromRow, toRow, columnIndex);
    writeElementData(sink, 0, columnDescriptor, data, fromRow, toRow, columnIndex);
  }

  private void writeElementData(ClickhouseStreamDataSink sink, int localDepth, ClickhouseBinaryColumnDescriptor descr, List<Tuple> localData, int fromRow, int toRow, int colIndex) {
    if (localDepth != 0 && descr.isArray() && elementTypeDescr.isNullable() && localData == null) {
      localData = Collections.emptyList();
    }

    while (localDepth < descr.arrayDimensionsCount()) {
      localData = flattenArrays(localData, fromRow, toRow, colIndex);
      colIndex = 0;
      fromRow = 0;
      toRow = localData.size();
      localDepth += 1;
    }
    ClickhouseColumn localNestedColumn = ClickhouseColumns.columnForSpec(elementTypeDescr, md);
    ClickhouseColumnWriter localWriter = localNestedColumn.writer(localData, colIndex);
    localWriter.serializeDataInternal(sink, fromRow, toRow);
  }

  private void writeNullsInfo(ClickhouseStreamDataSink sink, int localDepth, ClickhouseBinaryColumnDescriptor descr, List<Tuple> localData, int fromRow, int toRow, int colIndex) {
    if (localDepth != 0 && descr.isArray() && elementTypeDescr.isNullable() && localData == null) {
      localData = Collections.emptyList();
    }

    while (localDepth < descr.arrayDimensionsCount()) {
      localData = flattenArrays(localData, fromRow, toRow, colIndex);
      colIndex = 0;
      fromRow = 0;
      toRow = localData.size();
      localDepth += 1;
    }
    if (elementTypeDescr.isNullable()) {
      elementTypeColumn.writer(localData, colIndex).serializeNullsMap(sink, fromRow, toRow);
    }
  }

  private static List<Tuple> flattenArrays(List<Tuple> data, int fromRow, int toRow, int colIndex) {
    List<Tuple> result = new ArrayList<>();
    for (int i = fromRow; i < toRow; ++i) {
      Tuple row = data.get(i);
      Object element = row.getValue(colIndex);
      Class<?> cls = element.getClass();
      if (cls.isArray() && cls != byte[].class) {
        Object[] arr = (Object[])element;
        List<Tuple> tuples = Arrays.stream(arr).map(Tuple::of).collect(Collectors.toList());
        result.addAll(tuples);
      } else {
        result.add(Tuple.of(element));
      }
    }
    return result;
  }

  private void writeSizes(ClickhouseStreamDataSink sink, boolean writeTotalSize, int fromRow, int toRow) {
    int nRows = toRow - fromRow;
    List<Integer> sizes = new ArrayList<>();
    if (writeTotalSize) {
      sizes.add(nRows);
    }

    List<?> values = data;
    int localColumnIndex = columnIndex;
    int localDepth = 0;
    while (localDepth < columnDescriptor.arrayDimensionsCount()) {
      int offset = 0;
      List<Object> newValue = new ArrayList<>();
      for (int i = fromRow; i < toRow; ++i) {
        Object valObj = values.get(i);
        Object tmp = maybeUnwrapTuple(valObj, localColumnIndex);
        Object[] val = (Object[]) tmp;
        offset += val.length;
        sizes.add(offset);
        List<Object> newTuples = Arrays.asList(val);
        newValue.addAll(newTuples);
      }
      values = newValue;
      ++localDepth;
      localColumnIndex = 0;
      fromRow = 0;
      toRow = newValue.size();
    }
    sink.ensureWritable(sizes.size() * Long.BYTES);
    for (Integer size : sizes) {
      sink.writeLongLE(size);
    }
  }

  protected void serializeStatePrefix(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    elementTypeColumn.writer(data, 0).serializeStatePrefix(sink, fromRow, toRow);
  }

  private static Object maybeUnwrapTuple(Object val, int columnIndex) {
    return val instanceof Tuple ? ((Tuple)val).getValue(columnIndex) : val;
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
  }

  @Override
  protected void serializeNullsMap(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
  }

  @Override
  protected void ensureCapacity(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
  }
}
