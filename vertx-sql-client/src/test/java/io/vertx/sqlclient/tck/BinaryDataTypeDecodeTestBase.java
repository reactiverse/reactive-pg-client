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

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.sqlclient.Row;
import org.junit.Test;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalTime;

public abstract class BinaryDataTypeDecodeTestBase extends DataTypeTestBase {

  @Test
  public void testSmallInt(TestContext ctx) {
    testDecodeGeneric(ctx, "test_int_2", Short.class, JDBCType.SMALLINT, (short) 32767);
  }

  @Test
  public void testInteger(TestContext ctx) {
    testDecodeGeneric(ctx, "test_int_4", Integer.class, JDBCType.INTEGER, 2147483647);
  }

  @Test
  public void testBigInt(TestContext ctx) {
    testDecodeGeneric(ctx, "test_int_8", Long.class, JDBCType.BIGINT, 9223372036854775807L);
  }

  @Test
  public void testFloat4(TestContext ctx) {
    testDecodeGeneric(ctx, "test_float_4", Float.class, JDBCType.REAL, 3.40282e38F);
  }

  @Test
  public void testDouble(TestContext ctx) {
    testDecodeGeneric(ctx, "test_float_8", Double.class, JDBCType.DOUBLE, 1.7976931348623157E308);
  }

  @Test
  public void testNumeric(TestContext ctx) {
    testDecodeGeneric(ctx, "test_numeric", getNumericClass(), getNumericJDBCType(), getNumericValue("999.99"));
  }

  @Test
  public void testDecimal(TestContext ctx) {
    testDecodeGeneric(ctx, "test_decimal", getNumericClass(), getNumericJDBCType(), getNumericValue("12345"));
  }

  @Test
  public void testBoolean(TestContext ctx) {
    testDecodeGeneric(ctx, "test_boolean", Boolean.class, JDBCType.BOOLEAN, true);
  }

  @Test
  public void testChar(TestContext ctx) {
    testDecodeGeneric(ctx, "test_char", String.class, JDBCType.VARCHAR, "testchar");
  }

  @Test
  public void testVarchar(TestContext ctx) {
    testDecodeGeneric(ctx, "test_varchar", String.class, JDBCType.VARCHAR, "testvarchar");
  }

  @Test
  public void testDate(TestContext ctx) {
    testDecodeGeneric(ctx, "test_date", LocalDate.class, JDBCType.DATE, LocalDate.of(2019, 1, 1));
  }

  @Test
  public void testTime(TestContext ctx) {
    testDecodeGeneric(ctx, "test_time", LocalTime.class, JDBCType.TIME, LocalTime.of(18, 45, 2));
  }

  protected <T> void testDecodeGeneric(TestContext ctx,
                                       String columnName,
                                       Class<? extends T> clazz,
                                       JDBCType jdbcType,
                                       T expected) {
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn
        .preparedQuery("SELECT " + columnName + " FROM basicdatatype WHERE id = 1")
        .execute(ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        Row row = result.iterator().next();
        ctx.assertEquals(expected, row.getValue(0));
        ctx.assertEquals(expected, row.getValue(columnName));
        ctx.assertEquals(jdbcType, result.columnDescriptors().get(0).jdbcType());
//        ctx.assertEquals(expected, row.get(clazz, 0));
//        ColumnChecker.checkColumn(0, columnName)
//          .returns(Tuple::getValue, Row::getValue, expected)
//          .returns(byIndexGetter, byNameGetter, expected)
//          .forRow(row);
        conn.close();
      }));
    }));
  }

  @Test
  public void testNullValues(TestContext ctx) {
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn.preparedQuery("SELECT " +
        "test_int_2," +
        "test_int_4," +
        "test_int_8," +
        "test_float_4," +
        "test_float_8," +
        "test_numeric," +
        "test_decimal," +
        "test_boolean," +
        "test_char," +
        "test_varchar," +
        "test_date," +
        "test_time " +
        "from basicdatatype where id = 3").execute(ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        Row row = result.iterator().next();
        ctx.assertEquals(12, row.size());
        for (int i = 0; i < 12; i++) {
          ctx.assertNull(row.getValue(i));
        }
        conn.close();
      }));
    }));
  }

  @Test
  public void testSelectAll(TestContext ctx) {
      connector.connect(ctx.asyncAssertSuccess(conn -> {
          conn.preparedQuery("SELECT " +
            "test_int_2," +
            "test_int_4," +
            "test_int_8," +
            "test_float_4," +
            "test_float_8," +
            "test_numeric," +
            "test_decimal," +
            "test_boolean," +
            "test_char," +
            "test_varchar," +
            "test_date," +
            "test_time " +
            "from basicdatatype where id = 1").execute(ctx.asyncAssertSuccess(result -> {
            ctx.assertEquals(1, result.size());
            Row row = result.iterator().next();
            ctx.assertEquals(12, row.size());
            ctx.assertEquals((short) 32767, row.getShort(0));
            ctx.assertEquals((short) 32767, row.getShort("test_int_2"));
            ctx.assertEquals(2147483647, row.getInteger(1));
            ctx.assertEquals(2147483647, row.getInteger("test_int_4"));
            ctx.assertEquals(9223372036854775807L, row.getLong(2));
            ctx.assertEquals(9223372036854775807L, row.getLong("test_int_8"));
            ctx.assertEquals(3.40282E38F, row.getFloat(3));
            ctx.assertEquals(3.40282E38F, row.getFloat("test_float_4"));
            ctx.assertEquals(1.7976931348623157E308, row.getDouble(4));
            ctx.assertEquals(1.7976931348623157E308, row.getDouble("test_float_8"));
            ctx.assertEquals(getNumericValue(999.99), row.get(getNumericClass(), 5));
            ctx.assertEquals(getNumericValue(999.99), row.getValue("test_numeric"));
            ctx.assertEquals(getNumericValue(12345), row.get(getNumericClass(), 6));
            ctx.assertEquals(getNumericValue(12345), row.getValue("test_decimal"));
            ctx.assertEquals(true, row.getBoolean(7));
            ctx.assertEquals(true, row.getBoolean("test_boolean"));
            ctx.assertEquals("testchar", row.getString(8));
            ctx.assertEquals("testchar", row.getString("test_char"));
            ctx.assertEquals("testvarchar", row.getString(9));
            ctx.assertEquals("testvarchar", row.getString("test_varchar"));
            ctx.assertEquals(LocalDate.parse("2019-01-01"), row.getValue(10));
            ctx.assertEquals(LocalDate.parse("2019-01-01"), row.getValue("test_date"));
            ctx.assertEquals(LocalTime.parse("18:45:02"), row.getValue(11));
            ctx.assertEquals(LocalTime.parse("18:45:02"), row.getValue("test_time"));
            conn.close();
          }));
      }));
  }

  @Test
  public void testToJsonObject(TestContext ctx) {
    connector.connect(ctx.asyncAssertSuccess(conn -> {
      conn.preparedQuery("SELECT " +
        "test_int_2," +
        "test_int_4," +
        "test_int_8," +
        "test_float_4," +
        "test_float_8," +
        "test_numeric," +
        "test_decimal," +
        "test_boolean," +
        "test_char," +
        "test_varchar," +
        "test_date " +
        "from basicdatatype where id = 1").execute(ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        Row row = result.iterator().next();
        JsonObject json = row.toJson();

        ctx.assertEquals(11, json.size());
        ctx.assertEquals((short) 32767, json.getValue("test_int_2"));
        ctx.assertEquals(2147483647, json.getValue("test_int_4"));
        ctx.assertEquals(9223372036854775807L, json.getValue("test_int_8"));
        ctx.assertEquals(3.40282E38F, json.getValue("test_float_4"));
        ctx.assertEquals(1.7976931348623157E308, json.getValue("test_float_8"));
        ctx.assertEquals(getNumericValue(999.99), json.getValue("test_numeric"));
        ctx.assertEquals(getNumericValue(12345), json.getValue("test_decimal"));
        Object booleanValue = json.getValue("test_boolean");
        ctx.assertTrue(booleanValue instanceof Boolean || booleanValue instanceof Number);
        ctx.assertEquals("testchar", json.getValue("test_char"));
        ctx.assertEquals("testvarchar", json.getValue("test_varchar"));
        ctx.assertEquals("2019-01-01", json.getValue("test_date"));
        conn.close();
      }));
    }));
  }
}
