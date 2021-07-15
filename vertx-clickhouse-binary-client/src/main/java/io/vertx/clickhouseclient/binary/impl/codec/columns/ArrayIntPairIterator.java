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

public class ArrayIntPairIterator implements IntPairIterator {
  private final int[] src;
  private int pos;

  public ArrayIntPairIterator(int[] src) {
    this.src = src;
    this.pos = -1;
  }


  @Override
  public boolean hasNext() {
    return pos < src.length - 2;
  }

  @Override
  public void next() {
    ++pos;
  }

  @Override
  public int getKey() {
    return src[pos];
  }

  @Override
  public int getValue() {
    return src[pos + 1];
  }
}
