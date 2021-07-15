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

package io.vertx.clickhouseclient.binary.impl;

import io.vertx.sqlclient.desc.ColumnDescriptor;
import io.vertx.sqlclient.impl.RowDesc;

import java.util.Collections;
import java.util.List;

public class ClickhouseBinaryRowDesc extends RowDesc {
  public static final ClickhouseBinaryRowDesc EMPTY = new ClickhouseBinaryRowDesc(Collections.emptyList(), Collections.emptyList());

  public ClickhouseBinaryRowDesc(List<String> columnNames) {
    super(columnNames);
  }

  public ClickhouseBinaryRowDesc(List<String> columnNames, List<ColumnDescriptor> columnDescriptors) {
    super(columnNames, columnDescriptors);
  }
}
