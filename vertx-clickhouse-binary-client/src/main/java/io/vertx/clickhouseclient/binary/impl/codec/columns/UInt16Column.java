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

public class UInt16Column extends ClickhouseColumn {
  public static final Integer[] EMPTY_INT_ARRAY = new Integer[0];
  public static final Short[] EMPTY_SHORT_ARRAY = new Short[0];

  public UInt16Column(ClickhouseBinaryColumnDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new UInt16ColumnReader(nRows, descriptor);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new UInt16ColumnWriter(data, descriptor, columnIndex);
  }

  @Override
  public Object nullValue() {
    if (descriptor.isUnsigned()) {
      return (int) 0;
    } else {
      return (short) 0;
    }
  }

  @Override
  public Object[] emptyArray() {
    if (descriptor.isUnsigned()) {
      return EMPTY_INT_ARRAY;
    } else {
      return EMPTY_SHORT_ARRAY;
    }
  }
}
