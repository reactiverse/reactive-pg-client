package io.vertx.clickhouse.clickhousenative.impl.codec;

import io.vertx.clickhouse.clickhousenative.ClickhouseConstants;
import io.vertx.clickhouse.clickhousenative.impl.BlockInfo;
import io.vertx.clickhouse.clickhousenative.impl.ClickhouseNativeDatabaseMetadata;
import io.vertx.clickhouse.clickhousenative.impl.ColumnOrientedBlock;
import io.vertx.clickhouse.clickhousenative.impl.codec.columns.ClickhouseColumnReader;
import io.vertx.clickhouse.clickhousenative.impl.codec.columns.ClickhouseColumns;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ColumnOrientedBlockReader {
  private static final Logger LOG = LoggerFactory.getLogger(ColumnOrientedBlockReader.class);

  private final int serverRevision;
  private final ClickhouseNativeDatabaseMetadata md;

  private String tempTableInfo;
  private BlockInfo blockInfo;
  private Integer nColumns;
  private Integer nRows;
  private Map<String, ClickhouseNativeColumnDescriptor> colWithTypes;
  private List<ClickhouseColumnReader> data;

  private String colName;
  private String colType;
  private ClickhouseColumnReader columnData;
  private ClickhouseNativeColumnDescriptor columnDescriptor;

  public ColumnOrientedBlockReader(ClickhouseNativeDatabaseMetadata md) {
    assert(md != null);
    this.md = md;
    this.serverRevision = md.getRevision();
  }

  public ColumnOrientedBlock readFrom(ClickhouseStreamDataSource in) {


    //BlockInputStream.read
    if (blockInfo == null) {
      blockInfo = new BlockInfo();
    }
    if (serverRevision >= ClickhouseConstants.DBMS_MIN_REVISION_WITH_BLOCK_INFO) {
      if (blockInfo.isPartial()) {
        blockInfo.readFrom(in);
        if (blockInfo.isPartial()) {
          return null;
        }
      }
    }
    if (nColumns == null) {
      nColumns = in.readULeb128();
      if (nColumns == null) {
        return null;
      }
      colWithTypes = new LinkedHashMap<>();
    }
    if (nRows == null) {
      nRows = in.readULeb128();
      if (nRows == null) {
        return null;
      }
    }

    while (colWithTypes.size() < nColumns) {
      if (colName == null) {
        colName = in.readPascalString();
        if (colName == null) {
          return null;
        }
      }
      if (colType == null) {
        colType = in.readPascalString();
        if (colType == null) {
          return null;
        }
      }
      if (columnDescriptor == null) {
        columnDescriptor = ClickhouseColumns.columnDescriptorForSpec(colType, colName);
      }
      if (nRows > 0) {
        if (data == null) {
          data = new ArrayList<>(nColumns);
        }
        if (columnData == null) {
          columnData = ClickhouseColumns.columnForSpec(columnDescriptor, md).reader(nRows);
        }
        if (columnData.isPartial()) {
          LOG.info("reading column " + colName + "[" + nRows + "] of type " + colType);

          columnData.readColumn(in);
          if (columnData.isPartial()) {
            return null;
          } else {
            data.add(columnData);
            columnData = null;
          }
        }
      }
      colWithTypes.put(colName, columnDescriptor);
      columnDescriptor = null;
      colName = null;
      colType = null;
    }
    if (colWithTypes.size() == nColumns) {
      return new ColumnOrientedBlock(colWithTypes, data, blockInfo, md);
    }
    return null;
  }
}
