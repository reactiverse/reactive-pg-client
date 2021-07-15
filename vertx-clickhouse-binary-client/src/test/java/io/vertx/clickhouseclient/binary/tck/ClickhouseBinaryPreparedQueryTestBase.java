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
import io.vertx.ext.unit.TestContext;
import io.vertx.sqlclient.tck.PreparedQueryTestBase;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

public abstract class ClickhouseBinaryPreparedQueryTestBase extends PreparedQueryTestBase {
  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  @Override
  protected String statement(String... parts) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      if (i > 0) {
        sb.append("$").append((i));
      }
      sb.append(parts[i]);
    }
    return sb.toString();
  }

  @Override
  @Test
  @Ignore
  public void testQueryCursor(TestContext ctx) {
    //TODO cursor support
    super.testQueryCursor(ctx);
  }

  @Override
  @Test
  @Ignore
  public void testQueryCloseCursor(TestContext ctx) {
    //TODO cursor support
    super.testQueryCloseCursor(ctx);
  }

  @Override
  @Test
  @Ignore
  public void testQueryStreamCloseCursor(TestContext ctx) {
    //TODO cursor support
    super.testQueryStreamCloseCursor(ctx);
  }

  @Override
  @Test
  @Ignore
  public void testStreamQueryPauseInBatch(TestContext ctx) {
    // TODO streaming support
    super.testStreamQueryPauseInBatch(ctx);
  }

  @Override
  @Test
  @Ignore
  public void testStreamQueryPauseInBatchFromAnotherThread(TestContext ctx) {
    // TODO streaming support
    super.testStreamQueryPauseInBatchFromAnotherThread(ctx);
  }

  @Override
  @Test
  @Ignore
  public void testStreamQueryPauseResume(TestContext ctx) {
    // TODO streaming support
    super.testStreamQueryPauseResume(ctx);
  }

  @Override
  @Test
  @Ignore
  public void testStreamQuery(TestContext ctx) {
    // TODO streaming support
    super.testStreamQuery(ctx);
  }

  @Override
  @Test
  @Ignore
  public void testPreparedQueryParamCoercionTypeError(TestContext ctx) {
    //no real prepared selects support
  }

  @Override
  @Ignore
  @Test
  public void testPrepareError(TestContext ctx) {
    //no real prepared selects support
  }

  @Override
  @Ignore
  @Test
  public void testPreparedUpdateWithNullParams(TestContext ctx) {
    //no real prepared selects support
  }

  @Override
  @Ignore
  @Test
  public void testPreparedUpdate(TestContext ctx) {
    //Clickhouse does not return real affected row count
  }

  @Override
  @Ignore
  @Test
  public void testPreparedUpdateWithParams(TestContext ctx) {
    //Clickhouse does not return real affected row count
  }
}
