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

import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnection;
import io.vertx.core.Future;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.future.PromiseInternal;
import io.vertx.core.spi.metrics.ClientMetrics;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.SqlConnectionImpl;
import io.vertx.sqlclient.impl.tracing.QueryTracer;

public class ClickhouseBinaryConnectionImpl extends SqlConnectionImpl<ClickhouseBinaryConnectionImpl> implements ClickhouseBinaryConnection {
  private final ClickhouseBinaryConnectionFactory factory;

  public static Future<ClickhouseBinaryConnection> connect(ContextInternal ctx, ClickhouseBinaryConnectOptions options) {
    ClickhouseBinaryConnectionFactory client;
    try {
      client = new ClickhouseBinaryConnectionFactory(ctx.owner(), options);
    } catch (Exception e) {
      return ctx.failedFuture(e);
    }
    ctx.addCloseHook(client);
    QueryTracer tracer = ctx.tracer() == null ? null : new QueryTracer(ctx.tracer(), options);
    PromiseInternal<Connection> promise = ctx.promise();
    client.connect(promise);
    return promise.future().map(conn -> {
      ClickhouseBinaryConnectionImpl mySQLConnection = new ClickhouseBinaryConnectionImpl(client, ctx, conn, tracer, null);
      conn.init(mySQLConnection);
      return mySQLConnection;
    });
  }

  ClickhouseBinaryConnectionImpl(ClickhouseBinaryConnectionFactory factory, ContextInternal context, Connection conn, QueryTracer tracer, ClientMetrics metrics) {
    super(context, conn, tracer, metrics);
    this.factory = factory;
  }

  @Override
  public Future<Transaction> begin() {
    return Future.failedFuture(new UnsupportedOperationException());
  }
}
