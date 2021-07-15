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

import java.util.HashMap;
import java.util.Map;

public enum ServerPacketType {
  //Name, version, revision.
  HELLO(0),

  //A block of data (compressed or not).
  DATA(1),

  //The exception during query execution.
  EXCEPTION(2),

  //Query execution progress: rows read, bytes read.
  PROGRESS( 3),

  //Ping response
  PONG(4),

  //All packets were transmitted
  END_OF_STREAM(5),

  //Packet with profiling info.
  PROFILE_INFO(6),

  //A block with totals (compressed or not).
  TOTALS(7),

  //A block with minimums and maximums (compressed or not).
  EXTREMES(8),

  //A response to TablesStatus request.
  TABLES_STATUS_RESPONSE(9),

  //System logs of the query execution
  LOG(10),

  //Columns' description for default values calculation
  TABLE_COLUMNS(11);

  private static final Map<Integer, ServerPacketType> CODE_INDEX = buildCodeIndex();

  private final int code;
  ServerPacketType(int code) {
    this.code = code;
  }

  public int code() {
    return code;
  }

  @Override
  public String toString() {
    return this.name() + "(" + this.code + ")";
  }

  public static ServerPacketType fromCode(int code) {
    ServerPacketType ret = CODE_INDEX.get(code);
    if (ret == null) {
      throw new IllegalArgumentException("unknown code: " + code + "(" + Integer.toHexString(code) + ")");
    }
    return ret;
  }

  private static Map<Integer, ServerPacketType> buildCodeIndex() {
    Map<Integer, ServerPacketType> ret = new HashMap<>();
    for (ServerPacketType pType : ServerPacketType.values()) {
      ret.put(pType.code(), pType);
    }
    return ret;
  }
}
