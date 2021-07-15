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

import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.columns.ClickhouseColumns;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(VertxUnitRunner.class)
public class BasicClickhouseTest {
  private static final Logger LOG = LoggerFactory.getLogger(BasicClickhouseTest.class);

  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  private ClickhouseBinaryConnectOptions options;
  private Vertx vertx;

  @Before
  public void setup() {
    options = rule.options();
    vertx = Vertx.vertx();
  }

  @After
  public void teardDown(TestContext ctx) {
    vertx.close(ctx.asyncAssertSuccess());
  }

  @Test
  public void baseConnectTest(TestContext ctx) {
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(SqlClient::close));
  }

  @Test
  public void loginFailureTest(TestContext ctx) {
    ClickhouseBinaryConnectOptions opts = new ClickhouseBinaryConnectOptions(options);
    opts.setPassword("wrong-password");
    ClickhouseBinaryConnection.connect(vertx, opts, ctx.asyncAssertFailure());
  }

  @Test
  public void testIntegerRanges(TestContext ctx) {
    List<ClickhouseBinaryColumnDescriptor> types = Stream.of("Int8", "Int16", "Int32", "Int64", "Int128")
      .flatMap(el -> "Int128".equals(el)
        ? Stream.of(el, "Nullable(" + el + ")")
        : Stream.of(el, "U" + el, "LowCardinality(Nullable(" + el + "))", "Nullable(U" + el + ")", "LowCardinality(Nullable(U" + el + "))"))
      .map(spec -> ClickhouseColumns.columnDescriptorForSpec(spec, "fake_name"))
      .collect(Collectors.toList());
    List<String> typeNames = types.stream()
      .map(ClickhouseBinaryColumnDescriptor::getUnparsedNativeType).collect(Collectors.toList());
    LOG.info("integer columns: " + typeNames);
    Iterator<ClickhouseBinaryColumnDescriptor> typesIter = types.iterator();
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      scheduleIntTypeQuery(ctx, typesIter, conn);
    }));
  }

  private void scheduleIntTypeQuery(TestContext ctx, Iterator<ClickhouseBinaryColumnDescriptor> typeIterator, ClickhouseBinaryConnection conn) {
    if (typeIterator.hasNext()) {
      ClickhouseBinaryColumnDescriptor descr = typeIterator.next();
      String nativeType = descr.getUnparsedNativeType();
      //looks like Nullable(UInt128) is broken for min/max at the moment, hence add/subtract
      BigInteger minValue = descr.getMinValue().add(BigInteger.ONE);
      BigInteger maxValue = descr.getMaxValue().subtract(BigInteger.ONE);
      String query = String.format("SELECT CAST('%s', '%s') as min_val, CAST('%s', '%s') as max_val",
        minValue, nativeType, maxValue, nativeType);
      LOG.info("running query: " + query);
      conn.query(query).execute(
        ctx.asyncAssertSuccess(res -> {
          ctx.assertEquals(1, res.size());
          Row row = res.iterator().next();
          ctx.assertEquals(minValue, row.getBigDecimal("min_val").toBigIntegerExact(), nativeType + " type min failure");
          ctx.assertEquals(maxValue, row.getBigDecimal("max_val").toBigIntegerExact(), nativeType + " type max failure");
          scheduleIntTypeQuery(ctx, typeIterator, conn);
        })
      );
    } else {
      conn.close();
    }
  }

  @Test
  public void emptyArrayTest(TestContext ctx) {
    arrayTest(ctx,
  "select arr from (" +
          "select 1 as id, array() as arr UNION ALL " +
          "select 2 as id, array('a') as arr UNION ALL " +
          "select 3 as id, array() as arr" +
        ") t1 order by id",
      Arrays.asList(new Object[0], new Object[]{"a"}, new Object[0]));
  }

  @Test
  public void nonEmptyArrayTest(TestContext ctx) {
    arrayTest(ctx,
  "select arr from (" +
          "select 1 as id, array(array(), array(NULL), array(1, NULL, 2), array(321)) as arr UNION ALL " +
          "select 1 as id, array(array(34), array(), array(5, 6, NULL), array(14, NULL)) as arr " +
        ") t1 order by id",
      Arrays.asList(new Object[][]{{}, {null}, {1, null, 2}, {321}}, new Object[][]{{34}, {}, {5, 6, null}, {14, null}}));
  }

  @Test
  public void nonEmptyLowCardinalityArrayTest(TestContext ctx) {
    arrayTest(ctx,
  "select arr from (" +
          "select 1 as id, CAST(array(array(), array(NULL), array('a', NULL, 'b'), array('c')), 'Array(Array(LowCardinality(Nullable(String))))') as arr UNION ALL " +
          "select 2 as id, CAST(array(array(NULL), array(), array('d', 'e', NULL), array('f', NULL)), 'Array(Array(LowCardinality(Nullable(String))))') as arr" +
        ") t1 order by id",
      Arrays.asList(new Object[][]{{}, {null}, {"a", null, "b"}, {"c"}}, new Object[][]{{null}, {}, {"d", "e", null}, {"f", null}}));
  }

  private void arrayTest(TestContext ctx, String query, List<Object[]> expected) {
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.query(query).execute(
        ctx.asyncAssertSuccess(res1 -> {
          ctx.assertEquals(res1.size(), expected.size());
          int i = 0;
          for (Row row : res1) {
            Object[] expectedVal = expected.get(i);
            Object[] actualVal = (Object[]) row.getValue(0);
            ctx.assertEquals(true, Arrays.deepEquals(expectedVal, actualVal));
            ++i;
          }
          conn.close();
        })
      );
    }));
  }

  @Test
  public void baseQueryTest(TestContext ctx) {
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.query("select 4 as resource, 'aa' as str_col1, CAST('abcdef', 'FixedString(6)') as str_col2").execute(
        ctx.asyncAssertSuccess(res1 -> {
          ctx.assertEquals(1, res1.size());
          conn.close();
        })
      );
    }));
  }

  @Test
  public void blobTest(TestContext ctx) {
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.query("select 'abcd'").execute(
        ctx.asyncAssertSuccess(res1 -> {
          ctx.assertEquals(1, res1.size());
          Row row = res1.iterator().next();
          byte[] bytes = row.get(byte[].class, 0);
          ctx.assertEquals("abcd", new String(bytes, StandardCharsets.UTF_8));
          conn.close();
        })
      );
    }));
  }
}
