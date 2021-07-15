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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PairedIterator {
  public static <T>  Iterator<Map.Entry<T, T>> of(List<T> src) {
    if (src.size() <= 1) {
      return Collections.emptyIterator();
    }

    Iterator<T> iter2 = src.iterator();
    iter2.next();
    return new ListPairedIterator<>(src.iterator(), iter2);
  }

  public static IntPairIterator of(int[] src) {
    if (src.length <= 1) {
      return IntPairIterator.EMPTY;
    }
    return new ArrayIntPairIterator(src);
  }

  public static void main(String[] args) {
    Iterator<Map.Entry<String, String>> iter = PairedIterator.of(Arrays.asList("A", "B", "C"));
    while (iter.hasNext()) {
      Map.Entry<String, String> n = iter.next();
      System.err.println(n.getKey() + "; " + n.getValue());
    }

    IntPairIterator iter2 = PairedIterator.of(new int[]{1, 2, 3, 4});
    while (iter2.hasNext()) {
      iter2.next();
      System.err.println(iter2.getKey() + "; " + iter2.getValue());
    }
  }
}
