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

public class FixedStringColumn extends ClickhouseColumn {
  protected final ClickhouseBinaryDatabaseMetadata md;
  private final boolean enableStringCache;

  public FixedStringColumn(ClickhouseBinaryColumnDescriptor descriptor, ClickhouseBinaryDatabaseMetadata md, boolean enableStringCache) {
    super(descriptor);
    this.md = md;
    this.enableStringCache = enableStringCache;
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new FixedStringColumnReader(nRows, descriptor, enableStringCache, md);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new FixedStringColumnWriter(data, descriptor, md.getStringCharset(), columnIndex);
  }

  @Override
  public Object nullValue() {
    return StringColumn.ZERO_VALUE;
  }

  @Override
  public Object[] emptyArray() {
    return StringColumn.EMPTY_ARRAY;
  }
}
