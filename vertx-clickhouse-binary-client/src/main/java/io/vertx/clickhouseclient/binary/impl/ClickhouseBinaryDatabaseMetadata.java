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

import io.vertx.sqlclient.spi.DatabaseMetadata;

import java.nio.charset.Charset;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Map;

public class ClickhouseBinaryDatabaseMetadata implements DatabaseMetadata {
  private final String productName;
  private final String fullVersion;
  private final int major;
  private final int minor;
  private final int revision;
  private final int patchVersion;
  private final String displayName;
  private final ZoneId serverZoneId;
  private final ZoneId defaultZoneId;
  private final String fullClientName;
  private final Charset stringCharset;
  private final Map<String, String> properties;
  private final Duration yearDuration;
  private final Duration quarterDuration;
  private final Duration monthDuration;
  private final boolean saturateExtraNanos;
  private final boolean removeTrailingZerosInFixedStrings;

  public ClickhouseBinaryDatabaseMetadata(String productName, String fullVersion, int major, int minor, int revision,
                                          int patchVersion, String displayName, ZoneId serverZoneId, ZoneId defaultZoneId,
                                          String fullClientName, Map<String, String> properties, Charset stringCharset,
                                          Duration yearDuration, Duration quarterDuration, Duration monthDuration,
                                          boolean saturateExtraNanos, boolean removeTrailingZerosInFixedStrings) {
    this.productName = productName;
    this.fullVersion = fullVersion;
    this.major = major;
    this.minor = minor;
    this.revision = revision;
    this.patchVersion = patchVersion;
    this.displayName = displayName;
    this.serverZoneId = serverZoneId;
    this.defaultZoneId = defaultZoneId;
    this.fullClientName = fullClientName;
    this.properties = properties;
    this.stringCharset = stringCharset;
    this.yearDuration = yearDuration;
    this.quarterDuration = quarterDuration;
    this.monthDuration = monthDuration;
    this.saturateExtraNanos = saturateExtraNanos;
    this.removeTrailingZerosInFixedStrings = removeTrailingZerosInFixedStrings;
  }

  @Override
  public String productName() {
    return productName;
  }

  @Override
  public String fullVersion() {
    return fullVersion;
  }

  @Override
  public int majorVersion() {
    return major;
  }

  @Override
  public int minorVersion() {
    return minor;
  }

  public int getRevision() {
    return revision;
  }

  public int getPatchVersion() {
    return patchVersion;
  }

  public String getDisplayName() {
    return displayName;
  }

  public ZoneId getServerZoneId() {
    return serverZoneId;
  }

  public ZoneId getDefaultZoneId() {
    return defaultZoneId;
  }

  public String getFullClientName() {
    return fullClientName;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public Charset getStringCharset() {
    return stringCharset;
  }

  public Duration yearDuration() {
    return yearDuration;
  }

  public Duration quarterDuration() {
    return quarterDuration;
  }

  public Duration monthDuration() {
    return monthDuration;
  }

  public boolean isSaturateExtraNanos() {
    return saturateExtraNanos;
  }

  public boolean isRemoveTrailingZerosInFixedStrings() {
    return removeTrailingZerosInFixedStrings;
  }

  @Override
  public String toString() {
    return "ClickhouseNativeDatabaseMetadata{" +
      "productName='" + productName + '\'' +
      ", fullVersion='" + fullVersion + '\'' +
      ", major=" + major +
      ", minor=" + minor +
      ", revision=" + revision +
      ", patchVersion=" + patchVersion +
      ", displayName='" + displayName + '\'' +
      ", timezone='" + serverZoneId + '\'' +
      '}';
  }
}
