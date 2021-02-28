package io.vertx.clickhouse.clickhousenative;

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

  public static final String OPTION_CLIENT_NAME = "application_name";
  public static final String OPTION_INITIAL_USER = "initial_user";
  public static final String OPTION_INITIAL_QUERY_ID = "initial_query_id";
  public static final String OPTION_INITIAL_ADDRESS = "initial_address";
  public static final String OPTION_INITIAL_USERNAME = "initial_username";
  public static final String OPTION_INITIAL_HOSTNAME = "initial_hostname";

  public static final String OPTION_MAX_BLOCK_SIZE = "max_block_size";

  public static final Set<String> NON_QUERY_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
    OPTION_CLIENT_NAME, OPTION_INITIAL_USER, OPTION_INITIAL_QUERY_ID, OPTION_INITIAL_ADDRESS, OPTION_INITIAL_USERNAME, OPTION_INITIAL_HOSTNAME)));
}
