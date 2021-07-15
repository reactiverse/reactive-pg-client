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

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryConnectionUriParser;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.SqlConnectOptions;

@DataObject(generateConverter = true)
public class ClickhouseBinaryConnectOptions extends SqlConnectOptions {

  public static ClickhouseBinaryConnectOptions fromUri(String connectionUri) throws IllegalArgumentException {
    JsonObject parsedConfiguration = ClickhouseBinaryConnectionUriParser.parse(connectionUri);
    return new ClickhouseBinaryConnectOptions(parsedConfiguration);
  }

  public ClickhouseBinaryConnectOptions() {
    super();
  }

  public ClickhouseBinaryConnectOptions(JsonObject json) {
    super(json);
    ClickhouseBinaryConnectOptionsConverter.fromJson(json, this);
  }

  public ClickhouseBinaryConnectOptions(SqlConnectOptions other) {
    super(other);
  }

  public ClickhouseBinaryConnectOptions(ClickhouseBinaryConnectOptions other) {
    super(other);
  }

}
