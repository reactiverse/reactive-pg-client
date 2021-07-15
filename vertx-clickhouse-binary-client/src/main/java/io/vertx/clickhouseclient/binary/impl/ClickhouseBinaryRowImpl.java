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

package io.vertx.clickhouseclient.binary.impl;

import io.vertx.clickhouseclient.binary.impl.codec.columns.ClickhouseColumnReader;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.nio.charset.Charset;
import java.util.List;

public class ClickhouseBinaryRowImpl implements Row {
  private final int rowNo;
  private final Charset stringCharset;
  private final ClickhouseBinaryRowDesc rowDesc;
  private final ColumnOrientedBlock block;

  public ClickhouseBinaryRowImpl(int rowNo, ClickhouseBinaryRowDesc rowDesc, ColumnOrientedBlock block, ClickhouseBinaryDatabaseMetadata md) {
    this.rowNo = rowNo;
    this.rowDesc = rowDesc;
    this.block = block;
    this.stringCharset = md.getStringCharset();
  }

  @Override
  public String getColumnName(int pos) {
    return rowDesc.columnNames().get(pos);
  }

  @Override
  public int getColumnIndex(String column) {
    return rowDesc.columnIndex(column);
  }

  @Override
  public Object getValue(int columnIndex) {
    return getValue(columnIndex, Object.class);
  }

  private Object getValue(int columnIndex, Class<?> desired) {
    List<ClickhouseColumnReader> data = block.getData();
    ClickhouseColumnReader column = data.get(columnIndex);
    Object columnData = column.getElement(rowNo, desired);
    return columnData;
  }

  @Override
  public  <T> T get(Class<T> type, int position) {
    if (type == null) {
      throw new IllegalArgumentException("Accessor type can not be null");
    }
    Object value = getValue(position, type);
    if (value == null) {
      return null;
    }
    if (type.isAssignableFrom(value.getClass())) {
      return type.cast(value);
    }
    throw new ClassCastException("can't cast value " + value + " at position " + position + " of class " + value.getClass().getName() + " to class " + type.getName());
  }

  @Override
  public String getString(int pos) {
    Object val = getValue(pos);
    if (val == null) {
      return null;
    } else if (val instanceof String) {
      return (String) val;
    } else if (val instanceof Enum<?>) {
      return ((Enum<?>) val).name();
    } else if (val.getClass() == byte[].class) {
      return new String((byte[])val, stringCharset);
    } else {
      throw new ClassCastException("Invalid String value type " + val.getClass());
    }
  }

  @Override
  public Tuple addValue(Object value) {
    throw new IllegalStateException("not implemented");
  }

  @Override
  public int size() {
    return block.numColumns();
  }

  @Override
  public void clear() {
    throw new IllegalStateException("not implemented");
  }
}
