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

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv6ColumnReader extends FixedStringColumnReader {

  protected IPv6ColumnReader(int nRows, ClickhouseBinaryColumnDescriptor columnDescriptor, ClickhouseBinaryDatabaseMetadata md) {
    super(nRows, columnDescriptor, false, md);
  }

  @Override
  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    if (desired == InetAddress.class || desired == Inet6Address.class || desired == Object.class || desired == null) {
      byte[] addr = (byte[]) super.getElementInternal(rowIdx, byte[].class);
      try {
        return Inet6Address.getByAddress(addr);
      } catch (UnknownHostException ex) {
        throw new RuntimeException(ex);
      }
    }
    return super.getElementInternal(rowIdx, desired);
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    if (desired == InetAddress.class || desired == Inet6Address.class || desired == Object.class || desired == null) {
      return new Inet6Address[dim1][dim2];
    }
    return super.allocateTwoDimArray(desired, dim1, dim2);
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    if (desired == InetAddress.class || desired == Inet6Address.class || desired == Object.class || desired == null) {
      return new Inet6Address[length];
    }
    return super.allocateOneDimArray(desired, length);
  }
}
