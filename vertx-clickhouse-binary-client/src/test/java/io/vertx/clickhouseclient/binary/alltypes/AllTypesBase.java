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
import io.vertx.clickhouseclient.binary.ClickhouseResource;
import io.vertx.clickhouseclient.binary.Sleep;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.ColumnChecker;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@RunWith(VertxUnitRunner.class)
public abstract class AllTypesBase<T> {
  private static final Logger LOG = LoggerFactory.getLogger(AllTypesBase.class);

  public static final String TABLE_PREFIX = "vertx_test_";
  protected final String tableSuffix;
  protected final MyColumnChecker<T> checker;
  protected final boolean hasLowCardinality;

  @ClassRule
  public static ClickhouseResource rule = new ClickhouseResource();

  protected ClickhouseBinaryConnectOptions options;
  protected Vertx vertx;

  public AllTypesBase(String tableSuffix, MyColumnChecker<T> checker) {
    this(tableSuffix, checker, true);
  }

  public AllTypesBase(String tableSuffix, MyColumnChecker<T> checker, boolean hasLowCardinality) {
    this.tableSuffix = tableSuffix;
    this.checker = checker;
    this.hasLowCardinality = hasLowCardinality;
  }

  @Before
  public void setup(TestContext ctx) {
    options = rule.options();
    vertx = Vertx.vertx();
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      conn.query("TRUNCATE TABLE " + tableName()).execute(ctx.asyncAssertSuccess());
    }));
  }

  @After
  public void teardDown(TestContext ctx) {
    vertx.close(ctx.asyncAssertSuccess());
  }

  @Test
  public void testEmptyData(TestContext ctx) {
    doTest(ctx, Collections.emptyList());
  }

  @Test
  public void testData(TestContext ctx) {
    doTest(ctx, createBatch());
  }

  public abstract List<Tuple> createBatch();

  private List<String> columnsList(boolean hasLowCardinality) {
    List<String> columns = new ArrayList<>(Arrays.asList("id", "simple_t", "nullable_t", "array_t", "array3_t", "nullable_array_t", "nullable_array3_t"));
    if (hasLowCardinality) {
      columns.addAll(Arrays.asList("simple_lc_t", "nullable_lc_t", "array_lc_t", "array3_lc_t", "nullable_array_lc_t", "nullable_array3_lc_t"));
    }
    return columns;
  }

  protected  void doTest(TestContext ctx, List<Tuple> batch) {
    String tableName = tableName();
    ClickhouseBinaryConnection.connect(vertx, options, ctx.asyncAssertSuccess(conn -> {
      List<String> columnsList = columnsList(hasLowCardinality);
      String columnsStr = String.join(", ", columnsList);
      String query = "INSERT INTO " + tableName + " (" + columnsStr + ") VALUES";
      conn.preparedQuery(query)
        .executeBatch(batch, ctx.asyncAssertSuccess(res2 -> {
          Sleep.sleepOrThrow();
          conn.query("SELECT " + columnsStr + " FROM " + tableName + " ORDER BY id").execute(ctx.asyncAssertSuccess(
            res3 -> {
              ctx.assertEquals(res3.size(), batch.size(), "row count mismatch");
              int batchIdx = 0;
              for (Row row : res3) {
                Number id = row.get(Number.class, "id");
                Tuple expectedRow = batch.get(batchIdx);
                LOG.info("checking row " + tableSuffix + ":" + id);
                for (int colIdx = 0; colIdx < expectedRow.size(); ++colIdx) {
                  String colName = columnsList.get(colIdx);
                  Object expectedColumnValue = expectedRow.getValue(colIdx);
                  checker.checkColumn(row, colIdx, colName, (T) expectedColumnValue);
                }
                ++batchIdx;
              }
          }));
        }));
    }));
  }

  protected String tableName() {
    return TABLE_PREFIX + tableSuffix;
  }
}

class MyColumnChecker<R> {
  private final Class<R> componentType;
  private final ColumnChecker.SerializableBiFunction<Tuple, Integer, R> byIndexGetter;
  private final ColumnChecker.SerializableBiFunction<Row, String, R> byNameGetter;
  private final ColumnChecker.SerializableBiFunction<Tuple, Integer, Object> arrayByIndexGetter;
  private final ColumnChecker.SerializableBiFunction<Row, String, Object> arrayByNameGetter;

  public MyColumnChecker(Class<R> componentType,
                         ColumnChecker.SerializableBiFunction<Tuple, Integer, R> byIndexGetter,
                         ColumnChecker.SerializableBiFunction<Row, String, R> byNameGetter,
                         ColumnChecker.SerializableBiFunction<Tuple, Integer, Object> arrayByIndexGetter,
                         ColumnChecker.SerializableBiFunction<Row, String, Object> arrayByNameGetter) {
    this.componentType = componentType;
    this.byIndexGetter = byIndexGetter;
    this.byNameGetter = byNameGetter;
    this.arrayByNameGetter = arrayByNameGetter;
    this.arrayByIndexGetter = arrayByIndexGetter;
  }

  public void checkColumn(Row row, int index, String name, R expected) {
    ColumnChecker checker = ColumnChecker.checkColumn(index, name);
    if ("id".equals(name)) {
      checker.returns((Class<R>)expected.getClass(), expected)
        .forRow(row);
      return;
    }
    if (componentType == byte[].class && (expected == null || expected.getClass() == byte[].class)) {
      //ask driver to turn off String encoding
      checker = checker
        .returns((tuple, idx) -> tuple.get(byte[].class, idx),
                 (ColumnChecker.SerializableBiFunction<Row, String, byte[]>) (r, colName) -> r.get(byte[].class, colName),
                 (Consumer<byte[]>) actual -> Assert.assertArrayEquals((byte[])actual, (byte[])expected));
    } else {
      //arrays are non-nullable
      if (expected != null && expected.getClass().isArray()) {
        boolean multidimensional = expected.getClass().getComponentType().isArray() && expected.getClass().getComponentType() != byte[].class;
        if (componentType == byte[].class || componentType.isEnum()) {
          //ask driver to turn off String encoding for BLOBs or force encoding for Enums
          checker = checker.returns((tuple, idx) -> tuple.get(expected.getClass(), idx), (r, colName) -> r.get(expected.getClass(), colName), (Object[]) expected);
        } else {
          checker = checker.returns(Tuple::getValue, Row::getValue, (Object[]) expected);
        }
        if (!multidimensional && arrayByIndexGetter != null) {
          //API does not provide dedicated methods to get multi-dimensional arrays
          checker = checker.returns(arrayByIndexGetter, arrayByNameGetter, (Object[]) expected);
        }
      } else {
        //regular non-array elements
        Object v = expected;
        if (componentType.isEnum()) {
          v = expected == null ? null : (R) ((Enum)expected).name();
        }
        checker = checker.returns(Tuple::getValue, Row::getValue, v);
        if (byIndexGetter != null) {
          checker = checker.returns(byIndexGetter, byNameGetter, expected);
        }
      }
    }
    checker.forRow(row);
  }
}
