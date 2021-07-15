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
import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public interface ClickhouseStreamDataSource {
  Logger LOG = LoggerFactory.getLogger(ClickhouseStreamDataSource.class);

  void moreData(ByteBuf buf, ByteBufAllocator ctx);
  int readableBytes();
  void skipBytes(int length);
  String readPascalString();
  Integer readULeb128();
  boolean readBoolean();
  int readIntLE();
  long readLongLE();
  short readShortLE();
  float readFloatLE();
  double readDoubleLE();
  ByteBuf readSlice(int nBytes);
  void readBytes(byte[] dst);
  byte readByte();
  String hexdump();
  void finish();
}
