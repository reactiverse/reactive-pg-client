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

import io.netty.buffer.ByteBuf;
import io.vertx.clickhouseclient.binary.impl.ClickhouseServerException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ClickhouseExceptionReader {
  private final List<ExceptionBlock> exceptionBlocks = new ArrayList<>();

  private final Charset charset;
  private Integer code;
  private String name;
  private String message;
  private String stacktrace;
  private Boolean hasNested;

  public ClickhouseExceptionReader(Charset charset) {
    this.charset = charset;
  }


  public ClickhouseServerException readFrom(ByteBuf in) {
    boolean hadNested;
    do {
      if (code == null) {
        if (in.readableBytes() >= 4) {
          code = in.readIntLE();
        } else {
          return null;
        }
      }
      if (name == null) {
        name = ByteBufUtils.readPascalString(in, charset);
        if (name == null) {
          return null;
        }
      }
      if (message == null) {
        message = ByteBufUtils.readPascalString(in, charset);
        if (message == null) {
          return null;
        }
      }
      if (stacktrace == null) {
        stacktrace = ByteBufUtils.readPascalString(in, charset);
        if (stacktrace == null) {
          return null;
        }
      }
      if (hasNested == null) {
        if (in.readableBytes() >= 1) {
          hasNested = in.readBoolean();
        } else {
          return null;
        }
      }
      hadNested = hasNested;
      ExceptionBlock tmp = new ExceptionBlock(code, name, message, stacktrace, hasNested);
      code = null;
      name = null;
      message = null;
      stacktrace = null;
      hasNested = null;
      exceptionBlocks.add(tmp);
    } while (hadNested);

    boolean isFirst = exceptionBlocks.size() == 1;
    ClickhouseServerException prevException = exceptionBlocks.get(exceptionBlocks.size() - 1).toException(null, isFirst);
    if (!isFirst) {
      for (int idx = exceptionBlocks.size() - 2; idx >= 0; --idx) {
        isFirst = idx == 0;
        prevException = exceptionBlocks.get(idx).toException(prevException, isFirst);
      }
    }
    return prevException;
  }

  private static class ExceptionBlock {
    private final Integer code;
    private final String name;
    private final String message;
    private final String stacktrace;

    private ExceptionBlock(Integer code, String name, String message, String stacktrace, Boolean hasNested) {
      this.code = code;
      this.name = name;
      this.message = message;
      this.stacktrace = stacktrace;
    }

    public ClickhouseServerException toException(ClickhouseServerException cause, boolean first) {
      return ClickhouseServerException.build(code, name, message, stacktrace, cause, first);
    }
  }
}
