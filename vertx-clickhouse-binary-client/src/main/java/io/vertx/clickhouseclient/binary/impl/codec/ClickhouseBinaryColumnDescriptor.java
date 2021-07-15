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

import io.vertx.sqlclient.desc.ColumnDescriptor;

import java.math.BigInteger;
import java.sql.JDBCType;

public class ClickhouseBinaryColumnDescriptor implements ColumnDescriptor {
  public static final int NOSIZE = -1;

  private final String name;
  private final String unparsedNativeType;
  private final String nestedType;
  private final JDBCType jdbcType;
  private final int elementSize;
  private final boolean isArray;
  private final boolean nullable;
  private final boolean unsigned;
  private final boolean lowCardinality;
  private final BigInteger minValue;
  private final BigInteger maxValue;

  private final Integer precision;
  private final Integer scale;

  private final int arrayDimensionsCount;
  private final ClickhouseBinaryColumnDescriptor nested;

  public ClickhouseBinaryColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue) {
    this(name, unparsedNativeType, nestedType, isArray, elementSize, jdbcType, nullable, unsigned, lowCardinality,
      minValue, maxValue, null, null, -1, null);
  }

  public ClickhouseBinaryColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue,
                                          int arrayDimensionsCount, ClickhouseBinaryColumnDescriptor nested) {
    this(name, unparsedNativeType, nestedType, isArray, elementSize, jdbcType, nullable, unsigned, lowCardinality,
      minValue, maxValue, null, null, arrayDimensionsCount, nested);
  }

  public ClickhouseBinaryColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue,
                                          Integer precision, Integer scale) {
    this(name, unparsedNativeType, nestedType, isArray, elementSize, jdbcType, nullable, unsigned, lowCardinality,
      minValue, maxValue, precision, scale, -1, null);
  }

  public ClickhouseBinaryColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue,
                                          Integer precision, Integer scale,
                                          int arrayDimensionsCount, ClickhouseBinaryColumnDescriptor nested) {
    this.name = name;
    this.unparsedNativeType = unparsedNativeType;
    this.nestedType = nestedType;
    this.isArray = isArray;
    this.elementSize = elementSize;
    this.jdbcType = jdbcType;
    this.nullable = nullable;
    this.unsigned = unsigned;
    this.lowCardinality = lowCardinality;
    this.minValue = bi(minValue);
    this.maxValue = bi(maxValue);
    this.precision = precision;
    this.scale = scale;
    this.arrayDimensionsCount = arrayDimensionsCount;
    this.nested = nested;
  }

  private BigInteger bi(Number src) {
    if (src instanceof Byte || src instanceof Integer || src instanceof Long) {
      return BigInteger.valueOf(src.longValue());
    }
    return (BigInteger) src;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean isArray() {
    return isArray;
  }

  public int arrayDimensionsCount() {
    return arrayDimensionsCount;
  }

  @Override
  public JDBCType jdbcType() {
    return jdbcType;
  }

  public String getUnparsedNativeType() {
    return unparsedNativeType;
  }

  public int getElementSize() {
    return elementSize;
  }

  public boolean isNullable() {
    return nullable;
  }

  public boolean isUnsigned() {
    return unsigned;
  }

  public boolean isLowCardinality() {
    return lowCardinality;
  }

  public BigInteger getMinValue() {
    return minValue;
  }

  public BigInteger getMaxValue() {
    return maxValue;
  }

  public String getNestedType() {
    return nestedType;
  }

  public ClickhouseBinaryColumnDescriptor getNestedDescr() {
    return nested;
  }

  public Integer getPrecision() {
    return precision;
  }

  public Integer getScale() {
    return scale;
  }

  public ClickhouseBinaryColumnDescriptor copyWithModifiers(boolean newArray, boolean newLowCardinality, boolean newNullable) {
    return new ClickhouseBinaryColumnDescriptor(name, unparsedNativeType, nestedType, newArray, elementSize, jdbcType,
      newNullable, unsigned, newLowCardinality, minValue, maxValue, precision, scale, arrayDimensionsCount, nested);
  }

  public ClickhouseBinaryColumnDescriptor copyWithModifiers(boolean newLowCardinality, boolean newNullable) {
    return copyWithModifiers(isArray, newLowCardinality, newNullable);
  }

  @Override
  public String toString() {
    return "ClickhouseNativeColumnDescriptor{" +
      "name='" + name + '\'' +
      ", unparsedNativeType='" + unparsedNativeType + '\'' +
      ", nativeType='" + nestedType + '\'' +
      ", isArray=" + isArray +
      ", jdbcType=" + jdbcType +
      ", elementSize=" + elementSize +
      ", nullable=" + nullable +
      '}';
  }
}
