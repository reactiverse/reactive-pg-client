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

import io.vertx.clickhouseclient.binary.impl.codec.columns.Decimal256Column;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.runner.RunWith;

import java.math.MathContext;
import java.math.RoundingMode;

@RunWith(VertxUnitRunner.class)
public class Decimal256Test extends HugeDecimalTest {
  public Decimal256Test() {
    super("decimal256", new MathContext(Decimal256Column.MAX_PRECISION, RoundingMode.HALF_EVEN));
  }
}
