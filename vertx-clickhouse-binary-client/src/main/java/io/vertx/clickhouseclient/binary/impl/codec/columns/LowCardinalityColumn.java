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

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.sqlclient.Tuple;

import java.util.List;

public class LowCardinalityColumn extends ClickhouseColumn {
  private final ClickhouseBinaryColumnDescriptor indexDescriptor;
  private final ClickhouseColumn indexColumn;
  private final ClickhouseBinaryDatabaseMetadata md;

  public LowCardinalityColumn(ClickhouseBinaryColumnDescriptor descriptor, ClickhouseBinaryDatabaseMetadata md) {
    super(descriptor);
    this.md = md;
    this.indexDescriptor = descriptor.copyWithModifiers(false, false);
    this.indexColumn = ClickhouseColumns.columnForSpec(indexDescriptor, md);
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new LowCardinalityColumnReader(nRows, descriptor, indexDescriptor, md);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new LowCardinalityColumnWriter(data, descriptor, md, columnIndex);
  }

  @Override
  public Object nullValue() {
    return indexColumn.nullValue();
  }

  @Override
  public Object[] emptyArray() {
    return indexColumn.emptyArray();
  }
}
