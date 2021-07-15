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

public class Triplet<L, M, R> {
  private final L left;
  private final M middle;
  private final R right;

  public Triplet(L left, M middle, R right) {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }

  public L left() {
    return left;
  }

  public M middle() {
    return middle;
  }

  public R right() {
    return right;
  }
}
