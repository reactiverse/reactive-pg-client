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
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinarySocketConnection;
import io.vertx.clickhouseclient.binary.impl.ColumnOrientedBlock;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.impl.command.CommandResponse;
import io.vertx.sqlclient.impl.command.QueryCommandBase;

import java.util.Collections;
import java.util.Map;

public class SimpleQueryCommandCodec<T> extends ClickhouseBinaryQueryCommandBaseCodec<T, QueryCommandBase<T>> {
  private static final Logger LOG = LoggerFactory.getLogger(SimpleQueryCommandCodec.class);
  private final boolean commandRequiresUpdatesDelivery;
  protected final QueryInfo queryInfo;
  protected final int batchSize;

  private RowResultDecoder<?, T> rowResultDecoder;
  private PacketReader packetReader;
  private int dataPacketNo;
  protected final ClickhouseBinarySocketConnection conn;

  protected SimpleQueryCommandCodec(QueryCommandBase<T> cmd, ClickhouseBinarySocketConnection conn) {
    this(null, 0, cmd, conn, false);
  }
  protected SimpleQueryCommandCodec(QueryInfo queryInfo, int batchSize, QueryCommandBase<T> cmd, ClickhouseBinarySocketConnection conn, boolean requireUpdatesDelivery) {
    super(cmd);
    this.queryInfo = queryInfo;
    this.batchSize = batchSize;
    this.conn = conn;
    this.commandRequiresUpdatesDelivery = requireUpdatesDelivery;
   }

  @Override
  void encode(ClickhouseBinaryEncoder encoder) {
    checkIfBusy();
    super.encode(encoder);
    if (!isSuspended()) {
      ByteBuf buf = allocateBuffer();
      try {
        PacketForge forge = new PacketForge(conn, encoder.chctx());
        forge.sendQuery(sql(), buf);
        forge.sendExternalTables(buf, Collections.emptyList());
        encoder.chctx().writeAndFlush(buf, encoder.chctx().voidPromise());
      } catch (Throwable t) {
        buf.release();
        throw t;
      }
    }
  }

  protected String sql() {
    return cmd.sql();
  }

  protected Map<String, String> settings() {
    return conn.getDatabaseMetaData().getProperties();
  }

  protected boolean isSuspended() {
    return false;
  }

  protected void checkIfBusy() {
    conn.throwExceptionIfCursorIsBusy(null);
  }

  @Override
  void decode(ChannelHandlerContext ctx, ByteBuf in) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("decode, readable bytes: " + in.readableBytes());
    }
    if (packetReader == null) {
      packetReader = new PacketReader(encoder.getConn().getDatabaseMetaData(), null, null, encoder.getConn().lz4Factory());
    }
    Object packet = packetReader.receivePacket(ctx.alloc(), in);
    if (packet != null) {
      if (packet.getClass() == ColumnOrientedBlock.class) {
        ColumnOrientedBlock block = (ColumnOrientedBlock)packet;
        if (LOG.isDebugEnabled()) {
          LOG.debug("decoded packet " + dataPacketNo + ": " + block + " row count " + block.numRows());
        }
        if (dataPacketNo == 0) {
          ClickhouseBinaryRowDesc rowDesc = block.rowDesc();
          rowResultDecoder = new RowResultDecoder<>(cmd.collector(), rowDesc, conn.getDatabaseMetaData());
        }
        packetReader = null;
        rowResultDecoder.generateRows(block);
        if (commandRequiresUpdatesDelivery && block.numRows() > 0) {
          notifyOperationUpdate(true, null);
        }
        ++dataPacketNo;
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.error("non-data packet type: " + packet.getClass());
        }
        if (packet instanceof Throwable) {
          Throwable t = (Throwable) packet;
          LOG.error("unknown packet type or server exception", t);
          notifyOperationUpdate(false, t);
        }
      }
    } else if (packetReader.isEndOfStream()) {
      notifyOperationUpdate(false, null);
      packetReader = null;
    }
  }

  private void notifyOperationUpdate(boolean hasMoreResults, Throwable t) {
    notifyOperationUpdate(0, hasMoreResults, t);
  }

  private void notifyOperationUpdate(int updateCount, boolean hasMoreResults, Throwable t) {
    Throwable failure = null;
    if (rowResultDecoder != null) {
      failure = rowResultDecoder.complete();
      T result = rowResultDecoder.result();
      int size = rowResultDecoder.size();
      rowResultDecoder.reset();
      if (LOG.isDebugEnabled()) {
        LOG.debug("notifying operation update; has more result = " + hasMoreResults + "; size: " + size);
      }
      cmd.resultHandler().handleResult(updateCount, size, rowResultDecoder.getRowDesc(), result, failure);
    } else {
      if (queryInfo != null && queryInfo.isInsert()) {
        rowResultDecoder = new RowResultDecoder<>(cmd.collector(), ClickhouseBinaryRowDesc.EMPTY, conn.getDatabaseMetaData());
        failure = rowResultDecoder.complete();
        cmd.resultHandler().handleResult(batchSize, 0, ClickhouseBinaryRowDesc.EMPTY, rowResultDecoder.result(), failure);
      }
    }
    if (t != null) {
      if (failure == null) {
        failure = t;
      } else {
        failure = new RuntimeException(failure);
        failure.addSuppressed(t);
      }
    }

    CommandResponse<Boolean> response;
    if (failure == null) {
      response = CommandResponse.success(hasMoreResults);
    } else {
      response = CommandResponse.failure(failure);
    }
    completionHandler.handle(response);
  }
}
