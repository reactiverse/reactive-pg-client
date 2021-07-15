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

public class UInt32Column extends ClickhouseColumn {
  public static final Long[] EMPTY_LONG_ARRAY = new Long[0];

  public UInt32Column(ClickhouseBinaryColumnDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new UInt32ColumnReader(nRows, descriptor);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new UInt32ColumnWriter(data, descriptor, columnIndex);
  }

  @Override
  public Object nullValue() {
    if (descriptor.isUnsigned()) {
      return 0L;
    }
    return 0;
  }

  @Override
  public Object[] emptyArray() {
    if (descriptor.isUnsigned()) {
      return EMPTY_LONG_ARRAY;
    }
    return UInt16Column.EMPTY_INT_ARRAY;
  }
}
