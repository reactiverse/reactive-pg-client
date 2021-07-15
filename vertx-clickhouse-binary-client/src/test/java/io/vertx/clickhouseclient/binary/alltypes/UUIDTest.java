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
import java.util.UUID;

public class UUIDTest extends AllTypesBase<UUID> {
  public UUIDTest() {
    super("uuid", new MyColumnChecker<>(UUID.class, null, null, null, null), false);
  }

  @Override
  public List<Tuple> createBatch() {
    UUID v1 = new UUID(1000, 2000);
    UUID v2 = new UUID(-1000, -2000);
    UUID v3 = new UUID(Integer.MIN_VALUE, Integer.MIN_VALUE);
    UUID v4 = new UUID(Integer.MAX_VALUE, Integer.MAX_VALUE);
    UUID v5 = new UUID(Long.MIN_VALUE / 2 , Long.MIN_VALUE / 2);
    UUID v6 = new UUID(Long.MAX_VALUE / 2, Long.MAX_VALUE / 2);
    UUID nv = new UUID(0, 0);
    UUID mn = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);
    UUID mx = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);

    return Arrays.asList(
      //            id    simple_t    nullable_t   array_t                                         array3_t                                                                                                              nullable_array_t                                        nullable_array3_t
      Tuple.of((byte)1,        mn,      mn,        new UUID[]{mn, mn},                             new UUID[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mn, mn},                                   new UUID[][][]{{{mn, null, mn}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}            ),
      Tuple.of((byte)2,        mn,      mn,        new UUID[]{mn, mn},                             new UUID[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mn, mn},                                   new UUID[][][]{{{mn, null, mn, null}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}      ),
      Tuple.of((byte)3,        mn,      mn,        new UUID[]{mn, mn},                             new UUID[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mn, null, mn},                             new UUID[][][]{{{mn, mn, null}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}            ),
      Tuple.of((byte)4,        mn,      mn,        new UUID[]{mn, mn},                             new UUID[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mn, null, mn},                             new UUID[][][]{{{mn, null, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                      ),
      Tuple.of((byte)5,        mx,      mx,        new UUID[]{mx, mx},                             new UUID[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mx, mx},                                   new UUID[][][]{{{mx, null, mx}, {mx, mx, null}, {}}, {{mn, mn}, {mn, null, mn}, {}}, {{}, {null}, {}}}          ),
      Tuple.of((byte)6,        mn,      mn,        new UUID[]{mx, mx},                             new UUID[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mx, mx},                                   new UUID[][][]{{{mx, mx}, {mx, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{null}, {}, {}}}                      ),
      Tuple.of((byte)7,        mx,      mx,        new UUID[]{mx, mx},                             new UUID[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mx, null, mx},                             new UUID[][][]{{{mx, mx}, {mx, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                          ),
      Tuple.of((byte)8,        mn,      mn,        new UUID[]{mx, mx},                             new UUID[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mx, null, mx},                             new UUID[][][]{{{mx, mx}, {mx, null, mx, null}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                ),
      Tuple.of((byte)9,        mx,      mx,        new UUID[]{mn, mx},                             new UUID[][][]{{{mn, mx}, {mn, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mn, null, mx},                             new UUID[][][]{{{mn, mx, null}, {mn, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                    ),
      Tuple.of((byte)10,       mn,      mn,        new UUID[]{mn, mx},                             new UUID[][][]{{{mn, mx}, {mn, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{mn, null, mx},                             new UUID[][][]{{{mn, mx, null}, {mn, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                    ),
      Tuple.of((byte)11,       v2,      v3,        new UUID[]{},                                   new UUID[][][]{{{}, {}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                                 new UUID[]{},                                         new UUID[][][]{{{}, {}, {}}, {{}, {}, {}}, {{}, {}, {}}}                                                        ),
      Tuple.of((byte)12,       v2,      v3,        new UUID[]{},                                   new UUID[][][]{{{}, {}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                                 new UUID[]{},                                         new UUID[][][]{{{}, {}, {}}, {{}, {}, {}}, {{}, {}, {}}}                                                        ),
      Tuple.of((byte)13,       v2,      v3,        new UUID[]{},                                   new UUID[][][]{{{}}},                                                                                                 new UUID[]{},                                         new UUID[][][]{{{}}}                                                                                            ),
      Tuple.of((byte)14,       v2,      v3,        new UUID[]{},                                   new UUID[][][]{{{}}},                                                                                                 new UUID[]{},                                         new UUID[][][]{{{}}}                                                                                            ),
      Tuple.of((byte)15,       v2,      v3,        new UUID[]{},                                   new UUID[][][]{{{}}},                                                                                                 new UUID[]{null},                                     new UUID[][][]{{{null}}}                                                                                        ),
      Tuple.of((byte)16,       v2,      v3,        new UUID[]{},                                   new UUID[][][]{{{}}},                                                                                                 new UUID[]{null},                                     new UUID[][][]{{{null}}}                                                                                        ),
      Tuple.of((byte)17,       v2,      v3,        new UUID[]{nv},                                 new UUID[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{nv},                                       new UUID[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                                ),
      Tuple.of((byte)18,       v2,      v3,        new UUID[]{nv},                                 new UUID[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new UUID[]{nv},                                       new UUID[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                                ),
      Tuple.of((byte)19,       v2,      v3,        new UUID[]{nv, mn, mx},                         new UUID[][][]{{{v2, nv, mn}, {v3, mn, nv, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                         new UUID[]{nv, mn, null, mx},                         new UUID[][][]{{{v2, nv, mn, null}, {v3, mn, nv, mx}, {}}, {{mn, mn, null}, {mn, mn}, {}}, {{}, {}, {}}}        ),
      Tuple.of((byte)20,       v2,      v3,        new UUID[]{nv, mn, mx},                         new UUID[][][]{{{v2, nv, mn}, {v3, mn, nv, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                         new UUID[]{nv, mn, null, mx},                         new UUID[][][]{{{v2, nv, mn, null}, {v3, mn, nv, mx}, {}}, {{mn, mn, null}, {mn, mn}, {}}, {{}, {}, {}}}        ),
      Tuple.of((byte)21,       v2,      v3,        new UUID[]{nv, mn, mx},                         new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{nv, mn, null, mx},                         new UUID[][][]{{{nv, mn, mx, v1, v2, null, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)22,       v2,      v3,        new UUID[]{nv, mn, mx},                         new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{nv, mn, null, mx},                         new UUID[][][]{{{nv, mn, mx, v1, v2, v3, null, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)23, nv,    null,        new UUID[]{v3, v1, nv, mx, v4},                 new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{v3, v1, null, nv, mx, v3},                 new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5, null}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)24, nv,    null,        new UUID[]{v3, v1, nv, mx, v4},                 new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{v3, v1, null, nv, mx, v3},                 new UUID[][][]{{{nv, mn, mx, null, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)25,       v1,      v1,        new UUID[]{v1, nv, nv},                         new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{v3, nv, null},                             new UUID[][][]{{{nv, mn, null, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)26,       nv,      nv,        new UUID[]{nv, nv, nv},                         new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{nv, null, v4},                             new UUID[][][]{{{nv, null, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)27,       v6,      v5,        new UUID[]{v4, nv, nv},                         new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{v3, nv, null},                             new UUID[][][]{{{nv, mn, null, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)28,       v6,      v5,        new UUID[]{v1, nv, mn, mx, v2, v3, v4, v5, v6}, new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{v1, nv, mn, mx, v2, v3, v4, v5, v6, null}, new UUID[][][]{{{nv, mn, mx, v1, null, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)29,       v6,      v5,        new UUID[]{v1, nv, mn, mx, v2, v3, v4, v5, v6}, new UUID[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new UUID[]{v1, nv, mn, mx, v2, v3, v4, v5, v6, null}, new UUID[][][]{{{nv, mn, mx, v1, v2, null, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}   )
    );
  }
}
