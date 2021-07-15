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
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.UUID;

public class UUIDColumn extends ClickhouseColumn {
  public static final UUID[] EMPTY_UUID_ARRAY = new UUID[0];

  public static final UUID ZERO_UUID = new UUID(0, 0);
  public static final int ELEMENT_SIZE = 16;

  public UUIDColumn(ClickhouseBinaryColumnDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new UUIDColumnReader(nRows, descriptor);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new UUIDColumnWriter(data, descriptor, columnIndex);
  }

  @Override
  public Object nullValue() {
    return ZERO_UUID;
  }

  @Override
  public Object[] emptyArray() {
    return EMPTY_UUID_ARRAY;
  }
}
