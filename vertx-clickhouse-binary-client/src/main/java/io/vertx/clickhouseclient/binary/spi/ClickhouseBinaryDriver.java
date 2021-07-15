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

package io.vertx.clickhouseclient.binary.spi;

import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryPool;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.spi.Driver;

public class ClickhouseBinaryDriver implements Driver {
  @Override
  public Pool createPool(SqlConnectOptions options, PoolOptions poolOptions) {
    return ClickhouseBinaryPool.pool(wrap(options), poolOptions);
  }

  @Override
  public Pool createPool(Vertx vertx, SqlConnectOptions options, PoolOptions poolOptions) {
    return ClickhouseBinaryPool.pool(vertx, wrap(options), poolOptions);
  }

  @Override
  public boolean acceptsOptions(SqlConnectOptions options) {
    return options instanceof ClickhouseBinaryConnectOptions || SqlConnectOptions.class.equals(options.getClass());
  }

  private static ClickhouseBinaryConnectOptions wrap(SqlConnectOptions options) {
    if (options instanceof ClickhouseBinaryConnectOptions) {
      return (ClickhouseBinaryConnectOptions) options;
    } else {
      return new ClickhouseBinaryConnectOptions(options);
    }
  }
}
