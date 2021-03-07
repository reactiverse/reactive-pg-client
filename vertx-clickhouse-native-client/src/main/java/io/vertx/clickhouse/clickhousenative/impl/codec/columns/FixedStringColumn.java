package io.vertx.clickhouse.clickhousenative.impl.codec.columns;

import io.vertx.clickhouse.clickhousenative.impl.codec.ClickhouseNativeColumnDescriptor;
import io.vertx.clickhouse.clickhousenative.impl.codec.ClickhouseStreamDataSource;

import java.util.ArrayList;
import java.util.List;

public class FixedStringColumn extends ClickhouseColumn {
  private List<Object> elements;

  protected FixedStringColumn(int nRows, ClickhouseNativeColumnDescriptor columnDescriptor) {
    super(nRows, columnDescriptor);
    this.elements = new ArrayList<>(nRows);
  }

  @Override
  protected Object[] readItems(ClickhouseStreamDataSource in) {
    while (elements.size() < nRows) {
      if (in.readableBytes() < columnDescriptor.getElementSize()) {
        return null;
      }
      byte[] stringBytes = new byte[columnDescriptor.getElementSize()];
      in.readBytes(stringBytes);
      elements.add(stringBytes);
    }
    Object[] ret = elements.toArray();
    elements = null;
    return ret;
  }

  @Override
  protected Object getElementInternal(int rowIdx) {
    return getObjectsArrayElement(rowIdx);
  }
}