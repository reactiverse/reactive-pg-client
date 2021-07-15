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
import java.util.ArrayList;
import java.util.List;

public class MultistringMessageReader {
  private final List<String> strings;
  private final Charset charset;
  private Integer stringsExpected;

  public MultistringMessageReader(Charset charset) {
    this.charset = charset;
    strings = new ArrayList<>();
  }

  public List<String> readFrom(ByteBuf in, ServerPacketType packetType) {
    if (stringsExpected == null) {
      stringsExpected = stringsInMessage(packetType);
    }
    String ln;
    while (strings.size() < stringsExpected && (ln = ByteBufUtils.readPascalString(in, charset)) != null) {
      strings.add(ln);
    }
    if (strings.size() == stringsExpected) {
      return strings;
    }
    return null;
  }

  private int stringsInMessage(ServerPacketType type) {
    if (type == ServerPacketType.TABLE_COLUMNS) {
      return 2;
    }
    return 0;
  }
}
