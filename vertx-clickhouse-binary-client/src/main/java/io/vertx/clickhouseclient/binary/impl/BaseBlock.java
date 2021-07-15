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

import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.columns.ClickhouseColumnReader;
import io.vertx.sqlclient.desc.ColumnDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseBlock {
  private final Map<String, ClickhouseBinaryColumnDescriptor> columnsWithTypes;
  protected final ClickhouseBinaryRowDesc rowDesc;
  private final List<ClickhouseColumnReader> data;
  private final BlockInfo blockInfo;
  protected final ClickhouseBinaryDatabaseMetadata md;

  public BaseBlock(Map<String, ClickhouseBinaryColumnDescriptor> columnsWithTypes,
                   List<ClickhouseColumnReader> data, BlockInfo blockInfo, ClickhouseBinaryDatabaseMetadata md) {
    this.columnsWithTypes = columnsWithTypes;
    this.rowDesc = buildRowDescriptor(columnsWithTypes);
    this.data = data;
    this.blockInfo = blockInfo;
    this.md = md;
  }

  public Map<String, ClickhouseBinaryColumnDescriptor> getColumnsWithTypes() {
    return columnsWithTypes;
  }

  public List<ClickhouseColumnReader> getData() {
    return data;
  }

  public BlockInfo getBlockInfo() {
    return blockInfo;
  }

  public ClickhouseBinaryDatabaseMetadata getMd() {
    return md;
  }

  public ClickhouseBinaryRowDesc rowDesc() {
    return rowDesc;
  }

  private ClickhouseBinaryRowDesc buildRowDescriptor(Map<String, ClickhouseBinaryColumnDescriptor> columnsWithTypes) {
    List<String> columnNames = new ArrayList<>(columnsWithTypes.keySet());
    List<ColumnDescriptor> columnTypes = new ArrayList<>(columnsWithTypes.values());
    return new ClickhouseBinaryRowDesc(columnNames, columnTypes);
  }
}
