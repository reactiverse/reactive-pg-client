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
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.data.Numeric;
import io.vertx.sqlclient.tck.BinaryDataTypeDecodeTestBase;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.JDBCType;
import java.time.LocalDate;

@RunWith(VertxUnitRunner.class)
public class ClickhouseBinaryDataTypeDecodeTest extends BinaryDataTypeDecodeTestBase {
  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  public ClickhouseBinaryDataTypeDecodeTest() {
  }

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

  @Test
  public void testBoolean(TestContext ctx) {
    testDecodeGeneric(ctx, "test_boolean", Byte.class, JDBCType.TINYINT, (byte) 1);
  }

  @Test
  public void testSelectAll(TestContext ctx) {
    //no time support
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
        ctx.assertEquals(11, row.size());
        ctx.assertEquals((short) 32767, row.getShort(0));
        ctx.assertEquals(Short.valueOf((short) 32767), row.getShort("test_int_2"));
        ctx.assertEquals(2147483647, row.getInteger(1));
        ctx.assertEquals(2147483647, row.getInteger("test_int_4"));
        ctx.assertEquals(9223372036854775807L, row.getLong(2));
        ctx.assertEquals(9223372036854775807L, row.getLong("test_int_8"));
        ctx.assertEquals(3.40282E38F, row.getFloat(3));
        ctx.assertEquals(3.40282E38F, row.getFloat("test_float_4"));
        ctx.assertEquals(1.7976931348623157E308, row.getDouble(4));
        ctx.assertEquals(1.7976931348623157E308, row.getDouble("test_float_8"));
        ctx.assertEquals(Numeric.create(999.99), row.get(Numeric.class, 5));
        ctx.assertEquals(Numeric.create(999.99), row.getValue("test_numeric"));
        ctx.assertEquals(Numeric.create(12345), row.get(Numeric.class, 6));
        ctx.assertEquals(Numeric.create(12345), row.getValue("test_decimal"));
        ctx.assertEquals((byte)1, row.getValue(7));
        ctx.assertEquals((byte)1, row.getValue("test_boolean"));
        ctx.assertEquals("testchar", row.getString(8));
        ctx.assertEquals("testchar", row.getString("test_char"));
        ctx.assertEquals("testvarchar", row.getString(9));
        ctx.assertEquals("testvarchar", row.getString("test_varchar"));
        ctx.assertEquals(LocalDate.parse("2019-01-01"), row.getValue(10));
        ctx.assertEquals(LocalDate.parse("2019-01-01"), row.getValue("test_date"));
        conn.close();
      }));
    }));
  }

  @Test
  public void testNullValues(TestContext ctx) {
    //no time support
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
        "from basicdatatype where id = 3").execute(ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        Row row = result.iterator().next();
        ctx.assertEquals(11, row.size());
        for (int i = 0; i < 11; i++) {
          ctx.assertNull(row.getValue(i));
        }
        conn.close();
      }));
    }));
  }

  @Ignore
  @Test
  public void testTime(TestContext ctx) {
    //No time support
  }
}
