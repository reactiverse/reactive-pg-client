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
import io.vertx.sqlclient.Tuple;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

public class IPv4Column extends UInt32Column {
  public static final int ELEMENT_SIZE = 4;
  public static final Inet4Address[] EMPTY_ARRAY = new Inet4Address[0];

  public static final Inet4Address ZERO_VALUE = ipv4(new byte[]{0, 0, 0, 0});
  public static final Inet4Address MAX_VALUE = ipv4(new byte[]{Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE});
  public static final Inet4Address MIN_VALUE = ipv4(new byte[]{Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE});

  private static Inet4Address ipv4(byte[] src) {
    try {
      return (Inet4Address) Inet4Address.getByAddress(src);
    } catch (UnknownHostException ex) {
      throw new RuntimeException(ex);
    }
  }

  public IPv4Column(ClickhouseBinaryColumnDescriptor descr) {
    super(descr);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new IPv4ColumnReader(nRows, descriptor);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new IPv4ColumnWriter(data, descriptor, columnIndex);
  }

  @Override
  public Object nullValue() {
    return ZERO_VALUE;
  }

  @Override
  public Object[] emptyArray() {
    return EMPTY_ARRAY;
  }
}
