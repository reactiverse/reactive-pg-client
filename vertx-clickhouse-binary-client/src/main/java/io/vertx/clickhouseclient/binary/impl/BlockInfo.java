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

package io.vertx.clickhouseclient.binary.impl;

import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSink;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSource;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class BlockInfo {
  private static final Logger LOG = LoggerFactory.getLogger(BlockInfo.class);

  private Boolean isOverflows;
  private Integer bucketNum;
  private boolean complete;
  private Integer fieldNum;

  public BlockInfo() {
    isOverflows = false;
    bucketNum = -1;
  }

  public BlockInfo(Boolean isOverflows, Integer bucketNum) {
    this.isOverflows = isOverflows;
    this.bucketNum = bucketNum;
  }

  public void serializeTo(ClickhouseStreamDataSink sink) {
    sink.writeULeb128(1);
    sink.writeByte(isOverflows ? 1 : 0);
    sink.writeULeb128(2);
    sink.writeIntLE(bucketNum);
    sink.writeULeb128(0);
  }

  public boolean isComplete() {
    return complete;
  }

  public boolean isPartial() {
    return !complete;
  }

  public void readFrom(ClickhouseStreamDataSource in) {
    while (isPartial()) {
      if (fieldNum == null) {
        fieldNum = in.readULeb128();
        if (fieldNum == null) {
          return;
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("fieldNum: " + fieldNum + "(" + Integer.toHexString(fieldNum) + ")");
      }
      if (fieldNum == 0) {
        complete = true;
        return;
      }
      if (fieldNum == 1) {
        if (in.readableBytes() >= 1) {
          isOverflows = in.readBoolean();
          fieldNum = null;
          if (LOG.isDebugEnabled()) {
            LOG.debug("isOverflows: " + isOverflows);
          }
        } else {
          return;
        }
      } else if (fieldNum == 2) {
        int readable = in.readableBytes();
        if (readable >= 4) {
          bucketNum = in.readIntLE();
          fieldNum = null;
          if (LOG.isDebugEnabled()) {
            LOG.debug("bucketNum: " + bucketNum);
          }
        } else {
          return;
        }
      }
    }
  }

  @Override
  public String toString() {
    return "BlockInfo{" +
      "isOverflows=" + isOverflows +
      ", bucketNum=" + bucketNum +
      '}';
  }
}
