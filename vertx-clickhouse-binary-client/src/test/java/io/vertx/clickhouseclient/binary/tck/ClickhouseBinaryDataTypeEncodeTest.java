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
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.data.Numeric;
import io.vertx.sqlclient.tck.BinaryDataTypeEncodeTestBase;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.JDBCType;

@RunWith(VertxUnitRunner.class)
public class ClickhouseBinaryDataTypeEncodeTest extends BinaryDataTypeEncodeTestBase {

  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  @Override
  protected JDBCType getNumericJDBCType() {
    return JDBCType.DECIMAL;
  }

  @Override
  protected Class<? extends Number> getNumericClass() {
    return Numeric.class;
  }

  @Override
  protected Number getNumericValue(Number value) {
    return Numeric.create(value);
  }

  @Override
  protected Number getNumericValue(String value) {
    return Numeric.parse(value);
  }

  @Override
  protected void initConnector() {
    connector = ClientConfig.CONNECT.connect(vertx, rule.options());
  }

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

  @Ignore
  @Test
  public void testTime(TestContext ctx) {
    //no time support
  }

  @Test
  public void testBoolean(TestContext ctx) {
    testEncodeGeneric(ctx, "test_boolean", Byte.class, null, (byte)0);
  }

  @Test
  public void testDouble(TestContext ctx) {
    //Double.MIN_VALUE is too small here
    testEncodeGeneric(ctx, "test_float_8", Double.class, Row::getDouble, (double) 4.9e-322);
  }

  //no time support, copied and modified test from parent
  @Test
  public void testNullValues(TestContext ctx) {
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn
        .preparedQuery(statement("ALTER TABLE basicdatatype UPDATE" +
            " test_int_2 = ",
          ", test_int_4 = ",
          ", test_int_8 = ",
          ", test_float_4 = ",
          ", test_float_8 = ",
          ", test_numeric = ",
          ", test_decimal = ",
          ", test_boolean = ",
          ", test_char = ",
          ", test_varchar = ",
          ", test_date = ",
          " WHERE id = 2"))
        .execute(Tuple.tuple()
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null)
            .addValue(null),
          ctx.asyncAssertSuccess(updateResult -> {
            Sleep.sleepOrThrow();
            conn
              .preparedQuery("SELECT * FROM basicdatatype WHERE id = 2")
              .execute(ctx.asyncAssertSuccess(result -> {
                ctx.assertEquals(1, result.size());
                Row row = result.iterator().next();
                ctx.assertEquals(12, row.size());
                ctx.assertEquals(2, row.getInteger(0));
                for (int i = 1; i < 12; i++) {
                  ctx.assertNull(row.getValue(i));
                }
                conn.close();
              }));
          }));
    }));
  }

  @Override
  protected void maybeSleep() {
    Sleep.sleepOrThrow();
  }

  @Override
  protected String encodeGenericUpdateStatement(String columnName, int id) {
    return statement("ALTER TABLE basicdatatype UPDATE " + columnName + " = ", " WHERE id = " + id);
  }
}
