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
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinarySocketConnection;
import io.vertx.sqlclient.impl.command.CloseCursorCommand;
import io.vertx.sqlclient.impl.command.CommandResponse;

public class CloseCursorCommandCodec extends ClickhouseBinaryCommandCodec<Void, CloseCursorCommand> {
  private final ClickhouseBinarySocketConnection conn;

  protected CloseCursorCommandCodec(CloseCursorCommand cmd, ClickhouseBinarySocketConnection conn) {
    super(cmd);
    this.conn = conn;
  }

  void encode(ClickhouseBinaryEncoder encoder) {
    conn.releaseCursor(((ClickhouseBinaryPreparedStatement)cmd.statement()).getPsId(), cmd.id());
    super.encode(encoder);
    completionHandler.handle(CommandResponse.success(null));
  }

  @Override
  void decode(ChannelHandlerContext ctx, ByteBuf in) {
  }
}

