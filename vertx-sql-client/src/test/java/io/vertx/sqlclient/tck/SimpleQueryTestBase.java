/*
 * Copyright (C) 2017 Julien Viet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.vertx.sqlclient.tck;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class SimpleQueryTestBase {

  protected Vertx vertx;
  protected Connector<SqlConnection> connector;

  private static void insertIntoTestTable(TestContext ctx, SqlClient client, int amount, Runnable completionHandler) {
    AtomicInteger count = new AtomicInteger();
    for (int i = 0; i < 10; i++) {
      client
        .query("INSERT INTO mutable (id, val) VALUES (" + i + ", 'Whatever-" + i + "')")
        .execute(ctx.asyncAssertSuccess(r1 -> {
        ctx.assertEquals(1, r1.rowCount());
        if (count.incrementAndGet() == amount) {
          completionHandler.run();
        }
      }));
    }
  }

  protected abstract void initConnector();

  protected void connect(Handler<AsyncResult<SqlConnection>> handler) {
    connector.connect(handler);
  }

  @Before
  public void setUp(TestContext ctx) throws Exception {
    vertx = Vertx.vertx();
    initConnector();
    cleanTestTable(ctx);
  }

  @After
  public void tearDown(TestContext ctx) {
    connector.close();
    vertx.close(ctx.asyncAssertSuccess());
  }

  @Test
  public void testQuery(TestContext ctx) {
    connect(ctx.asyncAssertSuccess(conn -> {
      conn
        .query("SELECT id, message FROM immutable ORDER BY id")
        .execute(ctx.asyncAssertSuccess(result -> {
        //TODO we need to figure how to handle PgResult#rowCount() method in common API,
        // MySQL returns affected rows as 0 for SELECT query but Postgres returns queried amount
        // ctx.assertEquals(12, result.rowCount()); this line does not pass in MySQL but passes in PG
        ctx.assertEquals(12, result.size());
        Tuple row = result.iterator().next();
        ctx.assertEquals(1, row.getInteger(0));
        ctx.assertEquals("fortune: No such file or directory", row.getString(1));
      }));
    }));
  }

  @Test
  public void testQueryError(TestContext ctx) {
    connect(ctx.asyncAssertSuccess(conn -> {
      conn.query("SELECT whatever from DOES_NOT_EXIST").execute(ctx.asyncAssertFailure(err -> {
      }));
    }));
  }

  @Test
  public void testUpdate(TestContext ctx) {
    Async async = ctx.async();
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn.query("INSERT INTO mutable (id, val) VALUES (1, 'Whatever')").execute(ctx.asyncAssertSuccess(r1 -> {
        ctx.assertEquals(1, r1.rowCount());
        conn.query("UPDATE mutable SET val = 'newValue' WHERE id = 1").execute(ctx.asyncAssertSuccess(r2 -> {
          ctx.assertEquals(1, r2.rowCount());
          async.complete();
        }));
      }));
    }));
  }

  @Test
  public void testInsert(TestContext ctx) {
    Async async = ctx.async();
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn.query("INSERT INTO mutable (id, val) VALUES (1, 'Whatever');").execute(ctx.asyncAssertSuccess(r1 -> {
        ctx.assertEquals(1, r1.rowCount());
        async.complete();
      }));
    }));
    async.await();
  }

  @Test
  public void testDelete(TestContext ctx) {
    Async async = ctx.async();
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      insertIntoTestTable(ctx, conn, 10, () -> {
        conn.query("DELETE FROM mutable where id = 6").execute(ctx.asyncAssertSuccess(result -> {
          ctx.assertEquals(1, result.rowCount());
          async.complete();
        }));
      });
    }));
  }

  protected void cleanTestTable(TestContext ctx) {
    connect(ctx.asyncAssertSuccess(conn -> {
      conn.query("TRUNCATE TABLE mutable;").execute(ctx.asyncAssertSuccess(result -> {
        conn.close();
      }));
    }));
  }
}
