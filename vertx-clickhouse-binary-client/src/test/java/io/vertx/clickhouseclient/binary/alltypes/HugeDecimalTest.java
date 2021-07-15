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


import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.data.Numeric;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;

@RunWith(VertxUnitRunner.class)
public abstract class HugeDecimalTest extends AllTypesBase<Numeric> {
  public static final int SCALE = 4;
  public final MathContext mc;

  public HugeDecimalTest(String tableSuffix, MathContext mc) {
    super(tableSuffix, new MyColumnChecker<>(Numeric.class, Tuple::getNumeric, Row::getNumeric, Tuple::getArrayOfNumerics, Row::getArrayOfNumerics), false);
    this.mc = mc;
  }

  private Numeric nm(Long src) {
    BigInteger bi = BigInteger.valueOf(src);
    BigDecimal bd = new BigDecimal(bi, SCALE, mc);
    return Numeric.create(bd);
  }

  @Override
  public List<Tuple> createBatch() {
    long mnl = Long.MIN_VALUE;
    long mxl = Long.MAX_VALUE;

    Numeric v1 = nm(mxl / 3);
    Numeric v2 = nm(mnl / 4);
    Numeric v3 = nm(mxl / 5);
    Numeric v4 = nm(mnl / 6);
    Numeric v5 = nm(mxl / 7);
    Numeric v6 = nm(mnl / 7);
    Numeric nv = nm(0L);
    Numeric mn = nm(mnl);
    Numeric mx = nm(mxl);

    return Arrays.asList(
      //            id    simple_t    nullable_t   array_t                                            array3_t                                                                                                                 nullable_array_t                                         nullable_array3_t
      Tuple.of((byte)1,        mn,      mn,        new Numeric[]{mn, mn},                             new Numeric[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mn, mn},                                   new Numeric[][][]{{{mn, null, mn}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}            ),
      Tuple.of((byte)2,        mn,      mn,        new Numeric[]{mn, mn},                             new Numeric[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mn, mn},                                   new Numeric[][][]{{{mn, null, mn, null}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}      ),
      Tuple.of((byte)3,        mn,      mn,        new Numeric[]{mn, mn},                             new Numeric[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mn, null, mn},                             new Numeric[][][]{{{mn, mn, null}, {mn, mn, null}, {null}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}            ),
      Tuple.of((byte)4,        mn,      mn,        new Numeric[]{mn, mn},                             new Numeric[][][]{{{mn, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mn, null, mn},                             new Numeric[][][]{{{mn, null, mn}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                      ),
      Tuple.of((byte)5,        mx,      mx,        new Numeric[]{mx, mx},                             new Numeric[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mx, mx},                                   new Numeric[][][]{{{mx, null, mx}, {mx, mx, null}, {}}, {{mn, mn}, {mn, null, mn}, {}}, {{}, {null}, {}}}          ),
      Tuple.of((byte)6,        mn,      mn,        new Numeric[]{mx, mx},                             new Numeric[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mx, mx},                                   new Numeric[][][]{{{mx, mx}, {mx, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{null}, {}, {}}}                      ),
      Tuple.of((byte)7,        mx,      mx,        new Numeric[]{mx, mx},                             new Numeric[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mx, null, mx},                             new Numeric[][][]{{{mx, mx}, {mx, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                          ),
      Tuple.of((byte)8,        mn,      mn,        new Numeric[]{mx, mx},                             new Numeric[][][]{{{mx, mx}, {mx, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mx, null, mx},                             new Numeric[][][]{{{mx, mx}, {mx, null, mx, null}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {null}, {}}}                ),
      Tuple.of((byte)9,        mx,      mx,        new Numeric[]{mn, mx},                             new Numeric[][][]{{{mn, mx}, {mn, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mn, null, mx},                             new Numeric[][][]{{{mn, mx, null}, {mn, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                    ),
      Tuple.of((byte)10,       mn,      mn,        new Numeric[]{mn, mx},                             new Numeric[][][]{{{mn, mx}, {mn, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{mn, null, mx},                             new Numeric[][][]{{{mn, mx, null}, {mn, null, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                    ),
      Tuple.of((byte)11,       v2,      v3,        new Numeric[]{},                                   new Numeric[][][]{{{}, {}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                                 new Numeric[]{},                                         new Numeric[][][]{{{}, {}, {}}, {{}, {}, {}}, {{}, {}, {}}}                                                        ),
      Tuple.of((byte)12,       v2,      v3,        new Numeric[]{},                                   new Numeric[][][]{{{}, {}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                                 new Numeric[]{},                                         new Numeric[][][]{{{}, {}, {}}, {{}, {}, {}}, {{}, {}, {}}}                                                        ),
      Tuple.of((byte)13,       v2,      v3,        new Numeric[]{},                                   new Numeric[][][]{{{}}},                                                                                                 new Numeric[]{},                                         new Numeric[][][]{{{}}}                                                                                            ),
      Tuple.of((byte)14,       v2,      v3,        new Numeric[]{},                                   new Numeric[][][]{{{}}},                                                                                                 new Numeric[]{},                                         new Numeric[][][]{{{}}}                                                                                            ),
      Tuple.of((byte)15,       v2,      v3,        new Numeric[]{},                                   new Numeric[][][]{{{}}},                                                                                                 new Numeric[]{null},                                     new Numeric[][][]{{{null}}}                                                                                        ),
      Tuple.of((byte)16,       v2,      v3,        new Numeric[]{},                                   new Numeric[][][]{{{}}},                                                                                                 new Numeric[]{null},                                     new Numeric[][][]{{{null}}}                                                                                        ),
      Tuple.of((byte)17,       v2,      v3,        new Numeric[]{nv},                                 new Numeric[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{nv},                                       new Numeric[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                                ),
      Tuple.of((byte)18,       v2,      v3,        new Numeric[]{nv},                                 new Numeric[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                                     new Numeric[]{nv},                                       new Numeric[][][]{{{nv, nv}, {nv, nv}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}                                ),
      Tuple.of((byte)19,       v2,      v3,        new Numeric[]{nv, mn, mx},                         new Numeric[][][]{{{v2, nv, mn}, {v3, mn, nv, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                         new Numeric[]{nv, mn, null, mx},                         new Numeric[][][]{{{v2, nv, mn, null}, {v3, mn, nv, mx}, {}}, {{mn, mn, null}, {mn, mn}, {}}, {{}, {}, {}}}        ),
      Tuple.of((byte)20,       v2,      v3,        new Numeric[]{nv, mn, mx},                         new Numeric[][][]{{{v2, nv, mn}, {v3, mn, nv, mx}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},                         new Numeric[]{nv, mn, null, mx},                         new Numeric[][][]{{{v2, nv, mn, null}, {v3, mn, nv, mx}, {}}, {{mn, mn, null}, {mn, mn}, {}}, {{}, {}, {}}}        ),
      Tuple.of((byte)21,       v2,      v3,        new Numeric[]{nv, mn, mx},                         new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{nv, mn, null, mx},                         new Numeric[][][]{{{nv, mn, mx, v1, v2, null, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)22,       v2,      v3,        new Numeric[]{nv, mn, mx},                         new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{nv, mn, null, mx},                         new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, null, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)23, nv,    null,        new Numeric[]{v3, v1, nv, mx, v4},                 new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{v3, v1, null, nv, mx, v3},                 new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5, null}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)24, nv,    null,        new Numeric[]{v3, v1, nv, mx, v4},                 new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{v3, v1, null, nv, mx, v3},                 new Numeric[][][]{{{nv, mn, mx, null, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)25,       v1,      v1,        new Numeric[]{v1, nv, nv},                         new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{v3, nv, null},                             new Numeric[][][]{{{nv, mn, null, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)26,       nv,      nv,        new Numeric[]{nv, nv, nv},                         new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{nv, null, v4},                             new Numeric[][][]{{{nv, null, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)27,       v6,      v5,        new Numeric[]{v4, nv, nv},                         new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{v3, nv, null},                             new Numeric[][][]{{{nv, mn, null, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)28,       v6,      v5,        new Numeric[]{v1, nv, mn, mx, v2, v3, v4, v5, v6}, new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{v1, nv, mn, mx, v2, v3, v4, v5, v6, null}, new Numeric[][][]{{{nv, mn, mx, v1, null, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  ),
      Tuple.of((byte)29,       v6,      v5,        new Numeric[]{v1, nv, mn, mx, v2, v3, v4, v5, v6}, new Numeric[][][]{{{nv, mn, mx, v1, v2, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}},             new Numeric[]{v1, nv, mn, mx, v2, v3, v4, v5, v6, null}, new Numeric[][][]{{{nv, mn, mx, v1, v2, null, v3, v4, v5}, {mn, mn}, {}}, {{mn, mn}, {mn, mn}, {}}, {{}, {}, {}}}  )
    );
  }
}
