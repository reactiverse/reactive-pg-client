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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.*;
import io.vertx.clickhouseclient.binary.impl.codec.columns.ClickhouseColumn;
import io.vertx.clickhouseclient.binary.impl.codec.columns.ClickhouseColumnReader;
import io.vertx.clickhouseclient.binary.impl.codec.columns.ClickhouseColumnWriter;
import io.vertx.clickhouseclient.binary.impl.codec.columns.ClickhouseColumns;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.Tuple;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(Parameterized.class)
public class ArraySerDesTest {
  private static final Logger LOG = LoggerFactory.getLogger(ArraySerDesTest.class);

  private final ClickhouseColumn col;
  private final List<Tuple> data;

  public ArraySerDesTest(String nm, ClickhouseColumn col, List<Tuple> data) {
    this.col = col;
    this.data = data;
  }

  @Parameterized.Parameters(name = "{0}")
  public static Iterable<Object[]> dataForTest() {
    ClickhouseBinaryColumnDescriptor descr = ClickhouseColumns.columnDescriptorForSpec("Array(Array(Array(LowCardinality(Nullable(String)))))", "name");
    ClickhouseBinaryDatabaseMetadata md = new ClickhouseBinaryDatabaseMetadata("a", "b",
      0, 0,0, 0, "dname", ZoneId.systemDefault(), ZoneId.systemDefault(), "client",
      Collections.emptyMap(), StandardCharsets.UTF_8, null, null, null, true, true);
    ClickhouseColumn col = ClickhouseColumns.columnForSpec(descr, md);
    List<Tuple> data = Arrays.asList(
      //row slice: [[0, 2], [0, 1, 2], [0, 0, 0]] (master slice for 2 similar rows: [0,2],[0,2,4],[0,1,2,3,4],[0,0,0,0,0])
      Tuple.of(new String[][][]{ {{}}, {{}} } ),
      //row slice: [[0, 1], [0, 3], [0, 0, 1, 3]]
      Tuple.of(new String[][][]{ {{}, {"1"}, {"2", "3"}} }),
      Tuple.of(new String[][][]{ {{}, {"1"}, {"2", "3"}} }),
      Tuple.of(new String[][][]{ {{}}, {{}} } ),
      Tuple.of(new String[][][]{ {{}}, {{}} } ),
      Tuple.of( new String[][][]{ {{"str1_1", "str1_2", null}, {null}}, {{}} } ),
      Tuple.of( new String[][][]{ {{"str1_1", "str1_2", null}, {null}}, {{"str1_3", "str1_4", null}, {null}}} ),
      //master slice for 2 rows: [[0, 2], [0, 1, 3], [0, 3, 5, 6], [0, 0, 1, 3, 6, 7, 7]]
      //per row slices: [[[0, 1], [0, 3], [0, 0, 1, 3]], [[0, 2], [0, 2, 3], [0, 3, 4, 4]]]
      //[0, 1], [0, 3], [0, 0, 1, 3]
      Tuple.of(new String[][][]{ {{}, {"1"}, {"2", "3"}} }),
      //[0, 2], [0, 2, 3], [0, 3, 4, 4]
      Tuple.of( new String[][][]{ {{"str1_1", "str1_2", null}, {null}}, {{}} } )
    );

    return Arrays.asList(new Object[][]{
      {data.size() + " rows", col, data},
      {"0 rows", col, Collections.emptyList()}
    });
  }

  @Test
  public void doSerDes() {
    ClickhouseColumnWriter writer = col.writer(data, 0);
    ByteBuf buf = Unpooled.buffer();
    ClickhouseStreamDataSink sink = new RawClickhouseStreamDataSink(buf);
    writer.serializeColumn(sink, 0, data.size());
    sink.finish();

    ClickhouseColumnReader reader = col.reader(data.size());
    ClickhouseStreamDataSource ds = new RawClickhouseStreamDataSource(StandardCharsets.UTF_8);
    ds.moreData(buf, UnpooledByteBufAllocator.DEFAULT);
    reader.readColumn(ds);
    for (int rowIdx = 0; rowIdx < data.size(); ++rowIdx) {
      LOG.info("rowIdx: " + rowIdx);
      Object actual = reader.getElement(rowIdx, String.class);
      Object expected = data.get(rowIdx).getValue(0);
      Assert.assertArrayEquals((Object[])expected, (Object[])actual);
    }
  }
}
