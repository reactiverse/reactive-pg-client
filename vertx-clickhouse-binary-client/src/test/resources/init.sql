-- USE testschema;
-- datatype testing table

-- immutable table for select query testing --
-- used by TCK
DROP TABLE IF EXISTS immutable;
CREATE TABLE immutable
(
  id      Int32,
  message varchar(2048)
) engine = MergeTree()
         ORDER BY (id);

INSERT INTO immutable (id, message) VALUES (1, 'fortune: No such file or directory');
INSERT INTO immutable (id, message) VALUES (2, 'A computer scientist is someone who fixes things that aren''t broken.');
INSERT INTO immutable (id, message) VALUES (3, 'After enough decimal places, nobody gives a damn.');
INSERT INTO immutable (id, message) VALUES (4, 'A bad random number generator: 1, 1, 1, 1, 1, 4.33e+67, 1, 1, 1');
INSERT INTO immutable (id, message) VALUES (5, 'A computer program does what you tell it to do, not what you want it to do.');
INSERT INTO immutable (id, message) VALUES (6, 'Emacs is a nice operating system, but I prefer UNIX. — Tom Christaensen');
INSERT INTO immutable (id, message) VALUES (7, 'Any program that runs right is obsolete.');
INSERT INTO immutable (id, message) VALUES (8, 'A list is only as strong as its weakest link. — Donald Knuth');
INSERT INTO immutable (id, message) VALUES (9, 'Feature: A bug with seniority.');
INSERT INTO immutable (id, message) VALUES (10, 'Computers make very fast, very accurate mistakes.');
INSERT INTO immutable (id, message) VALUES (11, '<script>alert("This should not be displayed in a browser alert box.");</script>');
INSERT INTO immutable (id, message) VALUES (12, 'フレームワークのベンチマーク');

-- mutable for insert,update,delete query testing --
-- used by TCK
DROP TABLE IF EXISTS mutable;
CREATE TABLE mutable
(
  id  Int32,
  val varchar(2048)
) engine = MergeTree()
           ORDER BY (id);

-- basic data type table --
-- used by TCK
DROP TABLE IF EXISTS basicdatatype;
CREATE TABLE basicdatatype
(
    id           Int16,
    test_int_2   Nullable(Int16),
    test_int_4   Nullable(Int32),
    test_int_8   Nullable(Int64),
    test_float_4 Nullable(Float32),
    test_float_8 Nullable(Float64),
    test_numeric Nullable(DECIMAL64(2)),
    test_decimal Nullable(DECIMAL64(0)),
    test_boolean Nullable(BOOLEAN),
    test_char    Nullable(FixedString(8)),
    test_varchar Nullable(String(20)),
    test_date    Nullable(DATE)
) engine = MergeTree()
        ORDER BY (id);
INSERT INTO basicdatatype(id, test_int_2, test_int_4, test_int_8,
                          test_float_4, test_float_8, test_numeric, test_decimal,
                          test_boolean, test_char, test_varchar,
                          test_date)
VALUES (1, 32767, 2147483647, 9223372036854775807,
        3.40282E38, 1.7976931348623157E308, 999.99, 12345,
        1, 'testchar', 'testvarchar',
        '2019-01-01');
INSERT INTO basicdatatype(id, test_int_2, test_int_4, test_int_8,
                          test_float_4, test_float_8, test_numeric, test_decimal,
                          test_boolean, test_char, test_varchar,
                          test_date)
VALUES (2, 32767, 2147483647, 9223372036854775807,
        3.40282E38, 1.7976931348623157E308, 999.99, 12345,
        1, 'testchar', 'testvarchar',
        '2019-01-01');
INSERT INTO basicdatatype(id, test_int_2, test_int_4, test_int_8,
                          test_float_4, test_float_8, test_numeric, test_decimal,
                          test_boolean, test_char, test_varchar, test_date)
VALUES (3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Collector API testing --
-- used by TCK
DROP TABLE IF EXISTS collector_test;
CREATE TABLE collector_test
(
    id           Int16,
    test_int_2   Int16,
    test_int_4   Int32,
    test_int_8   Int64,
    test_float   Float32,
    test_double  Float64,
    test_varchar VARCHAR(20)
) engine = MergeTree()
          ORDER BY (id);
INSERT INTO collector_test VALUES (1, 32767, 2147483647, 9223372036854775807, 123.456, 1.234567, 'HELLO,WORLD');
INSERT INTO collector_test VALUES (2, 32767, 2147483647, 9223372036854775807, 123.456, 1.234567, 'hello,world');

DROP TABLE IF EXISTS vertx_cl_test_table;
CREATE TABLE vertx_cl_test_table
(
    `name` String,
    `value` UInt32
)
ENGINE = GenerateRandom(1, 5, 3);

-- Fortune table
DROP TABLE IF EXISTS Fortune;
CREATE TABLE Fortune
(
    id      Int32,
    message String
) engine = MergeTree()
           ORDER BY (id);

INSERT INTO Fortune (id, message)
VALUES (1, 'fortune: No such file or directory');
INSERT INTO Fortune (id, message)
VALUES (2, 'A computer scientist is someone who fixes things that aren''t broken.');
INSERT INTO Fortune (id, message)
VALUES (3, 'After enough decimal places, nobody gives a damn.');
INSERT INTO Fortune (id, message)
VALUES (4, 'A bad random number generator: 1, 1, 1, 1, 1, 4.33e+67, 1, 1, 1');
INSERT INTO Fortune (id, message)
VALUES (5, 'A computer program does what you tell it to do, not what you want it to do.');
INSERT INTO Fortune (id, message)
VALUES (6, 'Emacs is a nice operating system, but I prefer UNIX. — Tom Christaensen');
INSERT INTO Fortune (id, message)
VALUES (7, 'Any program that runs right is obsolete.');
INSERT INTO Fortune (id, message)
VALUES (8, 'A list is only as strong as its weakest link. — Donald Knuth');
INSERT INTO Fortune (id, message)
VALUES (9, 'Feature: A bug with seniority.');
INSERT INTO Fortune (id, message)
VALUES (10, 'Computers make very fast, very accurate mistakes.');
INSERT INTO Fortune (id, message)
VALUES (11, '<script>alert("This should not be displayed in a browser alert box.");</script>');
INSERT INTO Fortune (id, message)
VALUES (12, 'フレームワークのベンチマーク');




--almost all possible supported types tables(maybe except experimental ones)
set allow_suspicious_low_cardinality_types=true;
set allow_experimental_bigint_types=true;
DROP TABLE IF EXISTS vertx_test_int8;
CREATE TABLE vertx_test_int8 (
    id Int8,
    simple_t Int8,
    nullable_t Nullable(Int8),
    array_t Array(Int8),
    array3_t Array(Array(Array(Int8))),
    nullable_array_t Array(Nullable(Int8)),
    nullable_array3_t Array(Array(Array(Nullable(Int8)))),
    simple_lc_t LowCardinality(Int8),
    nullable_lc_t LowCardinality(Nullable(Int8)),
    array_lc_t Array(LowCardinality(Int8)),
    array3_lc_t Array(Array(Array(LowCardinality(Int8)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Int8))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Int8)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_uint8;
CREATE TABLE vertx_test_uint8 (
    id Int8,
    simple_t UInt8,
    nullable_t Nullable(UInt8),
    array_t Array(UInt8),
    array3_t Array(Array(Array(UInt8))),
    nullable_array_t Array(Nullable(UInt8)),
    nullable_array3_t Array(Array(Array(Nullable(UInt8)))),
    simple_lc_t LowCardinality(UInt8),
    nullable_lc_t LowCardinality(Nullable(UInt8)),
    array_lc_t Array(LowCardinality(UInt8)),
    array3_lc_t Array(Array(Array(LowCardinality(UInt8)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(UInt8))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(UInt8)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_int16;
CREATE TABLE vertx_test_int16 (
    id Int8,
    simple_t Int16,
    nullable_t Nullable(Int16),
    array_t Array(Int16),
    array3_t Array(Array(Array(Int16))),
    nullable_array_t Array(Nullable(Int16)),
    nullable_array3_t Array(Array(Array(Nullable(Int16)))),
    simple_lc_t LowCardinality(Int16),
    nullable_lc_t LowCardinality(Nullable(Int16)),
    array_lc_t Array(LowCardinality(Int16)),
    array3_lc_t Array(Array(Array(LowCardinality(Int16)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Int16))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Int16)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_uint16;
CREATE TABLE vertx_test_uint16 (
    id Int8,
    simple_t UInt16,
    nullable_t Nullable(UInt16),
    array_t Array(UInt16),
    array3_t Array(Array(Array(UInt16))),
    nullable_array_t Array(Nullable(UInt16)),
    nullable_array3_t Array(Array(Array(Nullable(UInt16)))),
    simple_lc_t LowCardinality(UInt16),
    nullable_lc_t LowCardinality(Nullable(UInt16)),
    array_lc_t Array(LowCardinality(UInt16)),
    array3_lc_t Array(Array(Array(LowCardinality(UInt16)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(UInt16))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(UInt16)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_int32;
CREATE TABLE vertx_test_int32 (
    id Int8,
    simple_t Int32,
    nullable_t Nullable(Int32),
    array_t Array(Int32),
    array3_t Array(Array(Array(Int32))),
    nullable_array_t Array(Nullable(Int32)),
    nullable_array3_t Array(Array(Array(Nullable(Int32)))),
    simple_lc_t LowCardinality(Int32),
    nullable_lc_t LowCardinality(Nullable(Int32)),
    array_lc_t Array(LowCardinality(Int32)),
    array3_lc_t Array(Array(Array(LowCardinality(Int32)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Int32))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Int32)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_uint32;
CREATE TABLE vertx_test_uint32 (
    id Int8,
    simple_t UInt32,
    nullable_t Nullable(UInt32),
    array_t Array(UInt32),
    array3_t Array(Array(Array(UInt32))),
    nullable_array_t Array(Nullable(UInt32)),
    nullable_array3_t Array(Array(Array(Nullable(UInt32)))),
    simple_lc_t LowCardinality(UInt32),
    nullable_lc_t LowCardinality(Nullable(UInt32)),
    array_lc_t Array(LowCardinality(UInt32)),
    array3_lc_t Array(Array(Array(LowCardinality(UInt32)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(UInt32))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(UInt32)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_int64;
CREATE TABLE vertx_test_int64 (
    id Int8,
    simple_t Int64,
    nullable_t Nullable(Int64),
    array_t Array(Int64),
    array3_t Array(Array(Array(Int64))),
    nullable_array_t Array(Nullable(Int64)),
    nullable_array3_t Array(Array(Array(Nullable(Int64)))),
    simple_lc_t LowCardinality(Int64),
    nullable_lc_t LowCardinality(Nullable(Int64)),
    array_lc_t Array(LowCardinality(Int64)),
    array3_lc_t Array(Array(Array(LowCardinality(Int64)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Int64))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Int64)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_uint64;
CREATE TABLE vertx_test_uint64 (
    id Int8,
    simple_t UInt64,
    nullable_t Nullable(UInt64),
    array_t Array(UInt64),
    array3_t Array(Array(Array(UInt64))),
    nullable_array_t Array(Nullable(UInt64)),
    nullable_array3_t Array(Array(Array(Nullable(UInt64)))),
    simple_lc_t LowCardinality(UInt64),
    nullable_lc_t LowCardinality(Nullable(UInt64)),
    array_lc_t Array(LowCardinality(UInt64)),
    array3_lc_t Array(Array(Array(LowCardinality(UInt64)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(UInt64))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(UInt64)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_int128;
CREATE TABLE vertx_test_int128 (
    id Int8,
    simple_t Int128,
    nullable_t Nullable(Int128),
    array_t Array(Int128),
    array3_t Array(Array(Array(Int128))),
    nullable_array_t Array(Nullable(Int128)),
    nullable_array3_t Array(Array(Array(Nullable(Int128)))),
    simple_lc_t LowCardinality(Int128),
    nullable_lc_t LowCardinality(Nullable(Int128)),
    array_lc_t Array(LowCardinality(Int128)),
    array3_lc_t Array(Array(Array(LowCardinality(Int128)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Int128))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Int128)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_string;
CREATE TABLE vertx_test_string (
    id Int8,
    simple_t String,
    nullable_t Nullable(String),
    array_t Array(String),
    array3_t Array(Array(Array(String))),
    nullable_array_t Array(Nullable(String)),
    nullable_array3_t Array(Array(Array(Nullable(String)))),
    simple_lc_t LowCardinality(String),
    nullable_lc_t LowCardinality(Nullable(String)),
    array_lc_t Array(LowCardinality(String)),
    array3_lc_t Array(Array(Array(LowCardinality(String)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(String))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(String)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_fixedstring;
CREATE TABLE vertx_test_fixedstring (
    id Int8,
    simple_t FixedString(12),
    nullable_t Nullable(FixedString(12)),
    array_t Array(FixedString(12)),
    array3_t Array(Array(Array(FixedString(12)))),
    nullable_array_t Array(Nullable(FixedString(12))),
    nullable_array3_t Array(Array(Array(Nullable(FixedString(12))))),
    simple_lc_t LowCardinality(FixedString(12)),
    nullable_lc_t LowCardinality(Nullable(FixedString(12))),
    array_lc_t Array(LowCardinality(FixedString(12))),
    array3_lc_t Array(Array(Array(LowCardinality(FixedString(12))))),
    nullable_array_lc_t Array(LowCardinality(Nullable(FixedString(12)))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(FixedString(12))))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_datetime;
CREATE TABLE vertx_test_datetime (
    id Int8,
    simple_t DateTime,
    nullable_t Nullable(DateTime),
    array_t Array(DateTime),
    array3_t Array(Array(Array(DateTime))),
    nullable_array_t Array(Nullable(DateTime)),
    nullable_array3_t Array(Array(Array(Nullable(DateTime)))),
    simple_lc_t LowCardinality(DateTime),
    nullable_lc_t LowCardinality(Nullable(DateTime)),
    array_lc_t Array(LowCardinality(DateTime)),
    array3_lc_t Array(Array(Array(LowCardinality(DateTime)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(DateTime))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(DateTime)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_datetime64;
CREATE TABLE vertx_test_datetime64 (
    id Int8,
    simple_t DateTime64(3),
    nullable_t Nullable(DateTime64(3)),
    array_t Array(DateTime64(3)),
    array3_t Array(Array(Array(DateTime64(3)))),
    nullable_array_t Array(Nullable(DateTime64(3))),
    nullable_array3_t Array(Array(Array(Nullable(DateTime64(3)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_date;
CREATE TABLE vertx_test_date (
    id Int8,
    simple_t Date,
    nullable_t Nullable(Date),
    array_t Array(Date),
    array3_t Array(Array(Array(Date))),
    nullable_array_t Array(Nullable(Date)),
    nullable_array3_t Array(Array(Array(Nullable(Date)))),
    simple_lc_t LowCardinality(Date),
    nullable_lc_t LowCardinality(Nullable(Date)),
    array_lc_t Array(LowCardinality(Date)),
    array3_lc_t Array(Array(Array(LowCardinality(Date)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Date))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Date)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_uuid;
CREATE TABLE vertx_test_uuid (
    id Int8,
    simple_t UUID,
    nullable_t Nullable(UUID),
    array_t Array(UUID),
    array3_t Array(Array(Array(UUID))),
    nullable_array_t Array(Nullable(UUID)),
    nullable_array3_t Array(Array(Array(Nullable(UUID))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_decimal32;
CREATE TABLE vertx_test_decimal32 (
    id Int8,
    simple_t Decimal32(4),
    nullable_t Nullable(Decimal32(4)),
    array_t Array(Decimal32(4)),
    array3_t Array(Array(Array(Decimal32(4)))),
    nullable_array_t Array(Nullable(Decimal32(4))),
    nullable_array3_t Array(Array(Array(Nullable(Decimal32(4)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_decimal64;
CREATE TABLE vertx_test_decimal64 (
    id Int8,
    simple_t Decimal64(4),
    nullable_t Nullable(Decimal64(4)),
    array_t Array(Decimal64(4)),
    array3_t Array(Array(Array(Decimal64(4)))),
    nullable_array_t Array(Nullable(Decimal64(4))),
    nullable_array3_t Array(Array(Array(Nullable(Decimal64(4)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_decimal128;
CREATE TABLE vertx_test_decimal128 (
    id Int8,
    simple_t Decimal128(4),
    nullable_t Nullable(Decimal128(4)),
    array_t Array(Decimal128(4)),
    array3_t Array(Array(Array(Decimal128(4)))),
    nullable_array_t Array(Nullable(Decimal128(4))),
    nullable_array3_t Array(Array(Array(Nullable(Decimal128(4)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_decimal256;
CREATE TABLE vertx_test_decimal256 (
    id Int8,
    simple_t Decimal256(4),
    nullable_t Nullable(Decimal256(4)),
    array_t Array(Decimal256(4)),
    array3_t Array(Array(Array(Decimal256(4)))),
    nullable_array_t Array(Nullable(Decimal256(4))),
    nullable_array3_t Array(Array(Array(Nullable(Decimal256(4)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_enum8;
CREATE TABLE vertx_test_enum8 (
    id Int8,
    simple_t Enum8('v0' = -128, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 127),
    nullable_t Nullable(Enum8('v0' = -128, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 127)),
    array_t Array(Enum8('v0' = -128, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 127)),
    array3_t Array(Array(Array(Enum8('v0' = -128, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 127)))),
    nullable_array_t Array(Nullable(Enum8('v0' = -128, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 127))),
    nullable_array3_t Array(Array(Array(Nullable(Enum8('v0' = -128, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 127)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_enum16;
CREATE TABLE vertx_test_enum16 (
    id Int8,
    simple_t Enum16('v0' = -32768, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 32767),
    nullable_t Nullable(Enum16('v0' = -32768, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 32767)),
    array_t Array(Enum16('v0' = -32768, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 32767)),
    array3_t Array(Array(Array(Enum16('v0' = -32768, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 32767)))),
    nullable_array_t Array(Nullable(Enum16('v0' = -32768, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 32767))),
    nullable_array3_t Array(Array(Array(Nullable(Enum16('v0' = -32768, 'v1' = -2,'v2' = -1, 'v3' = 0,'v4' = 1, 'v5' = 2, 'v6' = 32767)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_float32;
CREATE TABLE vertx_test_float32 (
    id Int8,
    simple_t Float32,
    nullable_t Nullable(Float32),
    array_t Array(Float32),
    array3_t Array(Array(Array(Float32))),
    nullable_array_t Array(Nullable(Float32)),
    nullable_array3_t Array(Array(Array(Nullable(Float32)))),
    simple_lc_t LowCardinality(Float32),
    nullable_lc_t LowCardinality(Nullable(Float32)),
    array_lc_t Array(LowCardinality(Float32)),
    array3_lc_t Array(Array(Array(LowCardinality(Float32)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Float32))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Float32)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_float64;
CREATE TABLE vertx_test_float64 (
    id Int8,
    simple_t Float64,
    nullable_t Nullable(Float64),
    array_t Array(Float64),
    array3_t Array(Array(Array(Float64))),
    nullable_array_t Array(Nullable(Float64)),
    nullable_array3_t Array(Array(Array(Nullable(Float64)))),
    simple_lc_t LowCardinality(Float64),
    nullable_lc_t LowCardinality(Nullable(Float64)),
    array_lc_t Array(LowCardinality(Float64)),
    array3_lc_t Array(Array(Array(LowCardinality(Float64)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(Float64))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(Float64)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_ipv6;
CREATE TABLE vertx_test_ipv6 (
    id Int8,
    simple_t IPv6,
    nullable_t Nullable(IPv6),
    array_t Array(IPv6),
    array3_t Array(Array(Array(IPv6))),
    nullable_array_t Array(Nullable(IPv6)),
    nullable_array3_t Array(Array(Array(Nullable(IPv6)))),
    simple_lc_t LowCardinality(IPv6),
    nullable_lc_t LowCardinality(Nullable(IPv6)),
    array_lc_t Array(LowCardinality(IPv6)),
    array3_lc_t Array(Array(Array(LowCardinality(IPv6)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(IPv6))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(IPv6)))))
) engine = Memory();
DROP TABLE IF EXISTS vertx_test_ipv4;
CREATE TABLE vertx_test_ipv4 (
    id Int8,
    simple_t IPv4,
    nullable_t Nullable(IPv4),
    array_t Array(IPv4),
    array3_t Array(Array(Array(IPv4))),
    nullable_array_t Array(Nullable(IPv4)),
    nullable_array3_t Array(Array(Array(Nullable(IPv4)))),
    simple_lc_t LowCardinality(IPv4),
    nullable_lc_t LowCardinality(Nullable(IPv4)),
    array_lc_t Array(LowCardinality(IPv4)),
    array3_lc_t Array(Array(Array(LowCardinality(IPv4)))),
    nullable_array_lc_t Array(LowCardinality(Nullable(IPv4))),
    nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable(IPv4)))))
) engine = Memory();
