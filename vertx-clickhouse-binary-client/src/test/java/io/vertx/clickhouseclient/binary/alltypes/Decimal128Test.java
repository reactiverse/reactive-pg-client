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

package io.vertx.clickhouseclient.binary.alltypes;

import io.vertx.clickhouseclient.binary.impl.codec.columns.Decimal128Column;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.runner.RunWith;

import java.math.MathContext;
import java.math.RoundingMode;

@RunWith(VertxUnitRunner.class)
public class Decimal128Test extends HugeDecimalTest {
  public Decimal128Test() {
    super("decimal128", new MathContext(Decimal128Column.MAX_PRECISION, RoundingMode.HALF_EVEN));
  }
}
