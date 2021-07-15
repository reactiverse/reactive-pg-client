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

package io.vertx.clickhouseclient.binary.impl.codec;

import io.vertx.sqlclient.impl.command.QueryCommandBase;

abstract class ClickhouseBinaryQueryCommandBaseCodec<T, C extends QueryCommandBase<T>> extends ClickhouseBinaryCommandCodec<Boolean, C> {
  protected ClickhouseBinaryQueryCommandBaseCodec(C cmd) {
    super(cmd);
  }
}
