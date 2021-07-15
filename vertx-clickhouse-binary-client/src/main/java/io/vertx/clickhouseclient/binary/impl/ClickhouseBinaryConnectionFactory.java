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

import io.vertx.clickhouseclient.binary.ClickhouseConstants;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.EventLoopContext;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.future.PromiseInternal;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.impl.NetSocketInternal;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.ConnectionFactory;
import io.vertx.sqlclient.impl.SqlConnectionFactoryBase;
import net.jpountz.lz4.LZ4Factory;

public class ClickhouseBinaryConnectionFactory extends SqlConnectionFactoryBase implements ConnectionFactory {
  private static final Logger LOG = LoggerFactory.getLogger(ClickhouseBinaryConnectionFactory.class);

  private final LZ4Factory lz4Factory;

  ClickhouseBinaryConnectionFactory(VertxInternal vertx, ClickhouseBinaryConnectOptions options) {
    super(vertx, options);
    this.lz4Factory = lz4FactoryForName(options.getProperties().getOrDefault(ClickhouseConstants.OPTION_COMPRESSOR, "none"));
  }

  private LZ4Factory lz4FactoryForName(String name) {
    if ("lz4_native".equals(name)) {
      return LZ4Factory.nativeInstance();
    } else if ("lz4_fastest".equals(name)) {
      return LZ4Factory.fastestInstance();
    } else if ("lz4_fastest_java".equals(name)) {
      return LZ4Factory.fastestJavaInstance();
    } else if ("lz4_safe".equals(name)) {
      return LZ4Factory.safeInstance();
    } else if ("lz4_unsafe".equals(name)) {
      return LZ4Factory.unsafeInstance();
    }
    if (!"none".equals(name)) {
      LOG.warn("unknown compressor name '" + name + "', ignored");
    }
    return null;
  }

  @Override
  protected void initializeConfiguration(SqlConnectOptions connectOptions) {
  }

  @Override
  protected void configureNetClientOptions(NetClientOptions netClientOptions) {
    netClientOptions.setSsl(false);
  }

  @Override
  protected void doConnectInternal(Promise<Connection> promise) {
    PromiseInternal<Connection> promiseInternal = (PromiseInternal<Connection>) promise;
    doConnect(ConnectionFactory.asEventLoopContext(promiseInternal.context())).flatMap(conn -> {
      ClickhouseBinarySocketConnection socket = (ClickhouseBinarySocketConnection) conn;
      socket.init();
      return Future.<Connection>future(p -> socket.sendStartupMessage(username, password, database, properties, p))
        .map(conn);
    }).onComplete(promise);
  }

  private Future<Connection> doConnect(EventLoopContext ctx) {
    Future<NetSocket> soFut;
    try {
      soFut = netClient.connect(socketAddress, (String) null);
    } catch (Exception e) {
      // Client is closed
      return ctx.failedFuture(e);
    }
    return soFut.map(so -> newSocketConnection(ctx, (NetSocketInternal) so));
  }

  private ClickhouseBinarySocketConnection newSocketConnection(EventLoopContext ctx, NetSocketInternal socket) {
    return new ClickhouseBinarySocketConnection(socket, cachePreparedStatements, preparedStatementCacheSize,
      preparedStatementCacheSqlFilter, ctx, lz4Factory);
  }
}
