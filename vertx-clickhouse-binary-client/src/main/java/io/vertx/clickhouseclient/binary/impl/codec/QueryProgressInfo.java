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

public class QueryProgressInfo {
  private final int rows;
  private final int bytes;
  private final int totalRows;
  private final int writtenRows;
  private final int writtenBytes;

  public QueryProgressInfo(int rows, int bytes, int totalRows, int writtenRows, int writtenBytes) {
    this.rows = rows;
    this.bytes = bytes;
    this.totalRows = totalRows;

    this.writtenRows = writtenRows;
    this.writtenBytes = writtenBytes;
  }

  public int getRows() {
    return rows;
  }

  public int getBytes() {
    return bytes;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public int getWrittenRows() {
    return writtenRows;
  }

  public int getWrittenBytes() {
    return writtenBytes;
  }

  @Override
  public String toString() {
    return "QueryProgressInfo{" +
      "rows=" + rows +
      ", bytes=" + bytes +
      ", totalRows=" + totalRows +
      ", writtenRows=" + writtenRows +
      ", writtenBytes=" + writtenBytes +
      '}';
  }
}
