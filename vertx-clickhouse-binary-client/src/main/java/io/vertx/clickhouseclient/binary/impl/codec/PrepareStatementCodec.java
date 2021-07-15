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
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryRowDesc;
import io.vertx.sqlclient.desc.ColumnDescriptor;
import io.vertx.sqlclient.impl.PreparedStatement;
import io.vertx.sqlclient.impl.command.CommandResponse;
import io.vertx.sqlclient.impl.command.PrepareStatementCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrepareStatementCodec extends ClickhouseBinaryCommandCodec<PreparedStatement, PrepareStatementCommand> {
  private final QueryInfo queryInfo;
  private final UUID psId;
  private PacketReader packetReader;

  protected PrepareStatementCodec(PrepareStatementCommand cmd, QueryInfo queryInfo) {
    super(cmd);
    this.queryInfo = queryInfo;
    this.psId = UUID.randomUUID();
  }

  @Override
  void encode(ClickhouseBinaryEncoder encoder) {
    super.encode(encoder);
    String sql = cmd.sql();
    boolean realInsertBatch = queryInfo.isInsert() && queryInfo.hasValues();
    if (realInsertBatch) {
      encoder.getConn().lockPsOrThrow(psId);
      String truncatedSql = queryInfo.queryEndingWithValues();
      ByteBuf buf = allocateBuffer();
      try {
        PacketForge forge = new PacketForge(encoder.getConn(), encoder.chctx());
        forge.sendQuery(truncatedSql, buf);
        forge.sendExternalTables(buf, Collections.emptyList());
        encoder.chctx().writeAndFlush(buf, encoder.chctx().voidPromise());
      } catch (Throwable t) {
        buf.release();
        throw t;
      }
    } else {
      completionHandler.handle(CommandResponse.success(new ClickhouseBinaryPreparedStatement(sql, new ClickhouseBinaryParamDesc(Collections.emptyList()),
        new ClickhouseBinaryRowDesc(Collections.emptyList()), queryInfo, false, psId)));
    }
  }

  @Override
  void decode(ChannelHandlerContext ctx, ByteBuf in) {
    if (packetReader == null) {
      packetReader = new PacketReader(encoder.getConn().getDatabaseMetaData(), null, null, encoder.getConn().lz4Factory());
    }
    Object packet = packetReader.receivePacket(ctx.alloc(), in);
    if (packet != null) {
      packetReader = null;
      if (packet.getClass() == TableColumns.class) {
        TableColumns columns = (TableColumns)packet;
        Map<String, ClickhouseBinaryColumnDescriptor> data = columns.columnDefinition().getColumnsWithTypes();

        List<String> columnNames = new ArrayList<>(data.keySet());
        List<ColumnDescriptor> columnTypes = new ArrayList<>(data.values());
        ClickhouseBinaryRowDesc rowDesc = new ClickhouseBinaryRowDesc(columnNames, columnTypes);
        completionHandler.handle(CommandResponse.success(new ClickhouseBinaryPreparedStatement(cmd.sql(),
          new ClickhouseBinaryParamDesc(Collections.emptyList()), rowDesc, queryInfo, true, psId)));
      } else if (packet instanceof Throwable) {
        cmd.fail((Throwable) packet);
      }
    }
  }
}
