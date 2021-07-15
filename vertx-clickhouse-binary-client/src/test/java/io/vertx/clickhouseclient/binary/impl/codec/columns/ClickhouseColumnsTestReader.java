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

import io.vertx.clickhouseclient.binary.impl.codec.QueryParsers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@RunWith(Parameterized.class)
public class ClickhouseColumnsTestReader<T extends Number> {
  private final String enumDefinition;
  private final java.util.function.Function<Integer, T> converter;

  public ClickhouseColumnsTestReader(String enumType, String enumDefinition, Function<Integer, T> converter) {
    this.enumDefinition = enumDefinition;
    this.converter = converter;
  }

  @Parameterized.Parameters(name = "{0}")
  public static Iterable<Object[]> dataForTest() {
    java.util.function.Function<Integer, Byte> byteConverter = Integer::byteValue;
    java.util.function.Function<Integer, Short> shortConverter = Integer::shortValue;
    return Arrays.asList(new Object[][]{
      {"Enum8", "Enum8('aa4' = 1, '1b3b2' = 22, '1b3b3' = 24,'1b3b4' = 25,'1b3b5'= 26,'1b3b6' =27,'1b3b7'=28,'neg1'=-1, 'neg2' = -22)", byteConverter},
      {"Enum16", "Enum16('aa4' = 1, '1b3b2' = 22, '1b3b3' = 24,'1b3b4' = 25,'1b3b5'= 26,'1b3b6' =27,'1b3b7'=28,'neg1'=-1, 'neg2' = -22)", shortConverter}
    });
  }

  private T key(Integer k) {
    return converter.apply(k);
  }

  @Test
  public void testParseEnumVals() {
    Map<? extends Number, String> vals = QueryParsers.parseEnumValues(enumDefinition);
    Map<T, String> expected = new HashMap<>();
    expected.put(key(1), "aa4");
    expected.put(key(22), "1b3b2");
    expected.put(key(24), "1b3b3");
    expected.put(key(25), "1b3b4");
    expected.put(key(26), "1b3b5");
    expected.put(key(27), "1b3b6");
    expected.put(key(28), "1b3b7");
    expected.put(key(-1), "neg1");
    expected.put(key(-22), "neg2");
    Assert.assertEquals(expected, vals);
  }
}
