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

package io.vertx.clickhouseclient.binary.impl.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.vertx.clickhouseclient.binary.ClickhouseConstants;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import ru.yandex.clickhouse.util.ClickHouseCityHash;

import java.nio.charset.Charset;
import java.util.Arrays;

public class Lz4ClickhouseStreamDataSource implements ClickhouseStreamDataSource {
  private static final Logger LOG = LoggerFactory.getLogger(Lz4ClickhouseStreamDataSource.class);

  //compression method byte + sizeWithHeader + decompressed size
  public static final int CHECKSUMED_HEADER_LENGTH = 1 + 4 + 4;
  //cityhash size + compression method byte + sizeWithHeader + decompressed size
  public static final int HEADER_LENGTH = 16 + CHECKSUMED_HEADER_LENGTH;

  private final Charset charset;
  private final LZ4Factory lz4Factory;
  private final ByteBuf decompressedData;
  private long[] serverCityHash;
  private Long sizeWithHeader;
  private Long uncompressedSize;
  private ByteBuf arrayBb;

  public Lz4ClickhouseStreamDataSource(LZ4Factory lz4Factory, Charset charset, ByteBufAllocator alloc) {
    this.lz4Factory = lz4Factory;
    this.decompressedData = alloc.heapBuffer();
    this.charset = charset;
  }

  @Override
  public void moreData(ByteBuf buf, ByteBufAllocator alloc) {
    if (serverCityHash == null) {
      if (buf.readableBytes() >= HEADER_LENGTH) {
        serverCityHash = new long[2];
        if (LOG.isDebugEnabled()) {
          dumpHeader(buf);
          LOG.debug(this.hashCode() + ": lz4 header dump: " + ByteBufUtil.hexDump(buf, buf.readerIndex(), HEADER_LENGTH) +
            "; buf hash: " + buf.hashCode() + "; identityHash:" + System.identityHashCode(buf) +
            "; readerIndex: " + buf.readerIndex() + "; writerIndex: " + buf.writerIndex() + "; readableBytes: " + buf.readableBytes());
          LOG.debug("full dump: " + ByteBufUtil.hexDump(buf));
        }
        serverCityHash[0] = buf.readLongLE();
        serverCityHash[1] = buf.readLongLE();
        int checkSummedReaderIndex = buf.readerIndex();
        int compressionMethod = buf.readUnsignedByte();
        if (compressionMethod != ClickhouseConstants.COMPRESSION_METHOD_LZ4) {
          decompressedData.release();
          String msg = String.format("unexpected compression method type 0x%X; expects 0x%X",
            compressionMethod, ClickhouseConstants.COMPRESSION_METHOD_LZ4);
          throw new IllegalStateException(msg);
        }
        sizeWithHeader = buf.readUnsignedIntLE();
        if (sizeWithHeader > Integer.MAX_VALUE) {
          throw new IllegalStateException("block is too big: " + sizeWithHeader + "; limit " + Integer.MAX_VALUE);
        }
        long compressedAndSizeSize = sizeWithHeader - 1 - 4;
        uncompressedSize = buf.readUnsignedIntLE();
        if (uncompressedSize > Integer.MAX_VALUE) {
          throw new IllegalStateException("uncompressedSize is too big: " + uncompressedSize + "; limit " + Integer.MAX_VALUE);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug(String.format("compressed size: %d(0x%X), sizeWithHeader: %d(0x%X), uncompressed size: %d(0x%X)",
            compressedAndSizeSize, compressedAndSizeSize, sizeWithHeader, sizeWithHeader, uncompressedSize, uncompressedSize));
        }
        arrayBb = alloc.buffer(sizeWithHeader.intValue());
        buf.readerIndex(checkSummedReaderIndex);
        buf.readBytes(arrayBb, CHECKSUMED_HEADER_LENGTH);
      }
    }
    if (uncompressedSize == null) {
      return;
    }

    int compressedDataSize = sizeWithHeader.intValue() - CHECKSUMED_HEADER_LENGTH;
    if (buf.readableBytes() < compressedDataSize) {
      //NB: fragmented read
      return;
    }
    //TODO: maybe skip arrayBb allocation if buf.hasArray() == true and not fragmented read^^^^
    buf.readBytes(arrayBb);
    long[] oursCityHash = ClickHouseCityHash.cityHash128(arrayBb.array(), arrayBb.arrayOffset(), sizeWithHeader.intValue());
    //reposition at the beginning of the compressed data, need to skip compression method byte, sizeWithHeader and uncompressed size
    arrayBb.readerIndex(CHECKSUMED_HEADER_LENGTH);

    if (!Arrays.equals(serverCityHash, oursCityHash)) {
      LOG.error("cityhash mismatch");
      LOG.error("all available data: " + ByteBufUtil.hexDump(buf, 0, buf.readerIndex() + buf.readableBytes()));
      LOG.error("data from reader index(" + buf.readerIndex() + "): " + ByteBufUtil.hexDump(buf));
      LOG.error("compressed block bytes w/header: " + ByteBufUtil.hexDump(arrayBb, arrayBb.readerIndex() - CHECKSUMED_HEADER_LENGTH, sizeWithHeader.intValue()));
      LOG.error("readableBytes: " + arrayBb.readableBytes() + "; comprDataLen: " + compressedDataSize);
      throw new IllegalStateException("CityHash mismatch; server's: " +
        Arrays.toString(hex(serverCityHash)) + "; ours: " +
        Arrays.toString(hex(oursCityHash)));
    }
    byte[] uncompressedBytes = new byte[uncompressedSize.intValue()];
    LZ4FastDecompressor decompressor = lz4Factory.fastDecompressor();
    //LOG.info("compressed bytes: " + ByteBufUtil.hexDump(arrayBb));
    decompressor.decompress(arrayBb.array(), arrayBb.arrayOffset() + arrayBb.readerIndex(), uncompressedBytes, 0, uncompressedBytes.length);
    if (LOG.isDebugEnabled()) {
      LOG.debug("decompressed " + uncompressedBytes.length + " bytes of data");
    }
    //LOG.info("decompressed data: " + ByteBufUtil.hexDump(uncompressedBytes) + "; asStr: " + new String(uncompressedBytes, StandardCharsets.UTF_8));
    decompressedData.writeBytes(uncompressedBytes);
    arrayBb.release();
    serverCityHash = null;
    sizeWithHeader = null;
    uncompressedSize = null;
  }

  private void dumpHeader(ByteBuf buf) {
    String h1 = ByteBufUtil.hexDump(buf, buf.readerIndex(), 8);
    String h2 = ByteBufUtil.hexDump(buf, buf.readerIndex() + 8, 8);
    String method = ByteBufUtil.hexDump(buf, buf.readerIndex() + 16, 1);
    String sizeWithHeader = ByteBufUtil.hexDump(buf, buf.readerIndex() + 17, 4);
    String uncompressedSize = ByteBufUtil.hexDump(buf, buf.readerIndex() + 21, 4);
    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("header: [%s:%s]:%s:%s:%s", h1, h2, method, sizeWithHeader, uncompressedSize));
    }
  }

  @Override
  public int readableBytes() {
    return decompressedData.readableBytes();
  }

  @Override
  public void skipBytes(int length) {
    decompressedData.skipBytes(length);
  }

  @Override
  public String readPascalString() {
    return ByteBufUtils.readPascalString(decompressedData, charset);
  }

  @Override
  public Integer readULeb128() {
    return ByteBufUtils.readULeb128(decompressedData);
  }

  @Override
  public boolean readBoolean() {
    return decompressedData.readBoolean();
  }

  @Override
  public int readIntLE() {
    return decompressedData.readIntLE();
  }

  @Override
  public ByteBuf readSlice(int nBytes) {
    return decompressedData.readSlice(nBytes);
  }

  @Override
  public void readBytes(byte[] dst) {
    decompressedData.readBytes(dst);
  }

  @Override
  public byte readByte() {
    return decompressedData.readByte();
  }

  @Override
  public long readLongLE() {
    return decompressedData.readLongLE();
  }

  @Override
  public short readShortLE() {
    return decompressedData.readShortLE();
  }

  @Override
  public float readFloatLE() {
    return decompressedData.readFloatLE();
  }

  @Override
  public double readDoubleLE() {
    return decompressedData.readDoubleLE();
  }

  @Override
  public String hexdump() {
    return ByteBufUtil.hexDump(decompressedData);
  }

  @Override
  public void finish() {
    decompressedData.release();
  }

  private static String[] hex(long[] src) {
    String[] result = new String[src.length];
    for (int i = 0; i < src.length; ++i) {
      result[i] = "0x" + Long.toHexString(src[i]);
    }
    return result;
  }
}
