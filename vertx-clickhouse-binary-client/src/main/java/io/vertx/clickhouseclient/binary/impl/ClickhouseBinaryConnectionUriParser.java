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

import io.vertx.core.json.JsonObject;

import java.net.URI;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleImmutableEntry;

public class ClickhouseBinaryConnectionUriParser {
  public static JsonObject parse(String connectionUri) {
    try {
      JsonObject configuration = new JsonObject();
      URI location = URI.create(connectionUri);
      String userInfo = location.getUserInfo();
      String user = userInfo;
      String password = "";
      if (userInfo.contains(":")) {
        String[] tokens = userInfo.split(":");
        user = tokens[0];
        password = tokens[1];
      }
      configuration.put("user", user);
      configuration.put("password", password);
      configuration.put("host", location.getHost());
      int port = location.getPort();
      if (port == -1) {
        port = 9000;
      }
      configuration.put("port", port);
      String path = location.getPath();
      int startDbOffset = path.startsWith("/") ? 1 : 0;
      int endLocOffset = path.endsWith("/") && path.length() >= 2 ? 1 : 0;
      path = path.substring(startDbOffset, path.length() - endLocOffset);
      configuration.put("database", path);

      configuration.put("properties", queryAsMap(location.getQuery()));

      return configuration;
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot parse invalid connection URI: " + connectionUri, e);
    }
  }

  public static Map<String, String> queryAsMap(String query) {
    if (query == null || query.isEmpty()) {
      return Collections.emptyMap();
    }
    return Arrays.stream(query.split("&"))
      .map(ClickhouseBinaryConnectionUriParser::asEntry)
      .collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue));
  }

  public static AbstractMap.SimpleImmutableEntry<String, String> asEntry(String str) {
    int idx = str.indexOf("=");
    String key = idx > 0 ? str.substring(0, idx) : str;
    String value = idx > 0 && str.length() > idx + 1 ? str.substring(idx + 1) : null;
    return new AbstractMap.SimpleImmutableEntry<>(key, value);
  }
}
