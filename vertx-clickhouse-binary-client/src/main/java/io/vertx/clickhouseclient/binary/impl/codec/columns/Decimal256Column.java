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
import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

//Looks like support is experimental at the moment
public class Decimal256Column extends ClickhouseColumn {
  public static final int ELEMENT_SIZE = 32;
  public static final int MAX_PRECISION = 76;
  public static final MathContext MATH_CONTEXT = new MathContext(MAX_PRECISION, RoundingMode.HALF_EVEN);

  private final Numeric zeroValue;

  public Decimal256Column(ClickhouseBinaryColumnDescriptor descriptor) {
    super(descriptor);
    zeroValue = Numeric.create(new BigDecimal(BigInteger.ZERO, descriptor.getPrecision(), MATH_CONTEXT));
  }

  @Override
  public ClickhouseColumnReader reader(int nRows) {
    return new GenericDecimalColumnReader(nRows, descriptor, MATH_CONTEXT);
  }

  @Override
  public ClickhouseColumnWriter writer(List<Tuple> data, int columnIndex) {
    return new GenericDecimalColumnWriter(data, descriptor, columnIndex);
  }

  public Object nullValue() {
    return zeroValue;
  }

  @Override
  public Object[] emptyArray() {
    return Decimal128Column.EMPTY_ARRAY;
  }
}