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
import java.util.Map;

public class Enum16Column extends UInt16Column {
  private final Map<? extends Number, String> enumVals;
  private final EnumResolutionMethod resolutionMethod;

  public Enum16Column(ClickhouseBinaryColumnDescriptor descriptor, Map<? extends Number, String> enumVals, EnumResolutionMethod resolutionMethod) {
    super(descriptor);
    this.enumVals = enumVals;
    this.resolutionMethod = resolutionMethod;
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new Enum16ColumnReader(nRows, descriptor, enumVals, resolutionMethod);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new Enum16ColumnWriter(data, descriptor, columnIndex, enumVals, resolutionMethod);
  }
}
