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

package io.vertx.clickhouseclient.binary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PacketUtil {
  public static List<byte[]> filterServerBlocks(Map<String, byte[]> map) {
    return map.entrySet()
      .stream()
      .filter(packet -> !packet.getKey().startsWith("peer0_"))
      .map(Map.Entry::getValue)
      .collect(Collectors.toList());
  }

  public static byte[][] asPrimitiveByteArray(List<byte[]> src) {
    byte[][] ret = new byte[src.size()][];
    for (int i = 0; i < src.size(); ++i) {
      ret[i] = src.get(i);
    }
    return ret;
  }
}
