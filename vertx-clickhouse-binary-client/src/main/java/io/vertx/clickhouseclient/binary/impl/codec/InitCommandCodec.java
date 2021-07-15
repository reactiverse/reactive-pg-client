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
import io.vertx.clickhouseclient.binary.ClickhouseConstants;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.ClickhouseServerException;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.command.CommandResponse;
import io.vertx.sqlclient.impl.command.InitCommand;

public class InitCommandCodec extends ClickhouseBinaryCommandCodec<Connection, InitCommand> {
  private static final Logger LOG = LoggerFactory.getLogger(InitCommandCodec.class);

  private PacketReader packetReader;
  private String fullClientName;

  InitCommandCodec(InitCommand cmd) {
    super(cmd);
  }

  @Override
  void encode(ClickhouseBinaryEncoder encoder) {
    super.encode(encoder);

    ByteBuf buf = allocateBuffer();
    ByteBufUtils.writeULeb128(ClientPacketTypes.HELLO, buf);
    fullClientName = "ClickHouse " + cmd.properties()
      .getOrDefault(ClickhouseConstants.OPTION_APPLICATION_NAME, "vertx-sql");
    ByteBufUtils.writePascalString(fullClientName, buf);
    ByteBufUtils.writeULeb128(ClickhouseConstants.CLIENT_VERSION_MAJOR, buf);
    ByteBufUtils.writeULeb128(ClickhouseConstants.CLIENT_VERSION_MINOR, buf);
    ByteBufUtils.writeULeb128(ClickhouseConstants.CLIENT_REVISION, buf);
    ByteBufUtils.writePascalString(cmd.database(), buf);
    ByteBufUtils.writePascalString(cmd.username(), buf);
    ByteBufUtils.writePascalString(cmd.password(), buf);
    encoder.chctx().writeAndFlush(buf, encoder.chctx().voidPromise());
    if (LOG.isDebugEnabled()) {
      LOG.debug("sent hello packet ");
    }
  }

  @Override
  void decode(ChannelHandlerContext ctx, ByteBuf in) {
    if (packetReader == null) {
      packetReader = new PacketReader(encoder.getConn().getDatabaseMetaData(), fullClientName, cmd.properties(), encoder.getConn().lz4Factory());
    }
    Object packet = packetReader.receivePacket(ctx.alloc(), in);
    if (packet != null) {
      if (packet.getClass() == ClickhouseBinaryDatabaseMetadata.class) {
        ClickhouseBinaryDatabaseMetadata md = (ClickhouseBinaryDatabaseMetadata)packet;
        encoder.getConn().setDatabaseMetadata(md);
        if (LOG.isDebugEnabled()) {
          LOG.debug("connected to server: " + md);
        }
        completionHandler.handle(CommandResponse.success(null));
      } else if (packet.getClass() == ClickhouseServerException.class) {
        ClickhouseServerException exc = (ClickhouseServerException)packet;
        completionHandler.handle(CommandResponse.failure(exc));
      } else {
        String msg = "unknown packet type: " + packet.getClass();
        LOG.error(msg);
        completionHandler.handle(CommandResponse.failure(new RuntimeException(msg)));
      }
      packetReader = null;
    }
  }
}
