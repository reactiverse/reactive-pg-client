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

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryRowDesc;
import io.vertx.sqlclient.impl.ParamDesc;
import io.vertx.sqlclient.impl.PreparedStatement;
import io.vertx.sqlclient.impl.RowDesc;
import io.vertx.sqlclient.impl.TupleInternal;

import java.util.UUID;

public class ClickhouseBinaryPreparedStatement implements PreparedStatement {
  private final String sql;
  private final ClickhouseBinaryParamDesc paramDesc;
  private final ClickhouseBinaryRowDesc rowDesc;
  private final QueryInfo queryInfo;
  private final boolean sentQuery;
  private final UUID psId;

  public ClickhouseBinaryPreparedStatement(String sql, ClickhouseBinaryParamDesc paramDesc, ClickhouseBinaryRowDesc rowDesc,
                                           QueryInfo queryInfo, boolean sentQuery, UUID psId) {
    this.sql = sql;
    this.paramDesc = paramDesc;
    this.rowDesc = rowDesc;
    this.queryInfo = queryInfo;
    this.sentQuery = sentQuery;
    this.psId = psId;
  }

  @Override
  public ParamDesc paramDesc() {
    return paramDesc;
  }

  @Override
  public RowDesc rowDesc() {
    return rowDesc;
  }

  @Override
  public String sql() {
    return sql;
  }

  @Override
  public String prepare(TupleInternal values) {
    return null;
  }

  public QueryInfo queryInfo() {
    return queryInfo;
  }

  public boolean isSentQuery() {
    return sentQuery;
  }

  public UUID getPsId() {
    return psId;
  }
}
