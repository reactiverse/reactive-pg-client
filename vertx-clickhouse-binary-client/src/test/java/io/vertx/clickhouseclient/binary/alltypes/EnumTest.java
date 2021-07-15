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

package io.vertx.clickhouseclient.binary.alltypes;

import io.vertx.sqlclient.Tuple;

import java.util.Arrays;
import java.util.List;

public abstract class EnumTest extends AllTypesBase<TestEnum> {
  public EnumTest(String tableSuffix) {
    this(tableSuffix, TestEnum.class);
  }

  private EnumTest(String tableSuffix, Class<TestEnum> cls) {
    super(tableSuffix, new MyColumnChecker<>(cls,
      (row, idx) -> row.get(cls, idx), (tp, name) -> tp.get(cls, name), null, null), false);
  }

  @Override
  public List<Tuple> createBatch() {
    TestEnum v1 = TestEnum.v0;
    TestEnum v2 = TestEnum.v1;
    TestEnum v3 = TestEnum.v2;
    TestEnum v4 = TestEnum.v3;
    TestEnum v5 = TestEnum.v4;
    TestEnum v6 = TestEnum.v5;
    TestEnum nv = TestEnum.v0;
    TestEnum mn = TestEnum.v3;
    TestEnum mx = TestEnum.v6;

    return Arrays.asList(
      //            id    simple_t    nullable_t   array_t                                              array3_t                                                                                                                   nullable_array_t                                           nullable_array3_t
      Tuple.of((byte)1,        mn,      mn,        new TestEnum[]{mn, mn},                             new TestEnum[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mn, mn},                                   new TestEnum[][][]{{{mn, null, mn}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                    ),
      Tuple.of((byte)2,        mn,      mn,        new TestEnum[]{mn, mn},                             new TestEnum[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mn, mn},                                   new TestEnum[][][]{{{mn, null, mn, null}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}              ),
      Tuple.of((byte)3,        mn,      mn,        new TestEnum[]{mn, mn},                             new TestEnum[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mn, null, mn},                             new TestEnum[][][]{{{mn, mn, null}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                    ),
      Tuple.of((byte)4,        mn,      mn,        new TestEnum[]{mn, mn},                             new TestEnum[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mn, null, mn},                             new TestEnum[][][]{{{mn, null, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                              ),
      Tuple.of((byte)5,        mx,      mx,        new TestEnum[]{mx, mx},                             new TestEnum[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mx, mx},                                   new TestEnum[][][]{{{mx, null, mx}, {mx, mx, null}, {}}, {{mn, mn}, {mn, null, mn}, {}}, {{}, {null}, {}}}                  ),
      Tuple.of((byte)6,        mn,      mn,        new TestEnum[]{mx, mx},                             new TestEnum[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mx, mx},                                   new TestEnum[][][]{{{mx, mx}, {mx, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{null}, {}, {}}}                              ),
      Tuple.of((byte)7,        mx,      mx,        new TestEnum[]{mx, mx},                             new TestEnum[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mx, null, mx},                             new TestEnum[][][]{{{mx, mx}, {mx, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                                  ),
      Tuple.of((byte)8,        mn,      mn,        new TestEnum[]{mx, mx},                             new TestEnum[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mx, null, mx},                             new TestEnum[][][]{{{mx, mx}, {mx, null, mx, null}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                        ),
      Tuple.of((byte)9,        mx,      mx,        new TestEnum[]{mn, mx},                             new TestEnum[][][]{{{mn, mx}, {mn, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mn, null, mx},                             new TestEnum[][][]{{{mn, mx, null}, {mn, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                            ),
      Tuple.of((byte)10,       mn,      mn,        new TestEnum[]{mn, mx},                             new TestEnum[][][]{{{mn, mx}, {mn, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{mn, null, mx},                             new TestEnum[][][]{{{mn, mx, null}, {mn, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                            ),
      Tuple.of((byte)11,       v2,      v3,        new TestEnum[]{},                                   new TestEnum[][][]{{{}, {}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                                 new TestEnum[]{},                                         new TestEnum[][][]{{{}, {}, {}}, {{}, {}, {}}, {{}, {}, {}}}                                                                ),
      Tuple.of((byte)12,       v2,      v3,        new TestEnum[]{},                                   new TestEnum[][][]{{{}, {}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                                 new TestEnum[]{},                                         new TestEnum[][][]{{{}, {}, {}}, {{}, {}, {}}, {{}, {}, {}}}                                                                ),
      Tuple.of((byte)13,       v2,      v3,        new TestEnum[]{},                                   new TestEnum[][][]{{{}}},                                                                                                 new TestEnum[]{},                                         new TestEnum[][][]{{{}}}                                                                                                    ),
      Tuple.of((byte)14,       v2,      v3,        new TestEnum[]{},                                   new TestEnum[][][]{{{}}},                                                                                                 new TestEnum[]{},                                         new TestEnum[][][]{{{}}}                                                                                                    ),
      Tuple.of((byte)15,       v2,      v3,        new TestEnum[]{},                                   new TestEnum[][][]{{{}}},                                                                                                 new TestEnum[]{null},                                     new TestEnum[][][]{{{null}}}                                                                                                ),
      Tuple.of((byte)16,       v2,      v3,        new TestEnum[]{},                                   new TestEnum[][][]{{{}}},                                                                                                 new TestEnum[]{null},                                     new TestEnum[][][]{{{null}}}                                                                                                ),
      Tuple.of((byte)17,       v2,      v3,        new TestEnum[]{nv},                                 new TestEnum[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{nv},                                       new TestEnum[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                                        ),
      Tuple.of((byte)18,       v2,      v3,        new TestEnum[]{nv},                                 new TestEnum[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new TestEnum[]{nv},                                       new TestEnum[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                                        ),
      Tuple.of((byte)19,       v2,      v3,        new TestEnum[]{nv, mn, mx},                         new TestEnum[][][]{{{v2, nv, mn}, {v3, mn, nv, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                         new TestEnum[]{nv, mn, null, mx},                         new TestEnum[][][]{{{v2, nv, mn, null}, {v3, mn, nv, mx}, {}}, {{mn, mn, null}, {mn, mn}, {}}, {{}, {}, {}}}                ),
      Tuple.of((byte)20,       v2,      v3,        new TestEnum[]{nv, mn, mx},                         new TestEnum[][][]{{{v2, nv, mn}, {v3, mn, nv, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                         new TestEnum[]{nv, mn, null, mx},                         new TestEnum[][][]{{{v2, nv, mn, null}, {v3, mn, nv, mx}, {}}, {{mn, mn, null}, {mn, mn}, {}}, {{}, {}, {}}}                ),
      Tuple.of((byte)21,       v2,      v3,        new TestEnum[]{nv, mn, mx},                         new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{nv, mn, null, mx},                         new TestEnum[][][]{{{nv, mn, mx, v1, v2, null, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)22,       v2,      v3,        new TestEnum[]{nv, mn, mx},                         new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{nv, mn, null, mx},                         new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, null, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)23, nv,    null,        new TestEnum[]{v3, v1, nv, mx, v4},                 new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{v3, v1, null, nv, mx, v3},                 new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5, null}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)24, nv,    null,        new TestEnum[]{v3, v1, nv, mx, v4},                 new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{v3, v1, null, nv, mx, v3},                 new TestEnum[][][]{{{nv, mn, mx, null, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)25,       v1,      v1,        new TestEnum[]{v1, nv, nv},                         new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{v3, nv, null},                             new TestEnum[][][]{{{nv, mn, null, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)26,       nv,      nv,        new TestEnum[]{nv, nv, nv},                         new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{nv, null, v4},                             new TestEnum[][][]{{{nv, null, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)27,       v6,      v5,        new TestEnum[]{v4, nv, nv},                         new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{v3, nv, null},                             new TestEnum[][][]{{{nv, mn, null, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)28,       v6,      v5,        new TestEnum[]{v1, nv, mn, mx, v2, v3, v4, v5, v6}, new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{v1, nv, mn, mx, v2, v3, v4, v5, v6, null}, new TestEnum[][][]{{{nv, mn, mx, v1, null, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          ),
      Tuple.of((byte)29,       v6,      v5,        new TestEnum[]{v1, nv, mn, mx, v2, v3, v4, v5, v6}, new TestEnum[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new TestEnum[]{v1, nv, mn, mx, v2, v3, v4, v5, v6, null}, new TestEnum[][][]{{{nv, mn, mx, v1, v2, null, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}          )
    );
  }
}

