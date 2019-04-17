package io.vertx.mysqlclient;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MySQLDatatypeTest extends MySQLTestBase {
  Vertx vertx;
  PgConnectOptions options;

  @Before
  public void setup() {
    vertx = Vertx.vertx();
    options = new PgConnectOptions(MySQLTestBase.options);
  }

  @After
  public void teardown(TestContext ctx) {
    vertx.close(ctx.asyncAssertSuccess());
  }

  @Test
  public void testTextDecodeBinary(TestContext ctx) {
    testTextDecodeGeneric(ctx, "Binary", "HELLO");
  }

  @Test
  public void testBinaryDecodeBinary(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "Binary", "HELLO");
  }

  @Test
  public void testBinaryEncodeBinary(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "Binary", "HELLO");
  }

  @Test
  public void testBinaryEncodeVarBinary(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "VarBinary", "HELLO, WORLD");
  }

  @Test
  public void testTextDecodeVarBinary(TestContext ctx) {
    testTextDecodeGeneric(ctx, "VarBinary", "HELLO, WORLD");
  }

  @Test
  public void testBinaryDecodeVarBinary(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "VarBinary", "HELLO, WORLD");
  }

  @Test
  public void testBinaryEncodeTinyBlob(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "TinyBlob", Buffer.buffer("TINYBLOB"));
  }

  @Test
  public void testTextDecodeTinyBlob(TestContext ctx) {
    testTextDecodeGeneric(ctx, "TinyBlob", Buffer.buffer("TINYBLOB"));
  }

  @Test
  public void testBinaryDecodeTinyBlob(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "TinyBlob", Buffer.buffer("TINYBLOB"));
  }

  @Test
  public void testBinaryEncodeBlob(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "Blob", Buffer.buffer("BLOB"));
  }

  @Test
  public void testTextDecodeBlob(TestContext ctx) {
    testTextDecodeGeneric(ctx, "Blob", Buffer.buffer("BLOB"));
  }

  @Test
  public void testBinaryDecodeBlob(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "Blob", Buffer.buffer("BLOB"));
  }

  @Test
  public void testBinaryEncodeMediumBlob(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "MediumBlob", Buffer.buffer("MEDIUMBLOB"));
  }

  @Test
  public void testTextDecodeMediumBlob(TestContext ctx) {
    testTextDecodeGeneric(ctx, "MediumBlob", Buffer.buffer("MEDIUMBLOB"));
  }

  @Test
  public void testBinaryDecodeMediumBlob(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "MediumBlob", Buffer.buffer("MEDIUMBLOB"));
  }

  @Test
  public void testBinaryEncodeLongBlob(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "LongBlob", Buffer.buffer("LONGBLOB"));
  }

  @Test
  public void testTextDecodeLongBlob(TestContext ctx) {
    testTextDecodeGeneric(ctx, "LongBlob", Buffer.buffer("LONGBLOB"));
  }

  @Test
  public void testBinaryDecodeLongBlob(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "LongBlob", Buffer.buffer("LONGBLOB"));
  }

  @Test
  public void testBinaryEncodeTinyText(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "TinyText", Buffer.buffer("TINYTEXT"));
  }

  @Test
  public void testTextDecodeTinyText(TestContext ctx) {
    testTextDecodeGeneric(ctx, "TinyText", Buffer.buffer("TINYTEXT"));
  }

  @Test
  public void testBinaryDecodeTinyText(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "TinyText", Buffer.buffer("TINYTEXT"));
  }

  @Test
  public void testBinaryEncodeText(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "Text", Buffer.buffer("TEXT"));
  }

  @Test
  public void testTextDecodeText(TestContext ctx) {
    testTextDecodeGeneric(ctx, "Text", Buffer.buffer("TEXT"));
  }

  @Test
  public void testBinaryDecodeText(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "Text", Buffer.buffer("TEXT"));
  }

  @Test
  public void testBinaryEncodeMediumText(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "MediumText", Buffer.buffer("MEDIUMTEXT"));
  }

  @Test
  public void testTextDecodeMediumText(TestContext ctx) {
    testTextDecodeGeneric(ctx, "MediumText", Buffer.buffer("MEDIUMTEXT"));
  }

  @Test
  public void testBinaryDecodeMediumText(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "MediumText", Buffer.buffer("MEDIUMTEXT"));
  }

  @Test
  public void testBinaryEncodeLongText(TestContext ctx) {
    testBinaryEncodeGeneric(ctx, "LongText", Buffer.buffer("LONGTEXT"));
  }

  @Test
  public void testTextDecodeLongText(TestContext ctx) {
    testTextDecodeGeneric(ctx, "LongText", Buffer.buffer("LONGTEXT"));
  }

  @Test
  public void testBinaryDecodeLongText(TestContext ctx) {
    testBinaryDecodeGeneric(ctx, "LongText", Buffer.buffer("LONGTEXT"));
  }

  private <T> void testTextDecodeGeneric(TestContext ctx,
                                         String columnName,
                                         T expected) {
    Async async = ctx.async();
    MySQLClient.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.query("SELECT `" + columnName + "` FROM datatype WHERE id = 1", ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        Row row = result.iterator().next();
        ctx.assertEquals(expected, row.getValue(0));
        ctx.assertEquals(expected, row.getValue(columnName));
        async.complete();
      }));
    }));
  }

  private <T> void testBinaryDecodeGeneric(TestContext ctx,
                                           String columnName,
                                           T expected) {
    Async async = ctx.async();
    MySQLClient.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.preparedQuery("SELECT `" + columnName + "` FROM datatype WHERE id = 1", ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        Row row = result.iterator().next();
        ctx.assertEquals(expected, row.getValue(0));
        ctx.assertEquals(expected, row.getValue(columnName));
        async.complete();
      }));
    }));
  }

  private <T> void testBinaryEncodeGeneric(TestContext ctx,
                                           String columnName,
                                           T expected) {
    Async async = ctx.async();
    MySQLClient.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.preparedQuery("UPDATE datatype SET `" + columnName + "` = ?" + " WHERE id = 2", Tuple.tuple().addValue(expected), ctx.asyncAssertSuccess(updateResult -> {
        conn.preparedQuery("SELECT `" + columnName + "` FROM datatype WHERE id = 2", ctx.asyncAssertSuccess(result -> {
          ctx.assertEquals(1, result.size());
          Row row = result.iterator().next();
          ctx.assertEquals(expected, row.getValue(0));
          ctx.assertEquals(expected, row.getValue(columnName));
          async.complete();
        }));
      }));
    }));
  }
}
