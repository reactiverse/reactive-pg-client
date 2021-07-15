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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ByteBufUtils {
  public static void writeULeb128(int value, ByteBuf buf) {
    assert (value >= 0);
    int remaining = value >>> 7;
    while (remaining != 0) {
      buf.writeByte((byte) ((value & 0x7f) | 0x80));
      value = remaining;
      remaining >>>= 7;
    }
    buf.writeByte((byte) (value & 0x7f));
  }

  public static Integer readULeb128(ByteBuf buf) {
    int value = 0;
    int read = 0;
    int count = 0;
    int readerIndex = buf.readerIndex();
    boolean notEnoughData = false;
    do {
      if (buf.readableBytes() >= 1) {
        read = buf.readByte() & 0xff;
        value |= (read & 0x7f) << (count * 7);
        count++;
      } else {
        notEnoughData = true;
        break;
      }
    } while (((read & 0x80) == 0x80) && count < 5);

    if (notEnoughData) {
      buf.readerIndex(readerIndex);
      return null;
    }
    if ((read & 0x80) == 0x80) {
      buf.readerIndex(readerIndex);
      throw new RuntimeException("invalid LEB128 sequence");
    }
    return value;
  }

  public static String readPascalString(ByteBuf buf, Charset charset) {
    int readerIndex = buf.readerIndex();
    Integer length = readULeb128(buf);
    if (length == null) {
      return null;
    }
    if (buf.readableBytes() >= length) {
      byte[] b = new byte[length];
      buf.readBytes(b);
      return new String(b, charset);
    }
    buf.readerIndex(readerIndex);
    return null;
  }

  public static void writePascalString(String str, ByteBuf buf) {
    byte[] b = str.getBytes(StandardCharsets.UTF_8);
    writeULeb128(b.length, buf);
    buf.writeBytes(b);
  }
}
