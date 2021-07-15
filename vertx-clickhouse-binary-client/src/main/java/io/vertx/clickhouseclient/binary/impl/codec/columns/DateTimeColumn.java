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

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public class DateTimeColumn extends ClickhouseColumn {
  public static final OffsetDateTime[] EMPTY_ARRAY = new OffsetDateTime[0];

  private final ZoneId zoneId;
  private final OffsetDateTime nullValue;

  public DateTimeColumn(ClickhouseBinaryColumnDescriptor descriptor, ZoneId zoneId) {
    super(descriptor);
    this.zoneId = zoneId;
    this.nullValue = Instant.EPOCH.atZone(zoneId).toOffsetDateTime();
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new DateTimeColumnReader(nRows, descriptor, zoneId);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new DateTimeColumnWriter(data, descriptor, zoneId, columnIndex);
  }

  @Override
  public Object nullValue() {
    return nullValue;
  }

  @Override
  public Object[] emptyArray() {
    return EMPTY_ARRAY;
  }
}
