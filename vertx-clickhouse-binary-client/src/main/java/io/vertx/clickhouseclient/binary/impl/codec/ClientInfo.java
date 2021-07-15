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
import io.vertx.clickhouseclient.binary.ClickhouseConstants;
import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;

import java.util.Map;

public class ClientInfo {
  public static final int NO_QUERY = 0;
  public static final int INITIAL_QUERY = 1;

  private final ClickhouseBinaryDatabaseMetadata meta;

  public ClientInfo(ClickhouseBinaryDatabaseMetadata meta) {
    this.meta = meta;
  }

  public void serializeTo(ByteBuf buf) {
    int serverRevision = meta.getRevision();
    if (serverRevision < ClickhouseConstants.DBMS_MIN_REVISION_WITH_CLIENT_INFO) {
      throw new IllegalStateException(String.format("server revision %d < DBMS_MIN_REVISION_WITH_CLIENT_INFO(%d)",
        serverRevision, ClickhouseConstants.DBMS_MIN_REVISION_WITH_CLIENT_INFO));
    }
    buf.writeByte(INITIAL_QUERY);
    Map<String, String> properties = meta.getProperties();

    //initial_user
    ByteBufUtils.writePascalString(properties.getOrDefault(ClickhouseConstants.OPTION_INITIAL_USER, ""), buf);
    //initial_query_id
    ByteBufUtils.writePascalString(properties.getOrDefault(ClickhouseConstants.OPTION_INITIAL_QUERY_ID, ""), buf);
    //initial_address
    ByteBufUtils.writePascalString(properties.getOrDefault(ClickhouseConstants.OPTION_INITIAL_ADDRESS, "0.0.0.0:0"), buf);
    //interface: TCP
    buf.writeByte(1);
    ByteBufUtils.writePascalString(properties.getOrDefault(ClickhouseConstants.OPTION_INITIAL_USER, System.getProperty("user.name")), buf);
    ByteBufUtils.writePascalString(properties.getOrDefault(ClickhouseConstants.OPTION_INITIAL_HOSTNAME, "unknown-hostname"), buf);
    ByteBufUtils.writePascalString(meta.getFullClientName(), buf);
    ByteBufUtils.writeULeb128(ClickhouseConstants.CLIENT_VERSION_MAJOR, buf);
    ByteBufUtils.writeULeb128(ClickhouseConstants.CLIENT_VERSION_MINOR, buf);
    ByteBufUtils.writeULeb128(ClickhouseConstants.CLIENT_REVISION, buf);
    if (serverRevision >= ClickhouseConstants.DBMS_MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO) {
      //quota_key
      ByteBufUtils.writePascalString("", buf);
    }
    if (serverRevision >= ClickhouseConstants.DBMS_MIN_REVISION_WITH_VERSION_PATCH) {
      ByteBufUtils.writeULeb128(ClickhouseConstants.CLIENT_VERSION_PATCH, buf);
    }
  }
}
