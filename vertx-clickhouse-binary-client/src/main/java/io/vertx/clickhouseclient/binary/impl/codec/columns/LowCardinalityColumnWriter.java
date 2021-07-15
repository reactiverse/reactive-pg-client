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

package io.vertx.clickhouseclient.binary.impl.codec.columns;

import io.vertx.clickhouseclient.binary.impl.ClickhouseBinaryDatabaseMetadata;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseBinaryColumnDescriptor;
import io.vertx.clickhouseclient.binary.impl.codec.ClickhouseStreamDataSink;
import io.vertx.sqlclient.Tuple;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LowCardinalityColumnWriter extends ClickhouseColumnWriter {
  //Need to read additional keys.
  //Additional keys are stored before indexes as value N and N keys
  //after them.
  public static final int HAS_ADDITIONAL_KEYS_BIT = 1 << 9;
  //  # Need to update dictionary.
  //  # It means that previous granule has different dictionary.
  public static final int NEED_UPDATE_DICTIONARY = 1 << 10;
  public static final int SERIALIZATION_TYPE = HAS_ADDITIONAL_KEYS_BIT | NEED_UPDATE_DICTIONARY;

  private final ClickhouseBinaryDatabaseMetadata md;
  private Map<Object, Integer> dictionaryIndex;
  private List<Integer> keys;
  private int nullAddon;

  public LowCardinalityColumnWriter(List<Tuple> data, ClickhouseBinaryColumnDescriptor columnDescriptor, ClickhouseBinaryDatabaseMetadata md, int columnIndex) {
    super(data, columnDescriptor, columnIndex);
    this.md = md;
  }

  @Override
  protected void serializeStatePrefix(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    sink.writeLongLE(1);
  }

  @Override
  protected void serializeDataInternal(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
    dictionaryIndex = new LinkedHashMap<>();
    keys = new ArrayList<>();
    ClickhouseBinaryColumnDescriptor dictionaryWriterDescr = columnDescriptor.copyWithModifiers(false, false);
    ClickhouseColumn dictionaryColumn = ClickhouseColumns.columnForSpec(dictionaryWriterDescr, md);
    nullAddon = columnDescriptor.isNullable() ? 1 : 0;
    super.serializeDataInternal(sink, fromRow, toRow);
    int dictionarySize = dictionaryIndex.size() + nullAddon;
    //empty array
    if (dictionarySize == 0) {
      return;
    }
    int intType = (int) (log2(dictionarySize) / 8);
    ClickhouseColumn valuesColumn = LowCardinalityColumnReader.uintColumn(intType);

    int serializationType = SERIALIZATION_TYPE | intType;
    sink.writeLongLE(serializationType);
    sink.writeLongLE(dictionarySize);

    Collection<Object> nullVal = columnDescriptor.isNullable() ? Collections.singleton(dictionaryColumn.nullValue()) : Collections.emptyList();
    ArrayList<Tuple> dictionaryTuples = Stream.concat(nullVal.stream(), dictionaryIndex.keySet().stream())
      .map(LowCardinalityColumnWriter::maybeUnwrapArrayWrapper)
      .map(Tuple::of)
      .collect(Collectors.toCollection(ArrayList::new));

    ClickhouseColumnWriter dictionaryWriter = dictionaryColumn.writer(dictionaryTuples, 0);
    dictionaryWriter.serializeData(sink, 0, dictionaryTuples.size());
    sink.writeLongLE(data.size());
    ClickhouseColumnWriter valuesColumnWriter = valuesColumn.writer(keys.stream().map(Tuple::of).collect(Collectors.toCollection(ArrayList::new)), 0);
    valuesColumnWriter.serializeData(sink, 0, data.size());
  }

  private static Object maybeUnwrapArrayWrapper(Object from) {
    if (from.getClass() == ArrayWrapper.class) {
      return ((ArrayWrapper) from).array;
    }
    return from;
  }

  private Object maybeWrapArray(Object val) {
    if (val.getClass() == byte[].class) {
      val = new ArrayWrapper((byte[]) val);
    } else if (val.getClass() == String.class) {
      //TODO: maybe introduce cache with already observed Strings to skip getBytes() or mimic String.hashCode for byte[]
      val = new ArrayWrapper(((String) val).getBytes(md.getStringCharset()));
    }
    return val;
  }

  private double log2(int x) {
    return (Math.log(x) / Math.log(2));
  }

  @Override
  protected void serializeDataElement(ClickhouseStreamDataSink sink, Object val) {
    val = maybeWrapArray(val);
    Integer index = dictionaryIndex.computeIfAbsent(val, dictionaryMissVal -> dictionaryIndex.size() + nullAddon);
    keys.add(index);
  }

  @Override
  protected void serializeDataNull(ClickhouseStreamDataSink sink) {
    keys.add(0);
  }

  @Override
  protected void serializeNullsMap(ClickhouseStreamDataSink sink, int fromRow, int toRow) {
  }

  private static class ArrayWrapper {
    private final byte[] array;
    private final int hash;

    ArrayWrapper(byte[] array) {
      this.array = array;
      this.hash = Arrays.hashCode(array);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ArrayWrapper that = (ArrayWrapper) o;
      return hash == that.hash && Arrays.equals(array, that.array);
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }
}
