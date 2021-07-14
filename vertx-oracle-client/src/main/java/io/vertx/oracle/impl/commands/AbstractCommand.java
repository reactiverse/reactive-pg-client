/*
 * Copyright (c) 2011-2021 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.oracle.impl.commands;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.oracle.OracleConnectOptions;
import io.vertx.sqlclient.impl.command.CommandBase;
import oracle.jdbc.OracleConnection;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractCommand<T> extends CommandBase<T> {

  protected final OracleConnectOptions options;

  protected AbstractCommand(OracleConnectOptions options) {
    this.options = options;
  }

  public abstract Future<T> execute(OracleConnection conn, Context context);

  protected void applyStatementOptions(Statement statement) throws SQLException {
    if (options != null) {
      if (options.getQueryTimeout() > 0) {
        statement.setQueryTimeout(options.getQueryTimeout());
      }
      if (options.getFetchDirection() != null) {
        //noinspection MagicConstant
        statement.setFetchDirection(options.getFetchDirection().getType());
      }
      if (options.getFetchSize() != 0) {
        statement.setFetchSize(options.getFetchSize());
      }
      if (options.getMaxRows() > 0) {
        statement.setMaxRows(options.getMaxRows());
      }
    }
  }

}
