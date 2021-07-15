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

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowStream;
import io.vertx.sqlclient.impl.ArrayTuple;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.LongAdder;

@RunWith(VertxUnitRunner.class)
public class ExtendedClickhouseTest {
  private static final Logger LOG = LoggerFactory.getLogger(ExtendedClickhouseTest.class);

  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  private ClickhouseBinaryConnectOptions options;
  private Vertx vertx;
  private String query;

  @Before
  public void setup(TestContext ctx) {
    options = rule.options();
    vertx = Vertx.vertx();
  }

  @After
  public void teardDown(TestContext ctx) {
    vertx.close(ctx.asyncAssertSuccess());
  }

  @Test
  public void extendedQueryTest(TestContext ctx) {
    Async async = ctx.async();
    LongAdder adder = new LongAdder();
    final long limit = 55;
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      query = String.format("select name, value from (SELECT name, value from vertx_cl_test_table limit %s) t1 order by name desc", limit);
      conn
        .prepare(query, ctx.asyncAssertSuccess(ps -> {
          RowStream<Row> stream = ps.createStream(50, ArrayTuple.EMPTY);
          stream.exceptionHandler(err -> {
            LOG.error("exceptionHandler: ", err);
          });
          stream.endHandler(v -> {
            LOG.info("got End of stream");
            ctx.assertEquals(limit, adder.sum());
            async.complete();
          });
          stream.handler(row -> {
            adder.increment();
            //LOG.info("name: " + row.getString("name") + "; value: " + row.getLong("value"));
          });
        }));
    }));
  }
}
