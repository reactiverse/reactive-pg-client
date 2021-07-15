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

package io.vertx.clickhouseclient.binary.impl.codec;

import io.netty.buffer.ByteBuf;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryRowImpl;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryRowDesc;
import io.vertx.clickhouseclient.binary.impl.ColumnOrientedBlock;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.impl.RowDecoder;

import java.util.stream.Collector;

class RowResultDecoder<C, R> extends RowDecoder<C, R> {
  private static final Logger LOG = LoggerFactory.getLogger(RowResultDecoder.class);

  private final ClickhouseBinaryRowDesc rowDesc;
  private final ClickhouseBinaryDatabaseMetadata md;
  private ColumnOrientedBlock block;
  private int rowNo;

  protected RowResultDecoder(Collector<Row, C, R> collector, ClickhouseBinaryRowDesc rowDesc, ClickhouseBinaryDatabaseMetadata md) {
    super(collector);
    this.rowDesc = rowDesc;
    this.md = md;
  }

  @Override
  protected Row decodeRow(int len, ByteBuf in) {
    ClickhouseBinaryRowImpl row = block.row(rowNo);
    ++rowNo;
    return row;
  }

  public void generateRows(ColumnOrientedBlock block) {
    this.block = block;
    this.rowNo = 0;
    for (int i = 0; i < block.numRows(); ++i) {
      this.handleRow(-1, null);
    }
    this.block = null;
    this.rowNo = 0;
  }

  public ClickhouseBinaryRowDesc getRowDesc() {
    return rowDesc;
  }
}
