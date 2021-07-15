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

public class Float32Column extends ClickhouseColumn {
  public static final Float[] EMPTY_FLOAT_ARRAY = new Float[0];
  public static final Float ZERO_VALUE = 0.0f;

  public Float32Column(ClickhouseBinaryColumnDescriptor descriptor) {
    super(descriptor);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new Float32ColumnReader(nRows, descriptor);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new Float32ColumnWriter(data, descriptor, columnIndex);
  }

  @Override
  public Object nullValue() {
    return ZERO_VALUE;
  }

  @Override
  public Object[] emptyArray() {
    return EMPTY_FLOAT_ARRAY;
  }
}
