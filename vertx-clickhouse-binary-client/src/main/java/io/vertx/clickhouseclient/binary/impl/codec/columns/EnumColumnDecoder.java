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

import java.util.HashMap;
import java.util.Map;

class EnumColumnDecoder {
  private final Map<? extends Number, String> enumKeyToName;
  private final Map<Number, Integer> enumKeyToOrdinal;
  private final EnumResolutionMethod resolutionMethod;

  EnumColumnDecoder(Map<? extends Number, String> enumVals, EnumResolutionMethod resolutionMethod) {
    this.enumKeyToName = enumVals;
    this.resolutionMethod = resolutionMethod;
    this.enumKeyToOrdinal = resolutionMethod == EnumResolutionMethod.ORDINAL ? buildEnumKeyToOrdinal(enumVals) : null;
  }

  static Map<Number, Integer> buildEnumKeyToOrdinal(Map<? extends Number, String> enumVals) {
    Map<Number, Integer> ret = new HashMap<>();
    int idx = 0;
    for (Map.Entry<? extends Number, String> entry : enumVals.entrySet()) {
      ret.put(entry.getKey(), idx);
      ++idx;
    }
    return ret;
  }

  public Object recodeElement(Number key, Class desired) {
    if (Number.class.isAssignableFrom(desired)) {
      return key;
    }
    String str = enumKeyToName.get(key);
    if (desired.isEnum()) {
      if (resolutionMethod == EnumResolutionMethod.NAME) {
        return Enum.valueOf(desired, str);
      } else if (resolutionMethod == EnumResolutionMethod.KEY) {
        return desired.getEnumConstants()[key.intValue()];
      } else {
        return desired.getEnumConstants()[enumKeyToOrdinal.get(key)];
      }
    }
    return str;
  }

  Object[] recodeValues(Object[] src, Class desired) {
    Number[] bytes = (Number[])src;
    if (desired == Object.class) {
      desired = String.class;
    }
    Object[] ret = (Object[]) java.lang.reflect.Array.newInstance(desired, src.length);
    for (int idx = 0; idx < ret.length; ++idx) {
      Number el = bytes[idx];
      if (el != null) {
        ret[idx] = recodeElement(el, desired);
      }
    }
    return ret;
  }
}
