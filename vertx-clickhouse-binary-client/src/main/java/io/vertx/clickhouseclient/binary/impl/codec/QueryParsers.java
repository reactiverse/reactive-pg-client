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

import io.vertx.sqlclient.Tuple;

import java.time.temporal.Temporal;
import java.util.*;

//TODO: maybe switch to antlr4 or JavaCC + .jj file (see ClickHouseSqlParser.jj in regular ClickHouse jdbc driver)
public class QueryParsers {
  private static final String INSERT_KEYWORD = "INSERT";
  private static final String UPDATE_KEYWORD = "UPDATE";

  public static final Set<String> SELECT_KEYWORDS =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("SELECT", "WITH", "SHOW", "DESC", "EXISTS", "EXPLAIN")));
  public static final Set<String> SELECT_AND_MUTATE_KEYWORDS =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("SELECT", "WITH", "SHOW", "DESC", "EXISTS", "EXPLAIN",
    INSERT_KEYWORD, UPDATE_KEYWORD)));


  public static String insertParamValuesIntoQuery(String parametrizedSql, Tuple paramsList) {
    int prevIdx = 0;
    int newIdx;
    StringBuilder bldr = new StringBuilder();
    Set<Integer> usedArgs = new HashSet<>();
    while(prevIdx < parametrizedSql.length() && (newIdx = parametrizedSql.indexOf('$', prevIdx)) != -1) {
      if (newIdx == 0 || parametrizedSql.charAt(newIdx - 1) != '\\') {
        int paramIdxStartPos = newIdx + 1;
        int paramIdxEndPos = paramIdxStartPos;
        while (paramIdxEndPos < parametrizedSql.length() && Character.isDigit(parametrizedSql.charAt(paramIdxEndPos))) {
          ++paramIdxEndPos;
        }
        if (paramIdxStartPos == paramIdxEndPos) {
          throw new IllegalArgumentException("$ without digit at pos " + paramIdxStartPos + " in query " + parametrizedSql);
        }
        int paramIndex = Integer.parseInt(parametrizedSql.substring(paramIdxStartPos, paramIdxEndPos)) - 1;
        if (paramsList == null || paramIndex >= paramsList.size()) {
          throw new IllegalArgumentException("paramList is null or too small(" + (paramsList == null ? null : paramsList.size()) +
            ") for arg with index " + paramIndex);
        }
        Object paramValue = paramsList.getValue(paramIndex);
        bldr.append(parametrizedSql, prevIdx, newIdx);
        Class<?> paramClass = paramValue == null ? null : paramValue.getClass();
        if (paramClass != null) {
          if (CharSequence.class.isAssignableFrom(paramClass) || paramClass == Character.class || Temporal.class.isAssignableFrom(paramClass)) {
            bldr.append('\'').append(paramValue).append('\'');
          } else if (paramClass == Double.class) {
            //DB parser gets mad at 4.9e-322 or smaller. Using cast to cure
            bldr.append(String.format("CAST('%s', 'Float64')", paramValue.toString()));
          } else if (paramClass == Float.class) {
            bldr.append(String.format("CAST('%s', 'Float32')", paramValue.toString()));
          } else {
            bldr.append(paramValue);
          }
        } else {
          bldr.append(paramValue);
        }
        usedArgs.add(paramIndex);
        newIdx = paramIdxEndPos;
      }
      prevIdx = newIdx;
    }
    if (usedArgs.size() != paramsList.size()) {
      String msg = String.format("The number of parameters to execute should be consistent with the expected number of parameters = [%d] but the actual number is [%d].",
        usedArgs.size(), paramsList.size());
      throw new IllegalArgumentException(msg);
    }
    bldr.append(parametrizedSql, prevIdx, parametrizedSql.length());
    return bldr.toString();
  }

  public static boolean isSelect(String sql) {
    Map.Entry<String, Integer> tmp = findKeyWord(sql, 0, SELECT_KEYWORDS);
    return tmp != null;
  }

  public static Map.Entry<String, Integer> findKeyWord(String sql, Collection<String> keywords) {
    return findKeyWord(sql, 0, keywords);
  }

  public static Map.Entry<String, Integer> findKeyWord(String sql, int startPos, Collection<String> keywords) {
    Character requiredChar = null;
    //special case to find special chars, e.g. argument index $
    if (keywords.size() == 1) {
      String str = keywords.iterator().next();
      if (str.length() == 1) {
        requiredChar = str.charAt(0);
      }
    }
    for (int i = startPos; i < sql.length(); i++) {
      char ch = sql.charAt(i);
      String nextTwo = sql.substring(i, Math.min(i + 2, sql.length()));
      if ("--".equals(nextTwo)) {
        i = Math.max(i, sql.indexOf("\n", i));
      } else if ("/*".equals(nextTwo)) {
        i = Math.max(i, sql.indexOf("*/", i));
      } else if (ch == '\'' && (i == 0 || sql.charAt(i - 1) != '\\')) {
        while (i < sql.length() && !(sql.charAt(i) == '\'' && (i == 0 || sql.charAt(i - 1) != '\\'))) {
          ++i;
        }
      } else {
        if (requiredChar == null) {
          if (Character.isLetter(ch)) {
            String trimmed = sql.substring(i, Math.min(sql.length(), Math.max(i, sql.indexOf(" ", i))));
            for (String keyword : keywords) {
              if (trimmed.regionMatches(true, 0, keyword, 0, keyword.length())) {
                return new AbstractMap.SimpleEntry<>(keyword, i);
              }
            }
          }
        } else {
          if (requiredChar == ch) {
            return new AbstractMap.SimpleEntry<>(keywords.iterator().next(), i);
          }
        }
      }
    }
    return null;
  }

  public static Map<? extends Number, String> parseEnumValues(String nativeType) {
    final boolean isByte = nativeType.startsWith("Enum8(");
    int openBracketPos = nativeType.indexOf('(');
    Map<Number, String> result = new LinkedHashMap<>();
    int lastQuotePos = -1;
    boolean gotEq = false;
    String enumElementName = null;
    int startEnumValPos = -1;
    int signum = 1;
    for (int i = openBracketPos; i < nativeType.length(); ++i) {
      char ch = nativeType.charAt(i);
      if (ch == '\'') {
        if (lastQuotePos == -1) {
          lastQuotePos = i;
        } else {
          enumElementName = nativeType.substring(lastQuotePos + 1, i);
          lastQuotePos = -1;
        }
      } else if (ch == '=') {
        gotEq = true;
      } else if (gotEq) {
        if (ch == '-') {
          signum = -1;
        } if (Character.isDigit(ch)) {
          if (startEnumValPos == -1) {
            startEnumValPos = i;
          } else if (!Character.isDigit(nativeType.charAt(i + 1))) {
            int enumValue = Integer.parseInt(nativeType.substring(startEnumValPos, i + 1)) * signum;
            Number key = byteOrShort(enumValue, isByte);
            result.put(key, enumElementName);
            signum = 1;
            startEnumValPos = -1;
            enumElementName = null;
            gotEq = false;
          }
        } else if (startEnumValPos != -1) {
          int enumValue = Integer.parseInt(nativeType.substring(startEnumValPos, i)) * signum;
          Number key = byteOrShort(enumValue, isByte);
          result.put(key, enumElementName);
          signum = 1;
          startEnumValPos = -1;
          enumElementName = null;
          gotEq = false;
        }
      }
    }
    return result;
  }

  private static Number byteOrShort(int number, boolean isByte) {
    if (isByte) {
      return (byte) number;
    }
    return (short) number;
  }

  static int valuesPosForLoCaseSql(String sqlLoCase, int fromPos) {
    if (sqlLoCase.endsWith("values")) {
      return sqlLoCase.length() - "values".length();
    }
    Map.Entry<String, Integer> pos = findKeyWord(sqlLoCase, fromPos, Collections.singleton("$"));
    if (pos == null) {
      return -1;
    }
    return sqlLoCase.lastIndexOf("values", pos.getValue());
  }
}
