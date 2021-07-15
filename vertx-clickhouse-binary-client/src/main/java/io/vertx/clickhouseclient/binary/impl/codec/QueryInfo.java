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

import java.util.Map;

public class QueryInfo {
  public static final int VALUES_LENGTH = "values".length();

  private final String typeKeyword;
  private final Integer typeKeywordPos;
  private final boolean isInsert;
  private final String queryEndingWithValues;

  QueryInfo(String sql, String typeKeyword, Integer typeKeywordPos) {
    this.typeKeyword = typeKeyword;
    this.typeKeywordPos = typeKeywordPos;
    this.isInsert = "insert".equalsIgnoreCase(typeKeyword);
    this.queryEndingWithValues = isInsert ? findValuesAndTruncate(sql, typeKeywordPos) : null;
  }

  public String typeKeyword() {
    return typeKeyword;
  }

  public Integer typeKeywordPos() {
    return typeKeywordPos;
  }

  public boolean isInsert() {
    return isInsert;
  }

  public boolean hasValues() {
    return queryEndingWithValues != null;
  }

  public String queryEndingWithValues() {
    return queryEndingWithValues;
  }

  private String findValuesAndTruncate(String sql, int typeKeywordPos) {
    String loCaseSql = sql.toLowerCase();
    boolean endsWithVals = loCaseSql.endsWith("values");
    if (endsWithVals) {
      return sql;
    } else {
      //TODO: make sure there are placeholders only, maybe count placeholders count
      int valuesIndex = QueryParsers.valuesPosForLoCaseSql(loCaseSql, typeKeywordPos);
      if (valuesIndex != -1) {
        return sql.substring(0, valuesIndex + VALUES_LENGTH);
      } else {
        return null;
      }
    }
  }

  public static QueryInfo parse(String query) {
    Map.Entry<String, Integer> qInfo = QueryParsers.findKeyWord(query, QueryParsers.SELECT_AND_MUTATE_KEYWORDS);
    return new QueryInfo(query, qInfo == null ? null : qInfo.getKey(), qInfo == null ? null : qInfo.getValue());
  }
}
