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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ColumnOrientedBlock extends BaseBlock {
  public ColumnOrientedBlock(Map<String, ClickhouseBinaryColumnDescriptor> columnsWithTypes,
                             List<ClickhouseColumnReader> data, BlockInfo blockInfo, ClickhouseBinaryDatabaseMetadata md) {
    super(columnsWithTypes, data, blockInfo, md);
  }

  public int numColumns() {
    Collection<ClickhouseColumnReader> dt = getData();
    return dt == null ? 0 : dt.size();
  }

  public int numRows() {
    if (numColumns() > 0) {
      ClickhouseColumnReader firstColumn = getData().iterator().next();
      return firstColumn.nRows();
    } else {
      return 0;
    }
  }

  public List<ClickhouseBinaryRowImpl> rows() {
    int numRows = numRows();
    List<ClickhouseBinaryRowImpl> ret = new ArrayList<>(numRows);
    for (int i = 0; i < numRows; ++i) {
      ret.add(row(i));
    }
    return ret;
  }

  public ClickhouseBinaryRowImpl row(int rowNo) {
    return new ClickhouseBinaryRowImpl(rowNo, rowDesc, this, md);
  }
}
