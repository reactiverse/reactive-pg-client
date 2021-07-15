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

public class EnumColumnEncoder {
  private final Number[] enumOrdinalToKey;
  private final Map<String, ? extends Number> enumNameToKey;
  private final EnumResolutionMethod resolutionMethod;

  public EnumColumnEncoder(Map<? extends Number, String> enumKeyToName, EnumResolutionMethod resolutionMethod) {
    this.resolutionMethod = resolutionMethod;
    this.enumNameToKey = resolutionMethod == EnumResolutionMethod.NAME ? buildReverseIndex(enumKeyToName) : null;
    this.enumOrdinalToKey = resolutionMethod == EnumResolutionMethod.NAME ? null : enumOrdinalToKey(enumKeyToName);
  }

  private <R extends Number> Number[] enumOrdinalToKey(Map<R, String> enumVals) {
    Number[] ret = new Number[enumVals.size()];
    int idx = 0;
    for (Map.Entry<R, String> entry:enumVals.entrySet()) {
      ret[idx] = entry.getKey();
      ++idx;
    }
    return ret;
  }

  private <R extends Number> Map<String, R> buildReverseIndex(Map<R, String> enumVals) {
    Map<String, R> ret = new HashMap<>();
    for (Map.Entry<R, String> entry : enumVals.entrySet()) {
      ret.put(entry.getValue(), entry.getKey());
    }
    return ret;
  }

  public Number encode(Object val) {
    Number key;
    if (val.getClass() == String.class) {
      key = enumNameToKey.get(val);
    } else if (val.getClass().isEnum()) {
      Enum enumVal = (Enum) val;
      if (resolutionMethod == EnumResolutionMethod.NAME) {
        key = enumNameToKey.get(enumVal.name());
      } else {
        int ordinal = enumVal.ordinal();
        if (ordinal < enumOrdinalToKey.length) {
          key = enumOrdinalToKey[ordinal];
        } else {
          throw new IllegalArgumentException("ordinal " + ordinal + " for enum val " + enumVal + " is too big, max " + (enumOrdinalToKey.length - 1));
        }
      }
    } else if (val instanceof Number) {
      key = (Number) val;
    } else {
      throw new IllegalArgumentException("don't know how to serialize " + val + " of class " + val.getClass());
    }
    if (key == null) {
      throw new IllegalArgumentException(val + " is not in dictionary; possible values: " + enumNameToKey.keySet());
    }
    return key;
  }
}
