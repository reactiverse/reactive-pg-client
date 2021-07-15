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
import io.netty.channel.ChannelHandlerContext;
import io.vertx.core.Handler;
import io.vertx.sqlclient.impl.command.CommandBase;
import io.vertx.sqlclient.impl.command.CommandResponse;

abstract class ClickhouseBinaryCommandCodec<R, C extends CommandBase<R>> {
  protected ClickhouseBinaryEncoder encoder;
  protected Handler<? super CommandResponse<R>> completionHandler;
  protected final C cmd;

  protected ClickhouseBinaryCommandCodec(C cmd) {
    this.cmd = cmd;
  }

  void encode(ClickhouseBinaryEncoder encoder) {
    this.encoder = encoder;
  }

  abstract void decode(ChannelHandlerContext ctx, ByteBuf in);

  ByteBuf allocateBuffer() {
    return encoder.chctx().alloc().ioBuffer();
  }

  ByteBuf allocateBuffer(int capacity) {
    return encoder.chctx().alloc().ioBuffer(capacity);
  }
}
