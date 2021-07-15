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

public abstract class ClickhouseColumn {
  protected ClickhouseBinaryColumnDescriptor descriptor;

  public ClickhouseColumn(ClickhouseBinaryColumnDescriptor descriptor) {
    this.descriptor = descriptor;
  }

  public abstract ClickhouseColumnReader reader(int nRows);

  public abstract ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex);

  public abstract Object nullValue();
  public abstract Object[] emptyArray();
}
