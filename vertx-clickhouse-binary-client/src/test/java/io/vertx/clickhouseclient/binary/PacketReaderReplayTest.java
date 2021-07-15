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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.PacketReader;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import net.jpountz.lz4.LZ4Factory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RunWith(Parameterized.class)
public class PacketReaderReplayTest {
  private static final Logger LOG = LoggerFactory.getLogger(PacketReaderReplayTest.class);

  private final Map<String, String> props;
  private final ByteBuf buf;
  private final LZ4Factory lz4Factory;

  public PacketReaderReplayTest(String replayFile, String fragmented, ByteBuf buf, Map<String, String> props, LZ4Factory lz4Factory) {
    this.buf = buf;
    this.props = props;
    this.lz4Factory = lz4Factory;
  }

  @Parameterized.Parameters(name = "{0}({1})")
  public static Iterable<Object[]> dataForTest() throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    List<Object[]> result = new ArrayList<>();

    final int continuousOffset = 8;
    List<String> replayFiles = Arrays.asList(
      "/insert_prepare_with_compression.yaml",
      "/with_max_block_size_and_2_datablocks_with_compression.yaml",
      "/nullable_low_cardinality_with_compression.yaml",
      "/nullable_low_cardinality_without_compression.yaml",
      "/select_array_of_nullable_string_without_compression.yaml",
      "/select_empty_array_without_compression.yaml",
      "/ClickhouseBinaryPreparedQueryCachedTest_testConcurrentClose_with_compression.yaml"
    );
    for (String replayFile : replayFiles) {
      boolean compression = replayFile.contains("with_compression");
      try (InputStream is = PacketReaderReplayTest.class.getResourceAsStream(replayFile)) {
        Map<String, byte[]> map = mapper.readValue(is, Map.class);

        List<byte[]> queryAnswers = PacketUtil.filterServerBlocks(map);
        byte[][] arrays = PacketUtil.asPrimitiveByteArray(queryAnswers);
        ByteBuf fragmentedByteBuf = Unpooled.wrappedBuffer(arrays);
        ByteBuf continuousBuf = Unpooled.wrappedBuffer(new byte[fragmentedByteBuf.readableBytes()])
          .writerIndex(0);
        fragmentedByteBuf.readBytes(continuousBuf);
        fragmentedByteBuf.readerIndex(0);
        ByteBuf continuousWithOffsetBuf = Unpooled.wrappedBuffer(new byte[fragmentedByteBuf.readableBytes() + continuousOffset],
          continuousOffset, fragmentedByteBuf.readableBytes())
          .writerIndex(0);
        fragmentedByteBuf.readBytes(continuousWithOffsetBuf);
        fragmentedByteBuf.readerIndex(0);


        Map<String, String> p = buildProperties(compression);
        LZ4Factory f = compression ? LZ4Factory.safeInstance() : null;
        result.add(new Object[]{replayFile, "fragmented", fragmentedByteBuf, p, f});
        result.add(new Object[]{replayFile, "continuous", continuousBuf, p, f});
        result.add(new Object[]{replayFile, "continuousWithOffset", continuousWithOffsetBuf, p, f});
      }
    }
    return result;
  }

  @After
  public void cleanup() {
    buf.release();
  }

  @Test
  public void doReplayTest() {
    PooledByteBufAllocator allocator = new PooledByteBufAllocator();
    String fullName = "Clickhouse jython-driver";
    LOG.info("all bytes: " + ByteBufUtil.hexDump(buf));
    while (buf.readableBytes() > 0) {
      readConnInteraction(allocator, fullName);
    }
  }

  private void readConnInteraction(PooledByteBufAllocator allocator, String fullName) {
    //1st packet: server hello
    PacketReader rdr = new PacketReader(null, fullName, props, lz4Factory);
    ClickhouseBinaryDatabaseMetadata md = (ClickhouseBinaryDatabaseMetadata)rdr.receivePacket(allocator, buf);

    do {
      rdr = new PacketReader(md, fullName, props, lz4Factory);
      Object packet = rdr.receivePacket(allocator, buf);
      LOG.info("packet: " + packet);
    } while (!rdr.isEndOfStream() && buf.readableBytes() > 0);
  }

  private static Map<String, String> buildProperties(boolean withCompression) {
    Map<String, String> props = new HashMap<>();
    props.put(ClickhouseConstants.OPTION_APPLICATION_NAME, "jython-driver");
    if (withCompression) {
      props.put(ClickhouseConstants.OPTION_COMPRESSOR, "lz4_safe");
    }
    props.put(ClickhouseConstants.OPTION_INITIAL_HOSTNAME, "bhorse");
    return props;
  }
}
