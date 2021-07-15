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

import io.vertx.clickhouseclient.binary.ClickhouseConstants;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import io.vertx.clickhouseclient.binary.ClickhouseResource;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.spi.DatabaseMetadata;
import io.vertx.sqlclient.tck.ConnectionTestBase;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ClickhouseBinaryConnectionTest extends ConnectionTestBase {
  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  @Rule
  public TestName name = new TestName();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    options = new ClickhouseBinaryConnectOptions(rule.options());
    options.addProperty(ClickhouseConstants.OPTION_APPLICATION_NAME,
      ClickhouseBinaryPreparedQueryCachedTest.class.getSimpleName() + "." + name.getMethodName());
    connector = ClientConfig.CONNECT.connect(vertx, options);
  }

  @Override
  public void tearDown(TestContext ctx) {
    connector.close();
    super.tearDown(ctx);
  }

  @Override
  protected void validateDatabaseMetaData(TestContext ctx, DatabaseMetadata md) {
    ctx.assertTrue(md.majorVersion() >= 20);
    ctx.assertTrue(md.productName().toLowerCase().contains("ClickHouse".toLowerCase()));
  }
}
