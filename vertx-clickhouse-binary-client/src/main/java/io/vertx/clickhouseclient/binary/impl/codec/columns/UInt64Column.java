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
import io.vertx.sqlclient.data.Numeric;

import java.math.BigInteger;
import java.util.List;

public class UInt64Column extends ClickhouseColumn {
  public static final Numeric[] EMPTY_NUMERIC_ARRAY = new Numeric[0];
  public static final Numeric UINT64_MIN = Numeric.create(BigInteger.ZERO);

  public UInt64Column(ClickhouseBinaryColumnDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new UInt64ColumnReader(nRows, descriptor);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new UInt64ColumnWriter(data, descriptor, columnIndex);
  }

  @Override
  public Object nullValue() {
    if (descriptor.isUnsigned()) {
      return UINT64_MIN;
    }
    return 0L;
  }

  @Override
  public Object[] emptyArray() {
    if (descriptor.isUnsigned()) {
      return EMPTY_NUMERIC_ARRAY;
    }
    return UInt32Column.EMPTY_LONG_ARRAY;
  }
}
