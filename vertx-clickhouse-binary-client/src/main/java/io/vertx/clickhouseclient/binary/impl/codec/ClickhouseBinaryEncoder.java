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
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinarySocketConnection;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.impl.command.*;

import java.util.ArrayDeque;

public class ClickhouseBinaryEncoder extends ChannelOutboundHandlerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(ClickhouseBinaryEncoder.class);

  private final ArrayDeque<ClickhouseBinaryCommandCodec<?, ?>> inflight;
  private final ClickhouseBinarySocketConnection conn;

  private ChannelHandlerContext chctx;

  public ClickhouseBinaryEncoder(ArrayDeque<ClickhouseBinaryCommandCodec<?, ?>> inflight, ClickhouseBinarySocketConnection conn) {
      this.inflight = inflight;
      this.conn = conn;
  }

  ClickhouseBinarySocketConnection getConn() {
    return conn;
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    chctx = ctx;
  }

  ChannelHandlerContext chctx() {
    return chctx;
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    CommandBase<?> cmd = null;
    try {
      if (msg instanceof CommandBase<?>) {
        cmd = (CommandBase<?>) msg;
        write(cmd);
      } else {
        super.write(ctx, msg, promise);
      }
    } catch (Throwable t) {
      deliverError(cmd, t);
      throw t;
    }
  }

  void write(CommandBase<?> cmd) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("got command: " + cmd.getClass());
    }
    ClickhouseBinaryCommandCodec<?, ?> codec = wrap(cmd);
    codec.completionHandler = resp -> {
      ClickhouseBinaryCommandCodec<?, ?> c = inflight.poll();
      resp.cmd = (CommandBase) c.cmd;
      chctx.fireChannelRead(resp);
    };
    inflight.add(codec);
    codec.encode(this);
  }

  private ClickhouseBinaryCommandCodec<?, ?> wrap(CommandBase<?> cmd) {
    if (cmd instanceof InitCommand) {
      return new InitCommandCodec((InitCommand) cmd);
    } else if (cmd instanceof SimpleQueryCommand) {
      return new SimpleQueryCommandCodec<>((SimpleQueryCommand<?>) cmd, conn);
    } else if (cmd instanceof CloseConnectionCommand) {
      return new CloseConnectionCommandCodec((CloseConnectionCommand)cmd);
    } else if (cmd instanceof PrepareStatementCommand) {
      PrepareStatementCommand ps = (PrepareStatementCommand) cmd;
      QueryInfo queryInfo = QueryInfo.parse(ps.sql());
      return new PrepareStatementCodec(ps, queryInfo);
    } else if (cmd instanceof ExtendedQueryCommand) {
      ExtendedQueryCommand<?> ecmd = (ExtendedQueryCommand<?>) cmd;
      QueryInfo queryInfo;
      if (ecmd.preparedStatement() != null) {
        queryInfo = ((ClickhouseBinaryPreparedStatement) ecmd.preparedStatement()).queryInfo();
      } else {
        queryInfo = QueryInfo.parse(ecmd.sql());
      }
      if (queryInfo != null && !queryInfo.isInsert() && ecmd.isBatch() && ecmd.paramsList() != null && ecmd.paramsList().size() > 1) {
        RuntimeException ex = new UnsupportedOperationException("batch queries are supported for INSERTs only");
        deliverError(cmd, ex);
      }
      return new ExtendedQueryCommandCodec<>(queryInfo, ecmd, conn);
    } else if (cmd instanceof CloseCursorCommand) {
      return new CloseCursorCommandCodec((CloseCursorCommand)cmd, conn);
    } else if (cmd instanceof CloseStatementCommand) {
      return new CloseStatementCommandCodec((CloseStatementCommand) cmd, conn);
    }
    RuntimeException ex = new UnsupportedOperationException(cmd.getClass().getName());
    deliverError(cmd, ex);
    throw ex;
  }

  private void deliverError(CommandBase cmd, Throwable ex) {
    if (cmd instanceof QueryCommandBase) {
      QueryCommandBase ecmd = (QueryCommandBase)cmd;
      ecmd.resultHandler().handleResult(0, 0, null, null, ex);
    }
    CommandResponse<Object> resp = CommandResponse.failure(ex);
    resp.cmd = cmd;
    chctx.fireChannelRead(resp);
  }
}
