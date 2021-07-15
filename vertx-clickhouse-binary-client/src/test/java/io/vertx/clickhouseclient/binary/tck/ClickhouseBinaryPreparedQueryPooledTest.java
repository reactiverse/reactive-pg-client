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

package io.vertx.clickhouseclient.binary.tck;


import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ClickhouseBinaryPreparedQueryPooledTest extends ClickhouseBinaryPreparedQueryTestBase {
  @Override
  protected void initConnector() {
    options = rule.options();
    connector = ClientConfig.POOLED.connect(vertx, options);
  }
}
