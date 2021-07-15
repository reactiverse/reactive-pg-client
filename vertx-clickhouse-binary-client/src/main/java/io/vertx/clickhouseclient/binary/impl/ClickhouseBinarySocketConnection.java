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

package io.vertx.clickhouseclient.binary.impl;

import io.netty.channel.ChannelPipeline;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryCodec;
import io.vertx.core.Promise;
import io.vertx.core.impl.EventLoopContext;
import io.vertx.core.net.impl.NetSocketInternal;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.SocketConnectionBase;
import io.vertx.sqlclient.impl.command.InitCommand;
import net.jpountz.lz4.LZ4Factory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class ClickhouseBinarySocketConnection extends SocketConnectionBase {
  private ClickhouseBinaryCodec codec;
  private ClickhouseBinaryDatabaseMetadata md;
  private UUID psId;
  private String ourCursorId;
  private final LZ4Factory lz4Factory;


  public ClickhouseBinarySocketConnection(NetSocketInternal socket,
                                          boolean cachePreparedStatements,
                                          int preparedStatementCacheSize,
                                          Predicate<String> preparedStatementCacheSqlFilter,
                                          EventLoopContext context,
                                          LZ4Factory lz4Factory) {
    super(socket, cachePreparedStatements, preparedStatementCacheSize, preparedStatementCacheSqlFilter, 1, context);
    this.lz4Factory = lz4Factory;
  }

  @Override
  public void init() {
    codec = new ClickhouseBinaryCodec(this);
    ChannelPipeline pipeline = socket.channelHandlerContext().pipeline();
    pipeline.addBefore("handler", "codec", codec);
    super.init();
  }

  void sendStartupMessage(String username, String password, String database, Map<String, String> properties, Promise<Connection> completionHandler) {
    InitCommand cmd = new InitCommand(this, username, password, database, properties);
    schedule(context, cmd).onComplete(completionHandler);
  }

  public void setDatabaseMetadata(ClickhouseBinaryDatabaseMetadata md) {
    this.md = md;
  }

  public void lockPsOrThrow(UUID newPsId) {
    if (psId == null) {
      psId = newPsId;
    } else {
      if (newPsId != null) {
        if (!Objects.equals(psId, newPsId)) {
          throw new IllegalStateException("attempt to block blocked (" + psId + ") connection by ps" + newPsId);
        }
      }
    }
  }

  public void lockCursorOrThrow(UUID psId, String newCursorId) {
    lockPsOrThrow(psId);
    if (ourCursorId == null) {
      ourCursorId = newCursorId;
    } else {
      if (newCursorId != null) {
        if (!Objects.equals(ourCursorId, newCursorId)) {
          throw new IllegalStateException("attempt to block blocked (" + ourCursorId + ") connection by cursor " + newCursorId);
        }
      }
    }
  }

  public void releaseCursor(UUID psId, String newCursorId) {
    if (!Objects.equals(this.ourCursorId, newCursorId)) {
      throw new IllegalStateException("can't release: pending cursor = " + ourCursorId + "; provided: " + newCursorId);
    }
    this.ourCursorId = null;
  }

  public void releasePs(UUID newPs) {
    if (!Objects.equals(this.psId, newPs)) {
      throw new IllegalStateException("can't release: pending cursor = " + psId + "; provided: " + newPs);
    }
    this.psId = null;
  }

  public void throwExceptionIfCursorIsBusy(String callerId) {
    if (ourCursorId != null) {
      if (!Objects.equals(ourCursorId, callerId)) {
        throw new IllegalStateException("connection is busy with " + ourCursorId);
      }
    }
  }

  @Override
  public ClickhouseBinaryDatabaseMetadata getDatabaseMetaData() {
    return md;
  }

  public LZ4Factory lz4Factory() {
    return lz4Factory;
  }
}
