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

package io.vertx.clickhouseclient.binary;

import io.vertx.clickhouseclient.binary.ClickhouseConstants;
import io.vertx.clickhouseclient.binary.ClickhouseBinaryConnectOptions;
import org.junit.rules.ExternalResource;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.utility.DockerImageName;

public class ClickhouseResource extends ExternalResource {
  private static final String connectionUri = System.getProperty("connection.uri");
  private static final String tlsConnectionUri = System.getProperty("tls.connection.uri");

  private ClickHouseContainer server;
  private ClickhouseBinaryConnectOptions options;

  @Override
  protected void before() throws Throwable {
    if (isTestingWithExternalDatabase()) {
      this.options = ClickhouseBinaryConnectOptions.fromUri(connectionUri);
      return;
    }
    if (this.server != null) {
      return;
    }
    DockerImageName imageName = DockerImageName.parse("yandex/clickhouse-server").withTag(clickhouseVersion());
    server = new ClickHouseContainer(imageName);
    server.start();
    this.options = (ClickhouseBinaryConnectOptions) new ClickhouseBinaryConnectOptions()
      .setPort(server.getMappedPort(ClickHouseContainer.NATIVE_PORT))
      .setHost(server.getContainerIpAddress())
      .setUser(server.getUsername())
      .setPassword(server.getPassword())
      .setDatabase("default")
      .addProperty(ClickhouseConstants.OPTION_APPLICATION_NAME, "java-driver")
      .addProperty(ClickhouseConstants.OPTION_COMPRESSOR, "lz4_safe")
      .addProperty(ClickhouseConstants.OPTION_STRING_CHARSET, "utf-8")
      .addProperty(ClickhouseConstants.OPTION_DEFAULT_ZONE_ID, "Europe/Oslo")
      .addProperty(ClickhouseConstants.OPTION_DATETIME64_EXTRA_NANOS_MODE, "saturate")
      .addProperty(ClickhouseConstants.OPTION_ENUM_RESOLUTION, "by_name")
      .addProperty(ClickhouseConstants.OPTION_REMOVE_TRAILING_ZEROS_WHEN_ENCODE_FIXED_STRINGS, "true")
      .addProperty(ClickhouseConstants.OPTION_SEND_LOGS_LEVEL, "debug");
  }

  private static String clickhouseVersion() {
    String version = System.getProperty("embedded.clickhouse.version");
    return version == null ? "20.10.2" : version;
  }

  @Override
  protected void after() {
    if (server != null) {
      server.stop();
    }
  }

  public ClickhouseBinaryConnectOptions options() {
    return new ClickhouseBinaryConnectOptions(options);
  }

  public static boolean isTestingWithExternalDatabase() {
    return isSystemPropertyValid(connectionUri);
    //|| isSystemPropertyValid(tlsConnectionUri);
  }

  private static boolean isSystemPropertyValid(String systemProperty) {
    return systemProperty != null && !systemProperty.isEmpty();
  }
}
