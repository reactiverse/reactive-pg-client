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

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

public class ListPairedIterator<T> implements Iterator<Map.Entry<T, T>> {
  private final Iterator<T> wrapped1;
  private final Iterator<T> wrapped2;

  public ListPairedIterator(Iterator<T> wrapped1, Iterator<T> wrapped2) {
    this.wrapped1 = wrapped1;
    this.wrapped2 = wrapped2;
  }

  @Override
  public boolean hasNext() {
    return wrapped1.hasNext() && wrapped2.hasNext();
  }

  @Override
  public Map.Entry<T, T> next() {
    T key = wrapped1.next();
    T val = wrapped2.next();
    return new AbstractMap.SimpleEntry<>(key, val);
  }
}
