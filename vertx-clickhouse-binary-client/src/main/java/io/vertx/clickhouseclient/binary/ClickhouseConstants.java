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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClickhouseConstants {
  public static final int DBMS_MIN_REVISION_WITH_TEMPORARY_TABLES = 50264;
  public static final int DBMS_MIN_REVISION_WITH_TOTAL_ROWS_IN_PROGRESS = 51554;
  public static final int DBMS_MIN_REVISION_WITH_BLOCK_INFO = 51903;

  public static final int DBMS_MIN_REVISION_WITH_CLIENT_INFO = 54032;
  public static final int DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE = 54058;
  public static final int DBMS_MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO = 54060;
  public static final int DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME = 54372;
  public static final int DBMS_MIN_REVISION_WITH_VERSION_PATCH = 54401;
  public static final int DBMS_MIN_REVISION_WITH_SERVER_LOGS = 54406;
  public static final int DBMS_MIN_REVISION_WITH_COLUMN_DEFAULTS_METADATA = 54410;
  public static final int DBMS_MIN_REVISION_WITH_CLIENT_WRITE_INFO = 54420;
  public static final int DBMS_MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS = 54429;
  public static final int DBMS_MIN_REVISION_WITH_INTERSERVER_SECRET = 54441;

  public static final int CLIENT_VERSION_MAJOR = 20;
  public static final int CLIENT_VERSION_MINOR = 10;
  public static final int CLIENT_VERSION_PATCH = 2;
  public static final int CLIENT_REVISION = 54441;

  public static final String OPTION_APPLICATION_NAME = "application_name";
  public static final String OPTION_INITIAL_USER = "initial_user";
  public static final String OPTION_INITIAL_QUERY_ID = "initial_query_id";
  public static final String OPTION_INITIAL_ADDRESS = "initial_address";
  public static final String OPTION_INITIAL_USERNAME = "initial_username";
  public static final String OPTION_INITIAL_HOSTNAME = "initial_hostname";
  public static final String OPTION_COMPRESSOR = "compressor";
  public static final String OPTION_STRING_CHARSET = "string_charset";
  public static final String OPTION_DEFAULT_ZONE_ID = "default_zone_id";
  public static final String OPTION_YEAR_DURATION = "days_in_year";
  public static final String OPTION_QUARTER_DURATION = "days_in_quarter";
  public static final String OPTION_MONTH_DURATION = "days_in_month";
  public static final String OPTION_SEND_LOGS_LEVEL = "send_logs_level";
  public static final String OPTION_DATETIME64_EXTRA_NANOS_MODE = "dt64_extra_nanos";
  public static final String OPTION_ENUM_RESOLUTION = "enum_resolution";
  public static final String OPTION_MAX_BLOCK_SIZE = "max_block_size";
  public static final String OPTION_REMOVE_TRAILING_ZEROS_WHEN_ENCODE_FIXED_STRINGS = "remove_trailing_zeros_when_encode_fixed_strings";

  public static final short COMPRESSION_METHOD_LZ4 = 0x82;
  public static final short COMPRESSION_METHOD_ZSTD = 0x90;

  public static final Set<String> NON_QUERY_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
    OPTION_APPLICATION_NAME, OPTION_INITIAL_USER, OPTION_INITIAL_QUERY_ID, OPTION_INITIAL_ADDRESS, OPTION_INITIAL_USERNAME,
    OPTION_INITIAL_HOSTNAME, OPTION_COMPRESSOR, OPTION_STRING_CHARSET, OPTION_DEFAULT_ZONE_ID, OPTION_YEAR_DURATION, OPTION_QUARTER_DURATION,
    OPTION_MONTH_DURATION, OPTION_DATETIME64_EXTRA_NANOS_MODE, OPTION_ENUM_RESOLUTION, OPTION_REMOVE_TRAILING_ZEROS_WHEN_ENCODE_FIXED_STRINGS)));
}
