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

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

public class StringCache {
  private final int nElements;
  private SoftReference<String[]> stringCache;

  public StringCache(int nElements) {
    this.nElements = nElements;
    this.stringCache = new SoftReference<>(new String[nElements]);
  }

  public String get(int rowIdx, Supplier<String> supplier) {
    String[] cache = stringCache.get();
    String ret;
    if (cache == null) {
      cache = new String[nElements];
      stringCache = new SoftReference<>(cache);
      ret = supplier.get();
      cache[rowIdx] = ret;
    } else {
      ret = cache[rowIdx];
      if (ret == null) {
        ret = supplier.get();
        cache[rowIdx] = ret;
      }
    }
    return ret;
  }
}
