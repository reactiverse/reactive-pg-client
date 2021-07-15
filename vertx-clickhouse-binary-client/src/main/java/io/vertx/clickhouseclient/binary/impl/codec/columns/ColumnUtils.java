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

import java.math.BigDecimal;

public class ColumnUtils {
  public static byte[] reverse(byte[] src) {
    for (int i = 0, j = src.length - 1; i < j; ++i, --j) {
      byte tmp = src[i];
      src[i] = src[j];
      src[j] = tmp;
    }
    return src;
  }

  public static int getLastNonZeroPos(byte[] bytes) {
    int lastNonZeroPos = bytes.length - 1;
    for (; lastNonZeroPos >= 0 && bytes[lastNonZeroPos] == 0; --lastNonZeroPos) {
    }
    return lastNonZeroPos;
  }

  //TODO: maybe rework checks
  public static void bigDecimalFitsOrThrow(BigDecimal bd, ClickhouseBinaryColumnDescriptor columnDescriptor) {
    if (columnDescriptor.getScale() < bd.scale()) {
      throw new IllegalArgumentException("possible loss of scale: max " + columnDescriptor.getScale() + ", got " + bd.scale());
    }
    if (columnDescriptor.getPrecision() < bd.precision()) {
      throw new IllegalArgumentException("possible loss of precision: max " + columnDescriptor.getPrecision() + ", got " + bd.precision());
    }
  }
}
