package io.vertx.mysqlclient.impl.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.vertx.sqlclient.impl.command.*;

import java.util.ArrayDeque;

import static io.vertx.mysqlclient.impl.protocol.CapabilitiesFlag.*;

class MySQLEncoder extends ChannelOutboundHandlerAdapter {

  private final ArrayDeque<CommandCodec<?, ?>> inflight;
  ChannelHandlerContext chctx;

  int clientCapabilitiesFlag = 0x00000000;

  MySQLEncoder(ArrayDeque<CommandCodec<?, ?>> inflight) {
    this.inflight = inflight;
    initSupportedCapabilitiesFlags();
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    chctx = ctx;
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (msg instanceof CommandBase<?>) {
      CommandBase<?> cmd = (CommandBase<?>) msg;
      write(cmd);
    } else {
      super.write(ctx, msg, promise);
    }
  }

  void write(CommandBase<?> cmd) {
    CommandCodec<?, ?> codec = wrap(cmd);
    codec.completionHandler = resp -> {
      CommandCodec c = inflight.poll();
      resp.cmd = (CommandBase) c.cmd;
      chctx.fireChannelRead(resp);
    };
    inflight.add(codec);
    codec.encode(this);
  }

  private CommandCodec<?, ?> wrap(CommandBase<?> cmd) {
    if (cmd instanceof InitCommand) {
      return new InitCommandCodec((InitCommand) cmd);
    } else if (cmd instanceof SimpleQueryCommand) {
      return new SimpleQueryCommandCodec((SimpleQueryCommand) cmd);
    } else if (cmd instanceof ExtendedQueryCommand) {
      return new ExtendedQueryCommandCodec((ExtendedQueryCommand) cmd);
    } else if (cmd instanceof CloseConnectionCommand) {
      return new CloseConnectionCommandCodec((CloseConnectionCommand) cmd);
    } else if (cmd instanceof PrepareStatementCommand) {
      return new PrepareStatementCodec((PrepareStatementCommand) cmd);
    } else if (cmd instanceof CloseStatementCommand) {
      return new CloseStatementCommandCodec((CloseStatementCommand) cmd);
    } else if (cmd instanceof CloseCursorCommand) {
      return new ResetStatementCommandCodec((CloseCursorCommand) cmd);
    } else {
      System.out.println("Unsupported command " + cmd);
      throw new UnsupportedOperationException("Todo");
    }
  }

  private void initSupportedCapabilitiesFlags() {
    clientCapabilitiesFlag |= CLIENT_PLUGIN_AUTH;
    clientCapabilitiesFlag |= CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA;
    clientCapabilitiesFlag |= CLIENT_SECURE_CONNECTION;
    clientCapabilitiesFlag |= CLIENT_PROTOCOL_41;
    clientCapabilitiesFlag |= CLIENT_DEPRECATE_EOF;
    clientCapabilitiesFlag |= CLIENT_TRANSACTIONS;
    clientCapabilitiesFlag |= CLIENT_MULTI_STATEMENTS;
    clientCapabilitiesFlag |= CLIENT_MULTI_RESULTS;
  }
}
