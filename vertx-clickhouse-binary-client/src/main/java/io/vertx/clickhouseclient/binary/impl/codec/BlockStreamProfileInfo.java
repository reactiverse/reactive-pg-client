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

package io.vertx.clickhouseclient.binary.impl.codec;

public class BlockStreamProfileInfo {
  private final int rows;
  private final int blocks;
  private final int bytes;
  private final boolean appliedLimit;
  private final int rowsBeforeLimit;
  private final boolean calculatedRowsBeforeLimit;

  public BlockStreamProfileInfo(int rows, int blocks, int bytes, boolean appliedLimit, int rowsBeforeLimit,
                                boolean calculatedRowsBeforeLimit) {
    this.rows = rows;
    this.blocks = blocks;
    this.bytes = bytes;
    this.appliedLimit = appliedLimit;
    this.rowsBeforeLimit = rowsBeforeLimit;
    this.calculatedRowsBeforeLimit = calculatedRowsBeforeLimit;
  }

  public int getRows() {
    return rows;
  }

  public int getBlocks() {
    return blocks;
  }

  public int getBytes() {
    return bytes;
  }

  public boolean getAppliedLimit() {
    return appliedLimit;
  }

  public int getRowsBeforeLimit() {
    return rowsBeforeLimit;
  }

  public boolean getCalculatedRowsBeforeLimit() {
    return calculatedRowsBeforeLimit;
  }

  @Override
  public String toString() {
    return "BlockStreamProfileInfo{" +
      "rows=" + rows +
      ", blocks=" + blocks +
      ", bytes=" + bytes +
      ", appliedLimit=" + appliedLimit +
      ", rowsBeforeLimit=" + rowsBeforeLimit +
      ", calculatedRowsBeforeLimit=" + calculatedRowsBeforeLimit +
      '}';
  }
}
