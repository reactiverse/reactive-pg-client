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
import java.time.ZoneId;
import java.util.List;

public class DateTime64Column extends ClickhouseColumn {
  public static final int ELEMENT_SIZE = 8;
  public static final Instant ZERO_INSTANT = Instant.EPOCH;
  public static final Instant[] EMPTY_ARRAY = new Instant[0];

  private final Integer precision;
  private final ZoneId zoneId;
  private boolean saturateExtraNanos;

  public DateTime64Column(ClickhouseBinaryColumnDescriptor descriptor, Integer precision, boolean saturateExtraNanos, ZoneId zoneId) {
    super(descriptor);
    this.precision = precision;
    this.zoneId = zoneId;
    this.saturateExtraNanos = saturateExtraNanos;
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new DateTime64ColumnReader(nRows, descriptor, precision, zoneId);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new DateTime64ColumnWriter(data, descriptor, precision, zoneId, saturateExtraNanos, columnIndex);
  }

  @Override
  public Object nullValue() {
    return ZERO_INSTANT.atZone(zoneId).toOffsetDateTime();
  }

  @Override
  public Object[] emptyArray() {
    return EMPTY_ARRAY;
  }
}
