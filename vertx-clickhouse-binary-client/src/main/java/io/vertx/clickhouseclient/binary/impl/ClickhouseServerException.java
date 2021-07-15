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

package io.vertx.clickhouseclient.binary.impl;

public class ClickhouseServerException extends RuntimeException {
  private final int code;
  private final String name;
  private final String message;
  private final String stacktrace;

  private ClickhouseServerException(Integer code, String name, String message, String stacktrace, ClickhouseServerException cause, boolean unused) {
    super(message, cause, false, true);
    this.code = code;
    this.name = name;
    this.message = message;
    this.stacktrace = stacktrace;
  }

  private ClickhouseServerException(Integer code, String name, String message, String stacktrace, ClickhouseServerException cause) {
    super(message, cause, false, false);
    this.code = code;
    this.name = name;
    this.message = message;
    this.stacktrace = stacktrace;
  }

  public static ClickhouseServerException build(Integer code, String name, String message, String stacktrace, ClickhouseServerException cause, boolean first) {
    if (first) {
      return new ClickhouseServerException(code, name, message, stacktrace, cause, first);
    }
    return new ClickhouseServerException(code, name, message, stacktrace, cause);
  }

  public int getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public String getServerStacktrace() {
    return stacktrace;
  }
}
