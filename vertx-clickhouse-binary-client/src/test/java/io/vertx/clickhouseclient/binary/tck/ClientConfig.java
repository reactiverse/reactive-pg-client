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

package io.vertx.clickhouseclient.binary.tck;

import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnection;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryPool;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.tck.Connector;

public enum ClientConfig {
  CONNECT() {
    @Override
    Connector<ClickhouseBinaryConnection> connect(Vertx vertx, SqlConnectOptions options) {
      return new Connector<ClickhouseBinaryConnection>() {
        @Override
        public void connect(Handler<AsyncResult<ClickhouseBinaryConnection>> handler) {
          ClickhouseBinaryConnection.connect(vertx, new ClickhouseBinaryConnectOptions(options.toJson()), ar -> {
            if (ar.succeeded()) {
              handler.handle(Future.succeededFuture(ar.result()));
            } else {
              handler.handle(Future.failedFuture(ar.cause()));
            }
          });
        }
        @Override
        public void close() {
        }
      };
    }
  },

  POOLED() {
    @Override
    Connector<SqlClient> connect(Vertx vertx, SqlConnectOptions options) {
      ClickhouseBinaryPool pool = ClickhouseBinaryPool.pool(vertx, new ClickhouseBinaryConnectOptions(options.toJson()), new PoolOptions().setMaxSize(1));
      return new Connector<SqlClient>() {
        @Override
        public void connect(Handler<AsyncResult<SqlClient>> handler) {
          pool.getConnection(ar -> {
            if (ar.succeeded()) {
              handler.handle(Future.succeededFuture(ar.result()));
            } else {
              handler.handle(Future.failedFuture(ar.cause()));
            }
          });
        }
        @Override
        public void close() {
          pool.close();
        }
      };
    }
  };

  abstract <C extends SqlClient> Connector<C> connect(Vertx vertx, SqlConnectOptions options);
}
