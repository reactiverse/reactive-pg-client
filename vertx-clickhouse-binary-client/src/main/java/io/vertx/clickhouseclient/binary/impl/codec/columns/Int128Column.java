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

import java.math.BigInteger;
import java.util.List;

public class Int128Column extends ClickhouseColumn {
  public static final int ELEMENT_SIZE = 16;
  public static final BigInteger[] EMPTY_ARRAY = new BigInteger[0];

  public static final BigInteger ZERO_VALUE = new BigInteger(new byte[ELEMENT_SIZE]);
  public static final BigInteger INT128_MIN_VALUE = new BigInteger("-170141183460469231731687303715884105728");
  public static final BigInteger INT128_MAX_VALUE = new BigInteger( "170141183460469231731687303715884105727");

  public Int128Column(ClickhouseBinaryColumnDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new Int128ColumnReader(nRows, descriptor);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new Int128ColumnWriter(data, descriptor, columnIndex);
  }

  @Override
  public Object nullValue() {
    return ZERO_VALUE;
  }

  @Override
  public Object[] emptyArray() {
    return EMPTY_ARRAY;
  }
}
