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

import java.time.LocalDate;

public class DateColumnReader extends UInt16ColumnReader {
  public static final LocalDate[] EMPTY_ARRAY = new LocalDate[0];

  public static final LocalDate MIN_VALUE = LocalDate.of(1970, 1, 1);
  public static final LocalDate MAX_VALUE = MIN_VALUE.plusDays(65535);

  public DateColumnReader(int nRows, ClickhouseBinaryColumnDescriptor columnDescriptor) {
    super(nRows, columnDescriptor);
  }

  @Override
  protected Object getElementInternal(int rowIdx, Class<?> desired) {
    Integer offset = (Integer) super.getElementInternal(rowIdx, desired);
    return MIN_VALUE.plusDays(offset);
  }

  @Override
  protected Object[] allocateTwoDimArray(Class<?> desired, int dim1, int dim2) {
    return new LocalDate[dim1][dim2];
  }

  @Override
  protected Object allocateOneDimArray(Class<?> desired, int length) {
    return new LocalDate[length];
  }
}
