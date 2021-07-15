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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.impl.command.CloseConnectionCommand;

public class CloseConnectionCommandCodec extends ClickhouseBinaryCommandCodec<Void, CloseConnectionCommand> {
  private static final Logger LOG = LoggerFactory.getLogger(CloseConnectionCommandCodec.class);

  protected CloseConnectionCommandCodec(CloseConnectionCommand cmd) {
    super(cmd);
  }

  @Override
  void decode(ChannelHandlerContext ctx, ByteBuf in) {
  }

  @Override
  public void encode(ClickhouseBinaryEncoder encoder) {
    super.encode(encoder);
    if (LOG.isDebugEnabled()) {
      LOG.debug("closing channel");
    }
    ChannelHandlerContext ctx = encoder.chctx();
    SocketChannel channel = (SocketChannel) ctx.channel();
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(v -> channel.shutdownOutput());
  }
}
