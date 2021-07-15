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

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EnumResolutionMethod {
  NAME("by_name"), ORDINAL("by_ordinal"), KEY("by_key_as_ordinal");
  private static final Map<String, EnumResolutionMethod> index = Arrays
    .stream(EnumResolutionMethod.values())
    .collect(Collectors.toMap(el -> el.optVal, el -> el));

  String optVal;

  EnumResolutionMethod(String optVal) {
    this.optVal = optVal;
  }

  public static EnumResolutionMethod forOpt(String optVal) {
    EnumResolutionMethod ret = index.get(optVal);
    if (ret == null) {
      throw new IllegalArgumentException("unknown option value " + optVal);
    }
    return ret;
  }
}
