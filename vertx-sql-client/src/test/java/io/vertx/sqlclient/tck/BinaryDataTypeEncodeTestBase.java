/*
 * Copyright (c) 2011-2021 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.sqlclient.tck;

import io.vertx.ext.unit.TestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.BiFunction;

public abstract class BinaryDataTypeEncodeTestBase extends DataTypeTestBase {
  protected abstract String statement(String... parts);

  @Test
  public void testSmallInt(TestContext ctx) {
    testEncodeGeneric(ctx, "test_int_2", Short.class, Row::getShort, Short.MIN_VALUE);
  }

  @Test
  public void testInteger(TestContext ctx) {
    testEncodeGeneric(ctx, "test_int_4", Integer.class, Row::getInteger, Integer.MIN_VALUE);
  }

  @Test
  public void testBigInt(TestContext ctx) {
    testEncodeGeneric(ctx, "test_int_8", Long.class, Row::getLong, Long.MIN_VALUE);
  }

  @Test
  public void testFloat4(TestContext ctx) {
    testEncodeGeneric(ctx, "test_float_4", Float.class, Row::getFloat, -3.402823e38F);
  }

  @Test
  public void testDouble(TestContext ctx) {
    testEncodeGeneric(ctx, "test_float_8", Double.class, Row::getDouble, Double.MIN_VALUE);
  }

  @Test
  public void testNumeric(TestContext ctx) {
    testEncodeGeneric(ctx, "test_numeric", getNumericClass(), null, getNumericValue("-999.99"));
  }

  @Test
  public void testDecimal(TestContext ctx) {
    testEncodeGeneric(ctx, "test_decimal", getNumericClass(), null, getNumericValue("-12345"));
  }

  @Test
  public void testChar(TestContext ctx) {
    testEncodeGeneric(ctx, "test_char", String.class, Row::getString, "newchar0");
  }

  @Test
  public void testVarchar(TestContext ctx) {
    testEncodeGeneric(ctx, "test_varchar", String.class, Row::getString, "newvarchar");
  }

  @Test
  public void testBoolean(TestContext ctx) {
    testEncodeGeneric(ctx, "test_boolean", Boolean.class, Row::getBoolean, false);
  }

  @Test
  public void testDate(TestContext ctx) {
    testEncodeGeneric(ctx, "test_date", LocalDate.class, Row::getLocalDate, LocalDate.parse("1999-12-31"));
  }

  @Test
  public void testTime(TestContext ctx) {
    testEncodeGeneric(ctx, "test_time", LocalTime.class, Row::getLocalTime, LocalTime.of(12,1,30));
  }

  @Test
  public void testNullValues(TestContext ctx) {
      connector.connect(ctx.asyncAssertSuccess(conn -> {
          conn
            .preparedQuery(statement("UPDATE basicdatatype SET" +
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
                  ", test_time = ",
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
                .addValue(null)
                .addValue(null),
                ctx.asyncAssertSuccess(updateResult -> {
            conn
              .preparedQuery("SELECT * FROM basicdatatype WHERE id = 2")
              .execute(ctx.asyncAssertSuccess(result -> {
              ctx.assertEquals(1, result.size());
              Row row = result.iterator().next();
              ctx.assertEquals(13, row.size());
              ctx.assertEquals(2, row.getInteger(0));
              for (int i = 1; i < 13; i++) {
                ctx.assertNull(row.getValue(i));
              }
              conn.close();
            }));
          }));
        }));
  }

  protected void maybeSleep() {
  }

  protected String encodeGenericUpdateStatement(String columnName, int id) {
    return statement("UPDATE basicdatatype SET " + columnName + " = ", " WHERE id = " + id);
  }

  protected <T> void testEncodeGeneric(TestContext ctx,
                                       String columnName,
                                       Class<? extends T> clazz,
                                       BiFunction<Row, String, T> getter,
                                       T expected) {
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn
        .preparedQuery(encodeGenericUpdateStatement(columnName, 2))
        .execute(Tuple.tuple().addValue(expected), ctx.asyncAssertSuccess(updateResult -> {
         maybeSleep();
        conn
          .preparedQuery("SELECT " + columnName + " FROM basicdatatype WHERE id = 2")
          .execute(ctx.asyncAssertSuccess(result -> {
          ctx.assertEquals(1, result.size());
          Row row = result.iterator().next();
          ctx.assertEquals(1, row.size());
          ctx.assertEquals(expected, row.getValue(0));
          ctx.assertEquals(expected, row.getValue(columnName));
          if (getter != null) {
            ctx.assertEquals(expected, getter.apply(row, columnName));
          }
//        ctx.assertEquals(expected, row.get(clazz, 0));
//        ColumnChecker.checkColumn(0, columnName)
//          .returns(Tuple::getValue, Row::getValue, expected)
//          .returns(byIndexGetter, byNameGetter, expected)
//          .forRow(row);
          conn.close();
        }));
      }));
    }));
  }
}
