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
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.tck.PreparedBatchTestBase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(VertxUnitRunner.class)
public class ClickhouseBinaryPreparedBatchTest extends PreparedBatchTestBase {
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
  protected void initConnector() {
    connector = ClientConfig.CONNECT.connect(vertx, rule.options());
  }

  @Test
  public void testBatchQuery(TestContext ctx) {
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      List<Tuple> batch = new ArrayList<>();
      batch.add(Tuple.of(1));
      batch.add(Tuple.of(3));
      batch.add(Tuple.of(5));
      //select multi-batches are not supported
      conn.preparedQuery(statement("SELECT * FROM immutable WHERE id=", "")).executeBatch(batch, ctx.asyncAssertFailure());
    }));
  }

  @Override
  protected int expectedInsertBatchSize(List<Tuple> batch) {
    return batch.size();
  }

  @Override
  protected void maybeSleep() {
    Sleep.sleepOrThrow();
  }
}
