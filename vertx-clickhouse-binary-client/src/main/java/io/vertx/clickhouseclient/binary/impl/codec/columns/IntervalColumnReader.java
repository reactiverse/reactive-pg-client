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

import java.time.Duration;

public class IntervalColumnReader extends UInt64ColumnReader {
  private final Duration multiplier;

  public IntervalColumnReader(int nRows, ClickhouseBinaryColumnDescriptor descriptor, Duration multiplier) {
    super(nRows, descriptor);
    this.multiplier = multiplier;
  }

  @Override
  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    Long obj = (Long)super.getElementInternal(rowIdx, desired);
    if (desired != Duration.class) {
      return obj;
    }
    return multiplier.multipliedBy(obj);
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    if (desired == Duration.class) {
      return new Duration[dim1][dim2];
    }
    return super.allocateTwoDimArray(desired, dim1, dim2);
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    if (desired == Duration.class) {
      return new Duration[length];
    }
    return super.allocateOneDimArray(desired, length);
  }
}
