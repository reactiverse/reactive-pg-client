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

import io.vertx.clickhouseclient.binary.ClickhouseResource;
import io.vertx.clickhouseclient.binary.Sleep;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.tck.SimpleQueryTestBase;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ClickhouseBinarySimpleQueryPooledTest extends SimpleQueryTestBase {

  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  @Override
  protected void initConnector() {
    connector = ClientConfig.POOLED.connect(vertx, rule.options());
  }

  @Ignore
  @Test
  public void testDelete(TestContext ctx) {
    //no way to return count of altered rows
  }

  @Ignore
  @Test
  public void testInsert(TestContext ctx) {
    //no way to return count of inserted rows (can be emulated)
  }

  @Ignore
  @Test
  public void testUpdate(TestContext ctx) {
    //no way to return count of altered rows
  }

  @Override
  protected void cleanTestTable(TestContext ctx) {
    super.cleanTestTable(ctx);
    Sleep.sleepOrThrow();
  }
}
