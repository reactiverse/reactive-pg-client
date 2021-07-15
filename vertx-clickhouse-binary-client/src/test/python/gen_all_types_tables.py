#! /usr/bin/python3

ELEMENTARY_TYPES = ['Int8', 'UInt8', 'Int16', 'UInt16', 'Int32', 'UInt32', 'Int64', 'UInt64', 'Int128', 'String',
                    {'table': 'FixedString', 'type': 'FixedString(12)'},
                    'DateTime', {'table': 'datetime64', 'type': 'DateTime64(3)'}, 'Date', 'UUID',
                    {'table': 'Decimal32', 'type': 'Decimal32(4)'},
                    {'table': 'Decimal64', 'type': 'Decimal64(4)'},
                    {'table': 'Decimal128', 'type': 'Decimal128(4)'},
                    {'table': 'Decimal256', 'type': 'Decimal256(4)'},
                    {'table': 'Enum8', 'type': 'Enum8(\'v0\' = -128, \'v1\' = -2,\'v2\' = -1, \'v3\' = 0,\'v4\' = 1, \'v5\' = 2, \'v6\' = 127)'},
                    {'table': 'Enum16', 'type': 'Enum16(\'v0\' = -32768, \'v1\' = -2,\'v2\' = -1, \'v3\' = 0,\'v4\' = 1, \'v5\' = 2, \'v6\' = 32767)'},
                    'Float32', 'Float64', 'IPv6', 'IPv4'];

print('set allow_suspicious_low_cardinality_types=true;');
print('set allow_experimental_bigint_types=true;');

for elem_spec in ELEMENTARY_TYPES:
    table_name = elem_spec['table'] if isinstance(elem_spec, dict) else elem_spec;
    table_name = "vertx_test_{0}".format(table_name.lower());
    type_name = elem_spec['type'] if isinstance(elem_spec, dict) else elem_spec;
    print('DROP TABLE IF EXISTS {0};'.format(table_name));
    print('CREATE TABLE {0} ('.format(table_name));
    columns = ['id Int8'];
    low_cardinality = type_name != 'UUID' and not type_name.startswith('DateTime64') \
                      and not type_name.startswith('Decimal32(') and not type_name.startswith('Decimal64(') \
                      and not type_name.startswith('Decimal128(') \
                      and not type_name.startswith('Decimal256(') \
                      and not type_name.startswith('Enum');
    columns.append('simple_t {0}'.format(type_name));
    columns.append('nullable_t Nullable({0})'.format(type_name));
    columns.append('array_t Array({0})'.format(type_name));
    columns.append('array3_t Array(Array(Array({0})))'.format(type_name));
    columns.append('nullable_array_t Array(Nullable({0}))'.format(type_name));
    columns.append('nullable_array3_t Array(Array(Array(Nullable({0}))))'.format(type_name));

    if low_cardinality:
        columns.append('simple_lc_t LowCardinality({0})'.format(type_name));
        columns.append('nullable_lc_t LowCardinality(Nullable({0}))'.format(type_name));
        columns.append('array_lc_t Array(LowCardinality({0}))'.format(type_name));
        columns.append('array3_lc_t Array(Array(Array(LowCardinality({0}))))'.format(type_name));
        columns.append('nullable_array_lc_t Array(LowCardinality(Nullable({0})))'.format(type_name));
        columns.append('nullable_array3_lc_t Array(Array(Array(LowCardinality(Nullable({0})))))'.format(type_name));
    print('   ', ',\n    '.join(columns));
    print(') engine = Memory();');
