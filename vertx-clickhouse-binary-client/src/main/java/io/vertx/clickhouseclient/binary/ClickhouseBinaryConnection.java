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

package io.vertx.clickhouseclient.binary;

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryConnectionImpl;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import io.vertx.sqlclient.SqlConnection;

@VertxGen
public interface ClickhouseBinaryConnection extends SqlConnection {
  static void connect(Vertx vertx, ClickhouseBinaryConnectOptions connectOptions, Handler<AsyncResult<ClickhouseBinaryConnection>> handler) {
    Future<ClickhouseBinaryConnection> fut = connect(vertx, connectOptions);
    if (handler != null) {
      fut.onComplete(handler);
    }
  }

  static Future<ClickhouseBinaryConnection> connect(Vertx vertx, ClickhouseBinaryConnectOptions connectOptions) {
    return ClickhouseBinaryConnectionImpl.connect((ContextInternal) vertx.getOrCreateContext(), connectOptions);
  }
}
