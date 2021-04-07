package io.vertx.clickhouse.clickhousenative.impl.codec;

import io.vertx.sqlclient.desc.ColumnDescriptor;

import java.math.BigInteger;
import java.sql.JDBCType;

public class ClickhouseNativeColumnDescriptor implements ColumnDescriptor {
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

  private final int arrayDepth;
  private final ClickhouseNativeColumnDescriptor nested;

  public ClickhouseNativeColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue) {
    this(name, unparsedNativeType, nestedType, isArray, elementSize, jdbcType, nullable, unsigned, lowCardinality,
      minValue, maxValue, null, null, -1, null);
  }

  public ClickhouseNativeColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue,
                                          int arrayDepth, ClickhouseNativeColumnDescriptor nested) {
    this(name, unparsedNativeType, nestedType, isArray, elementSize, jdbcType, nullable, unsigned, lowCardinality,
      minValue, maxValue, null, null, arrayDepth, nested);
  }

  public ClickhouseNativeColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue,
                                          Integer precision, Integer scale) {
    this(name, unparsedNativeType, nestedType, isArray, elementSize, jdbcType, nullable, unsigned, lowCardinality,
      minValue, maxValue, precision, scale, -1, null);
  }

  public ClickhouseNativeColumnDescriptor(String name, String unparsedNativeType, String nestedType,
                                          boolean isArray, int elementSize, JDBCType jdbcType,
                                          boolean nullable, boolean unsigned,
                                          boolean lowCardinality, Number minValue, Number maxValue,
                                          Integer precision, Integer scale,
                                          int arrayDepth, ClickhouseNativeColumnDescriptor nested) {
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
    this.arrayDepth = arrayDepth;
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

  public int arrayDepth() {
    return arrayDepth;
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

  public ClickhouseNativeColumnDescriptor getNestedDescr() {
    return nested;
  }

  public Integer getPrecision() {
    return precision;
  }

  public Integer getScale() {
    return scale;
  }

  public ClickhouseNativeColumnDescriptor copyWithModifiers(boolean newArray, boolean newLowCardinality, boolean newNullable) {
    return new ClickhouseNativeColumnDescriptor(name, unparsedNativeType, nestedType, newArray, elementSize, jdbcType,
      newNullable, unsigned, newLowCardinality, minValue, maxValue, precision, scale, arrayDepth, nested);
  }

  public ClickhouseNativeColumnDescriptor copyWithModifiers(boolean newLowCardinality, boolean newNullable) {
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
