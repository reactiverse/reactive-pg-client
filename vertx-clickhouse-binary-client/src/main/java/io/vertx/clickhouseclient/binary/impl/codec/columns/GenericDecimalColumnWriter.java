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

package io.vertx.clickhouseclient.binary.impl.codec.columns;

import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSink;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class GenericDecimalColumnWriter extends ClickhouseColumnWriter {
  private final BigInteger negAddon;

  public GenericDecimalColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor descriptor, int columnIndex) {
    super(data, descriptor, columnIndex);
    this.negAddon = BigInteger.ONE.shiftLeft(columnDescriptor.getElementSize() * 8);
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    BigDecimal bd = ((Numeric) val).bigDecimalValue();
    if (bd == null) {
      serializeDataNull(sink);
      return;
    }
    ColumnUtils.bigDecimalFitsOrThrow(bd, columnDescriptor);
    BigInteger bi = bd.unscaledValue();
    boolean negative = bi.signum() == -1;
    if (negative) {
      bi = bi.add(negAddon);
    }
    byte[] bytes = ColumnUtils.reverse(bi.toByteArray());
    sink.writeBytes(bytes, 0, negative ? bytes.length - 1 : bytes.length);
    int extraZeros = negative ? 0 : columnDescriptor.getElementSize() - bytes.length;
    sink.writeZero(extraZeros);
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    sink.writeZero(columnDescriptor.getElementSize());
  }

  private static void traceBytes(byte[] bytes) {
    for (byte b : bytes) {
      System.err.print(Integer.toHexString(Byte.toUnsignedInt(b)));
      System.err.print(",");
    }
    System.err.println();
  }

  public static BigInteger parseBigIntegerPositive(String num, int bitlen) {
    BigInteger b = new BigInteger(num);
    if (b.compareTo(BigInteger.ZERO) < 0)
      b = b.add(BigInteger.ONE.shiftLeft(bitlen));
    return b;
  }

  public static void main(String[] args) {
    BigInteger bi = parseBigIntegerPositive("-2", 16);
    System.err.println(bi);
    System.err.println("bytes1:");
    traceBytes(bi.toByteArray());
    System.err.println("bytes2:");
    traceBytes(BigInteger.valueOf(65534).toByteArray());

    BigInteger bi2 = parseBigIntegerPositive("-1", 16);
    System.err.println("bytes3_1");
    traceBytes(bi2.toByteArray());

    bi2 = parseBigIntegerPositive("-1", 15);
    System.err.println("bytes3_2");
    traceBytes(bi2.toByteArray());
  }
}
