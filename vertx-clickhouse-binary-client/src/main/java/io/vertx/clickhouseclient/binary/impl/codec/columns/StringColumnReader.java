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
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSource;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class StringColumnReader extends ClickhouseColumnReader {
  private Integer curStrLength;
  private List<byte[]> elements;
  private final Charset charset;
  private final boolean enableStringCache;
  private final StringCache cache;

  protected StringColumnReader(int nRows, ClickhouseBinaryColumnDescriptor descriptor, boolean enableStringCache, ClickhouseBinaryDatabaseMetadata md) {
    super(nRows, descriptor);
    this.elements = new ArrayList<>(nRows);
    this.charset = md.getStringCharset();
    this.enableStringCache = enableStringCache;
    this.cache = enableStringCache ? new StringCache(nRows) : null;
  }

  @Override
  protected Object[] readItems(ClickhouseStreamDataSource in) {
    while (elements.size() < nRows) {
      if (curStrLength == null) {
        curStrLength = in.readULeb128();
        if (curStrLength == null) {
          return null;
        }
      }
      if (in.readableBytes() < curStrLength) {
        return null;
      }
      //no dedicated BLOB type support; will encode(later) into String if user asked for String explicitly
      byte[] stringBytes;
      if (nullsMap == null || !nullsMap.get(elements.size())) {
        stringBytes = new byte[curStrLength];
        in.readBytes(stringBytes);
      } else {
        stringBytes = null;
        in.skipBytes(curStrLength);
      }
      elements.add(stringBytes);
      curStrLength = null;
    }
    Object[] ret = elements.toArray(new byte[elements.size()][]);
    elements = null;
    return ret;
  }

  @Override
  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    Object tmp = getObjectsArrayElement(rowIdx);
    if ((desired == String.class || desired == Object.class) && tmp != null) {
      return enableStringCache ? cache.get(rowIdx, () -> new String((byte[])tmp, charset)) : new String((byte[])tmp, charset);
    }
    return tmp;
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    if (desired == byte[].class) {
      return new byte[dim1][dim2][];
    }
    return new String[dim1][dim2];
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    if (desired == byte[].class) {
      return new byte[length][];
    }
    return new String[length];
  }
}
