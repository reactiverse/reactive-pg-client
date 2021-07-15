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

import io.vertx.clickhouseclient.binary.ClickhouseConstants;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.QueryParsers;

import java.math.BigInteger;
import java.sql.JDBCType;
import java.time.Duration;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClickhouseColumns {
  public static final String NULLABLE_PREFIX = "Nullable(";
  public static final int NULLABLE_PREFIX_LENGTH = NULLABLE_PREFIX.length();

  public static final String ARRAY_PREFIX = "Array(";
  public static final int ARRAY_PREFIX_LENGTH = ARRAY_PREFIX.length();

  public static final String LOW_CARDINALITY_PREFIX = "LowCardinality(";
  public static final int LOW_CARDINALITY_PREFIX_LENGTH = LOW_CARDINALITY_PREFIX.length();

  public static final String FIXED_STRING_PREFIX = "FixedString(";
  public static final int FIXED_STRING_PREFIX_LENGTH = FIXED_STRING_PREFIX.length();

  public static final String DECIMAL_PREFIX = "Decimal(";
  public static final int DECIMAL_PREFIX_LENGTH = DECIMAL_PREFIX.length();

  public static final String ENUM_PREFIX = "Enum";
  public static final int ENUM_PREFIX_LENGTH = ENUM_PREFIX.length();

  public static final String INTERVAL_PREFIX = "Interval";

  private static final Map<String, Duration> CONST_DURATION_MULTIPLIERS = Collections.unmodifiableMap(buildConstDurationMultipliers());

  private static Map<String, Duration> buildConstDurationMultipliers() {
    HashMap<String, Duration> result = new HashMap<>();
    result.put("Second", Duration.ofSeconds(1));
    result.put("Day", Duration.ofDays(1));
    result.put("Hour", Duration.ofHours(1));
    result.put("Minute", Duration.ofMinutes(1));
    result.put("Week", Duration.ofDays(7));
    return result;
  }


  public static ClickhouseBinaryColumnDescriptor columnDescriptorForSpec(String unparsedSpec, String name) {
    String spec = unparsedSpec;
    Map.Entry<Integer, String> arrayDimensionsInfo = maybeUnwrapArrayDimensions(spec);
    if (arrayDimensionsInfo.getKey() > 0) {
      ClickhouseBinaryColumnDescriptor nested = columnDescriptorForSpec(arrayDimensionsInfo.getValue(), name);
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, true, ClickhouseBinaryColumnDescriptor.NOSIZE,
        JDBCType.ARRAY, false, false, false, null, null, arrayDimensionsInfo.getKey(), nested);
    }
    boolean isLowCardinality = false;
    if (spec.startsWith(LOW_CARDINALITY_PREFIX)) {
      spec = spec.substring(LOW_CARDINALITY_PREFIX_LENGTH, spec.length() - 1);
      isLowCardinality = true;
    }
    boolean nullable = false;
    if (spec.startsWith(NULLABLE_PREFIX)) {
      spec = spec.substring(NULLABLE_PREFIX_LENGTH, spec.length() - 1);
      nullable = true;
    }
    return columnDescriptorForSpec(unparsedSpec, spec, name, nullable, false, isLowCardinality);
  }

  private static Map.Entry<Integer, String> maybeUnwrapArrayDimensions(String spec) {
    int arrayDepth = 0;
    while (spec.startsWith(ARRAY_PREFIX)) {
      spec = spec.substring(ARRAY_PREFIX_LENGTH, spec.length() - 1);
      ++arrayDepth;
    }
    return new AbstractMap.SimpleEntry<>(arrayDepth, spec);
  }

  public static ClickhouseBinaryColumnDescriptor columnDescriptorForSpec(String unparsedSpec, String spec, String name,
                                                                         boolean nullable, boolean isArray,
                                                                         boolean isLowCardinality) {
    boolean unsigned = spec.startsWith("U");
    if (spec.equals("UInt8") || spec.equals("Int8")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, UInt8ColumnReader.ELEMENT_SIZE, JDBCType.TINYINT, nullable, unsigned, isLowCardinality,
        unsigned ? 0 : -128, unsigned ? 255 : 127);
    } else if (spec.equals("UInt16") || spec.equals("Int16")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, UInt16ColumnReader.ELEMENT_SIZE, JDBCType.SMALLINT, nullable, unsigned, isLowCardinality,
        unsigned ? 0 : -32768, unsigned ? 65535 : 32767);
    } if (spec.equals("UInt32") || spec.equals("Int32")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec,  isArray, UInt32ColumnReader.ELEMENT_SIZE, JDBCType.INTEGER, nullable, unsigned, isLowCardinality,
        unsigned ? 0 : -2147483648L, unsigned ? 4294967295L : 2147483647L);
    } if (spec.equals("UInt64") || spec.equals("Int64")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec,  isArray, UInt64ColumnReader.ELEMENT_SIZE, JDBCType.BIGINT, nullable, unsigned, isLowCardinality,
        unsigned ? BigInteger.ZERO : new BigInteger("-9223372036854775808"),
        unsigned ? new BigInteger("18446744073709551615") : new BigInteger("9223372036854775807"));
    } if (spec.equals("Int128")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec,  isArray, Int128Column.ELEMENT_SIZE, JDBCType.BIGINT, nullable, false, isLowCardinality,
        Int128Column.INT128_MIN_VALUE, Int128Column.INT128_MAX_VALUE);
    } else if (spec.equals("String")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, ClickhouseBinaryColumnDescriptor.NOSIZE, JDBCType.VARCHAR,
        nullable, false, isLowCardinality, null, null);
    } else if (spec.startsWith(FIXED_STRING_PREFIX)) {
      String lengthStr = spec.substring(FIXED_STRING_PREFIX_LENGTH, spec.length() - 1);
      int bytesLength = Integer.parseInt(lengthStr);
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, bytesLength, JDBCType.VARCHAR,
        nullable, false, isLowCardinality, null, null);
    } else if (spec.startsWith("DateTime64")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, DateTime64Column.ELEMENT_SIZE,
        spec.endsWith(")") ? JDBCType.TIMESTAMP_WITH_TIMEZONE : JDBCType.TIMESTAMP, nullable, false, isLowCardinality, null, null);
    } else if (spec.startsWith("DateTime")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, DateTimeColumnReader.ELEMENT_SIZE,
        spec.endsWith(")") ? JDBCType.TIMESTAMP_WITH_TIMEZONE : JDBCType.TIMESTAMP, nullable, false, isLowCardinality, null, null);
    } else if (spec.equals("UUID")) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, UUIDColumn.ELEMENT_SIZE,
        JDBCType.OTHER, nullable, false, isLowCardinality, null, null);
    } else if (spec.startsWith(DECIMAL_PREFIX)) {
      String decimalModifiers = spec.substring(DECIMAL_PREFIX_LENGTH, spec.length() - 1);
      String[] modifiersTokens = decimalModifiers.split(",");
      int precision = Integer.parseInt(modifiersTokens[0].trim());
      int scale = Integer.parseInt(modifiersTokens[1].trim());
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, decimalSize(precision),
        JDBCType.DECIMAL, nullable, false, isLowCardinality, null, null, precision, scale);
    } else if (spec.startsWith(ENUM_PREFIX)) {
      int openBracketPos = spec.indexOf('(', ENUM_PREFIX_LENGTH);
      int enumBitsSize = Integer.parseInt(spec.substring(ENUM_PREFIX_LENGTH, openBracketPos));
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, enumBitsSize / 8,
        JDBCType.OTHER, nullable, false, isLowCardinality, null, null, null, null);
    } else if ("Nothing".equals(spec)) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, UInt8ColumnReader.ELEMENT_SIZE,
        JDBCType.NULL, nullable, false, isLowCardinality, null, null, null, null);
    } else if ("Float32".equals(spec)) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, Float32ColumnReader.ELEMENT_SIZE,
        JDBCType.REAL, nullable, false, isLowCardinality, null, null, null, null);
    } else if ("Float64".equals(spec)) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, Float64ColumnReader.ELEMENT_SIZE,
        JDBCType.DOUBLE, nullable, false, isLowCardinality, null, null, null, null);
    } else if ("Date".equals(spec)) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, UInt16ColumnReader.ELEMENT_SIZE,
        JDBCType.DATE, nullable, true, isLowCardinality, 0, 65535, null, null);
    } else if (spec.startsWith(INTERVAL_PREFIX)) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, UInt64ColumnReader.ELEMENT_SIZE,
        JDBCType.OTHER, nullable, false, isLowCardinality, null, null, null, null);
    } else if ("IPv4".equals(spec)) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, IPv4Column.ELEMENT_SIZE,
        JDBCType.OTHER, nullable, true, isLowCardinality, null, null, null, null);
    } else if ("IPv6".equals(spec)) {
      return new ClickhouseBinaryColumnDescriptor(name, unparsedSpec, spec, isArray, IPv6Column.ELEMENT_SIZE,
        JDBCType.OTHER, nullable, true, isLowCardinality, null, null, null, null);
    }
    throw new IllegalArgumentException("unknown column spec: '" + spec + "'");
  }

  private static int decimalSize(int precision) {
    if (precision <= Decimal32Column.MAX_PRECISION) {
      return Decimal32Column.ELEMENT_SIZE;
    } else if (precision <= Decimal64Column.MAX_PRECISION) {
      return Decimal64Column.ELEMENT_SIZE;
    } else if (precision <= Decimal128Column.MAX_PRECISION) {
      return Decimal128Column.ELEMENT_SIZE;
    } else if (precision <= Decimal256Column.MAX_PRECISION ){
      return Decimal256Column.ELEMENT_SIZE;
    } else {
      throw new IllegalArgumentException("precision is too large: " + precision);
    }
  }

  public static ClickhouseColumn columnForSpec(String spec, String name, ClickhouseBinaryDatabaseMetadata md) {
    return columnForSpec(spec, name, md, false);
  }

  public static ClickhouseColumn columnForSpec(String spec, String name, ClickhouseBinaryDatabaseMetadata md, boolean enableStringCache) {
    ClickhouseBinaryColumnDescriptor descr = ClickhouseColumns.columnDescriptorForSpec(spec, name);
    return columnForSpec(descr, md, enableStringCache);
  }

  public static ClickhouseColumn columnForSpec(ClickhouseBinaryColumnDescriptor descr, ClickhouseBinaryDatabaseMetadata md) {
    return columnForSpec(descr, md, false);
  }

  public static ClickhouseColumn columnForSpec(ClickhouseBinaryColumnDescriptor descr, ClickhouseBinaryDatabaseMetadata md, boolean enableStringCache) {
    if (descr.isArray()) {
      return new ArrayColumn(descr, md);
    }
    if (descr.isLowCardinality()) {
      return new LowCardinalityColumn(descr, md);
    }
    JDBCType jdbcType = descr.jdbcType();
    if (jdbcType == JDBCType.TINYINT || jdbcType == JDBCType.NULL) {
      return new UInt8Column(descr);
    } else if (jdbcType == JDBCType.SMALLINT) {
      return new UInt16Column(descr);
    } else if (jdbcType == JDBCType.INTEGER) {
      return new UInt32Column(descr);
    } else if (jdbcType == JDBCType.BIGINT) {
      if (descr.getElementSize() == UInt64ColumnReader.ELEMENT_SIZE) {
        return new UInt64Column(descr);
      } else if (descr.getElementSize() == Int128Column.ELEMENT_SIZE) {
        return new Int128Column(descr);
      }
    } else if (jdbcType == JDBCType.VARCHAR) {
      if (descr.getElementSize() == ClickhouseBinaryColumnDescriptor.NOSIZE) {
        return new StringColumn(descr, md, enableStringCache);
      } else {
        return new FixedStringColumn(descr, md, enableStringCache);
      }
    } else if (jdbcType == JDBCType.TIMESTAMP || jdbcType == JDBCType.TIMESTAMP_WITH_TIMEZONE) {
      ZoneId zoneId;
      Integer precision = null;
      String nativeType = descr.getNestedType();
      if (nativeType.endsWith(")")) {
        int openBracePos = nativeType.indexOf("(");
        String dateModifiers = nativeType.substring(openBracePos + 1, nativeType.length() - 1);
        if (descr.getElementSize() == DateTime64Column.ELEMENT_SIZE) {
          String[] modifiers = dateModifiers.split(",");
          precision = Integer.parseInt(modifiers[0].trim());
          if (modifiers.length == 2) {
            String id = modifiers[1].trim();
            id = id.substring(1, id.length() - 1);
            zoneId = ZoneId.of(id);
          } else {
            zoneId = md.getDefaultZoneId();
          }
        } else {
          zoneId = ZoneId.of(dateModifiers);
        }
      } else {
        zoneId = md.getDefaultZoneId();
      }
      return precision == null ? new DateTimeColumn(descr, zoneId) : new DateTime64Column(descr, precision, md.isSaturateExtraNanos(), zoneId);
    } else if (jdbcType == JDBCType.DECIMAL) {
      if (descr.getElementSize() == Decimal32Column.ELEMENT_SIZE) {
        return new Decimal32Column(descr);
      } else if (descr.getElementSize() == Decimal64Column.ELEMENT_SIZE) {
        return new Decimal64Column(descr);
      } else if (descr.getElementSize() == Decimal128Column.ELEMENT_SIZE) {
        return new Decimal128Column(descr);
      } else if (descr.getElementSize() == Decimal256Column.ELEMENT_SIZE) {
        return new Decimal256Column(descr);
      }
    } else if (jdbcType == JDBCType.REAL) {
      return new Float32Column(descr);
    } else if (jdbcType == JDBCType.DOUBLE) {
      return new Float64Column(descr);
    } else if (jdbcType == JDBCType.DATE) {
      return new DateColumn(descr);
    } else if (jdbcType == JDBCType.OTHER) {
      String nativeType = descr.getNestedType();
      if (nativeType.equals("UUID")) {
        return new UUIDColumn(descr);
      } else if (nativeType.startsWith(ENUM_PREFIX)) {
        Map<? extends Number, String> enumVals = QueryParsers.parseEnumValues(nativeType);
        String enumResolutionStr = md.getProperties().get(ClickhouseConstants.OPTION_ENUM_RESOLUTION);
        EnumResolutionMethod resolutionMethod = enumResolutionStr == null ? EnumResolutionMethod.ORDINAL : EnumResolutionMethod.forOpt(enumResolutionStr);
        if (descr.getElementSize() == Enum8ColumnReader.ELEMENT_SIZE) {
          return new Enum8Column(descr, enumVals, resolutionMethod);
        } else if (descr.getElementSize() == Enum16ColumnReader.ELEMENT_SIZE) {
          return new Enum16Column(descr, enumVals, resolutionMethod);
        }
      } else if (nativeType.startsWith(INTERVAL_PREFIX)) {
        Duration multiplier = getDurationMultiplier(descr, md);
        if (multiplier == null) {
          throw new IllegalArgumentException("unknown duration specifier in spec: " + descr.getUnparsedNativeType());
        }
        return new IntervalColumn(descr, multiplier);
      } else if (nativeType.equals("IPv4")) {
        return new IPv4Column(descr);
      } else if (nativeType.equals("IPv6")) {
        return new IPv6Column(descr, md);
      }
    }
    throw new IllegalArgumentException("no column type for jdbc type " + jdbcType + " (raw type: '" + descr.getUnparsedNativeType() + "')");
  }

  private static Duration getDurationMultiplier(ClickhouseBinaryColumnDescriptor descr, ClickhouseBinaryDatabaseMetadata md) {
    String durationStr = descr.getNestedType().substring(INTERVAL_PREFIX.length());
    Duration multiplier = CONST_DURATION_MULTIPLIERS.get(durationStr);
    if (multiplier == null) {
      switch (durationStr) {
        case "Year":
          return md.yearDuration();
        case "Quarter":
          return md.quarterDuration();
        case "Month":
          return md.monthDuration();
      }
    }
    return multiplier;
  }
}
