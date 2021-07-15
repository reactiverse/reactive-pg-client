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

import java.time.Duration;
import java.util.List;

public class IntervalColumn extends ClickhouseColumn {
  public static final Duration[] EMPTY_ARRAY = new Duration[0];

  public static final Duration ZERO_VALUE = Duration.ZERO;
  private final Duration multiplier;

  public IntervalColumn(ClickhouseBinaryColumnDescriptor descriptor, Duration multiplier) {
    super(descriptor);
    this.multiplier = multiplier;
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new IntervalColumnReader(nRows, descriptor, multiplier);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    throw new IllegalStateException("not implemented");
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
