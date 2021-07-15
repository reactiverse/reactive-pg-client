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

package io.vertx.clickhouseclient.binary.alltypes;

import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnection;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.Tuple;

import java.util.Arrays;
import java.util.List;

public class StringArrayDeduplicationTester {
  private final String tableName;
  private final ClickhouseBinaryConnectOptions options;
  private final Vertx vertx;

  public StringArrayDeduplicationTester(String tableName, Vertx vertx, ClickhouseBinaryConnectOptions options) {
    this.tableName = tableName;
    this.options = options;
    this.vertx = vertx;
  }

  public void test(TestContext ctx) {
    List<Tuple> batch = Arrays.asList(
      Tuple.of(1, "dedup", (Object) new Object[][][]{ {{"str1_1", "str1_2", null, "dedup3", "dedup1"}, {null}}, {{"str1_3", "str1_4", null}, {null, "dedup2"}} }),
      Tuple.of(2, "val",   (Object) new Object[][][]{ {{"str2_1", "str2_2", null, "dedup2"}, {null, "dedup1"}} }),
      Tuple.of(3, "dedup", (Object) new Object[][][]{ {{"str3_1", "str3_2", null}, {null}, {"dedup3"}} })
    );
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.preparedQuery(String.format("INSERT INTO %s (id, nullable_lc_t, nullable_array3_lc_t) VALUES", tableName)).executeBatch(batch, ctx.asyncAssertSuccess(result -> {
        conn.query("SELECT nullable_lc_t, nullable_array3_lc_t FROM " + tableName + " ORDER BY id").execute(ctx.asyncAssertSuccess(
          res3 -> {
            ctx.assertEquals(res3.size(), batch.size(), "row count mismatch");
            RowIterator<Row> rows = res3.iterator();
            Row row1 = rows.next();
            Row row2 = rows.next();
            Row row3 = rows.next();
            String val1 = row1.getString(0);
            String val2 = row3.getString(0);
            ctx.assertTrue(val1 == val2);
            String[][][] arr1 = row1.get(String[][][].class, 1);
            String[][][] arr2 = row2.get(String[][][].class, 1);
            String[][][] arr3 = row3.get(String[][][].class, 1);
            ctx.assertTrue(arr1[0][0][3] == arr3[0][2][0]);//dedup3
            ctx.assertTrue(arr1[0][0][4] == arr2[0][1][1]);//dedup1
            ctx.assertTrue(arr1[1][1][1] == arr2[0][0][3]);//dedup2
          }));
      }));
    }));
  }
}
