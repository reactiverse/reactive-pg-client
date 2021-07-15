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
import io.vertx.clickhouseclient.binary.ClickhouseBinaryPool;
import io.vertx.core.impl.CloseFuture;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.spi.metrics.ClientMetrics;
import io.vertx.core.spi.metrics.VertxMetrics;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.PoolBase;
import io.vertx.sqlclient.impl.SqlConnectionImpl;
import io.vertx.sqlclient.impl.tracing.QueryTracer;

public class ClickhouseBinaryPoolImpl extends PoolBase<ClickhouseBinaryPoolImpl> implements ClickhouseBinaryPool {
  public static ClickhouseBinaryPoolImpl create(VertxInternal vertx, boolean closeVertx,
                                                ClickhouseBinaryConnectOptions connectOptions, PoolOptions poolOptions) {
    QueryTracer tracer = vertx.tracer() == null ? null : new QueryTracer(vertx.tracer(), connectOptions);
    VertxMetrics vertxMetrics = vertx.metricsSPI();
    ClientMetrics metrics = vertxMetrics != null ? vertxMetrics.createClientMetrics(connectOptions.getSocketAddress(),
      "sql", connectOptions.getMetricsName()) : null;
    ClickhouseBinaryPoolImpl pool = new ClickhouseBinaryPoolImpl(vertx,
      new ClickhouseBinaryConnectionFactory(vertx, connectOptions), tracer, metrics, poolOptions);
    CloseFuture closeFuture = pool.closeFuture();
    if (closeVertx) {
      closeFuture.future().onComplete(ar -> vertx.close());
    } else {
      vertx.addCloseHook(closeFuture);
    }
    return pool;
  }

  private final ClickhouseBinaryConnectionFactory factory;

  private ClickhouseBinaryPoolImpl(VertxInternal vertx, ClickhouseBinaryConnectionFactory factory, QueryTracer tracer,
                                   ClientMetrics metrics, PoolOptions poolOptions) {
    super(vertx, factory, tracer, metrics, 1, poolOptions);
    this.factory = factory;
  }

  @Override
  protected SqlConnectionImpl wrap(ContextInternal context, Connection conn) {
    return new ClickhouseBinaryConnectionImpl(factory, context, conn, tracer, metrics);
  }
}
