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

public interface ClickhouseStreamDataSink {
  void writeULeb128(int value);
  void writeByte(int value);
  void writeShortLE(int value);
  void writeIntLE(int value);
  void writeLongLE(long value);
  void writeFloatLE(float value);
  void writeDoubleLE(double value);
  void writeBytes(byte[] value);
  void writeBytes(byte[] value, int index, int length);
  void writeBoolean(boolean value);
  void writeZero(int length);
  void writePascalString(String value);
  void ensureWritable(int size);

  default void finish() {
  }
}
