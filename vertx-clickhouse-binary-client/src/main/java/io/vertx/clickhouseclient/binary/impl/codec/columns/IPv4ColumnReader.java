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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv4ColumnReader extends UInt32ColumnReader {

  public IPv4ColumnReader(int nRows, ClickhouseBinaryColumnDescriptor descriptor) {
    super(nRows, descriptor);
  }

  @Override
  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    if (desired == InetAddress.class || desired == Inet4Address.class || desired == Object.class || desired == null) {
      Long addr = (Long) super.getElementInternal(rowIdx, byte[].class);
      try {
        return Inet4Address.getByAddress(intBytes(addr));
      } catch (UnknownHostException ex) {
        throw new RuntimeException(ex);
      }
    }
    return super.getElementInternal(rowIdx, desired);
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    if (desired == InetAddress.class || desired == Inet4Address.class || desired == Object.class || desired == null) {
      return new Inet4Address[dim1][dim2];
    }
    return super.allocateTwoDimArray(desired, dim1, dim2);
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    if (desired == InetAddress.class || desired == Inet4Address.class || desired == Object.class || desired == null) {
      return new Inet4Address[length];
    }
    return super.allocateOneDimArray(desired, length);
  }

  private static byte[] intBytes(Long l) {
    return new byte[] {
      (byte) (l >>> 24 & 0xFF),
      (byte) (l >>> 16 & 0xFF),
      (byte) (l >>> 8 & 0xFF),
      (byte) (l & 0xFF)
    };
  }
}
