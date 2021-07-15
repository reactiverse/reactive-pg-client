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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinarySocketConnection;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.impl.command.CommandBase;
import io.vertx.sqlclient.impl.command.CommandResponse;

import java.util.ArrayDeque;
import java.util.Iterator;

public class ClickhouseBinaryCodec extends CombinedChannelDuplexHandler<ClickhouseBinaryDecoder, ClickhouseBinaryEncoder> {
  private static final Logger LOG = LoggerFactory.getLogger(ClickhouseBinaryCodec.class);

  private ArrayDeque<ClickhouseBinaryCommandCodec<?, ?>> inflight;

  public ClickhouseBinaryCodec(ClickhouseBinarySocketConnection conn) {
    inflight = new ArrayDeque<>();
    ClickhouseBinaryEncoder encoder = new ClickhouseBinaryEncoder(inflight, conn);
    ClickhouseBinaryDecoder decoder = new ClickhouseBinaryDecoder(inflight, conn);
    init(decoder, encoder);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    fail(ctx, cause);
    super.exceptionCaught(ctx, cause);
  }

  private void fail(ChannelHandlerContext ctx, Throwable cause) {
    for  (Iterator<ClickhouseBinaryCommandCodec<?, ?>> it = inflight.iterator(); it.hasNext();) {
      ClickhouseBinaryCommandCodec<?, ?> codec = it.next();
      it.remove();
      CommandResponse<Object> failure = CommandResponse.failure(cause);
      failure.cmd = (CommandBase) codec.cmd;
      ctx.fireChannelRead(failure);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    fail(ctx, new NoStackTraceThrowable("Fail to read any response from the server, the underlying connection might get lost unexpectedly."));
    super.channelInactive(ctx);
  }
}
