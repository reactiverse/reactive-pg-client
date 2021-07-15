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

package io.vertx.clickhouseclient.binary.tck;

import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import io.vertx.clickhouseclient.binary.ClickhouseResource;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.tck.ConnectionAutoRetryTestBase;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ClickhouseBinaryConnectionAutoRetryTest extends ConnectionAutoRetryTestBase {
  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    options = rule.options();
  }

  @Override
  public void tearDown(TestContext ctx) {
    connectionConnector.close();
    poolConnector.close();
    super.tearDown(ctx);
  }

  @Override
  protected void initialConnector(int proxyPort) {
    SqlConnectOptions proxyOptions = new ClickhouseBinaryConnectOptions(options);
    proxyOptions.setPort(proxyPort);
    proxyOptions.setHost("localhost");
    connectionConnector = ClientConfig.CONNECT.connect(vertx, proxyOptions);
    poolConnector = ClientConfig.POOLED.connect(vertx, proxyOptions);
  }
}
