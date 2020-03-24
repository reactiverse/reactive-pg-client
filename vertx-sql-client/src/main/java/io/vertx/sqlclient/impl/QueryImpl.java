/*
 * Copyright (C) 2017 Julien Viet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.vertx.sqlclient.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Query;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.command.BiCommand;
import io.vertx.sqlclient.impl.command.PrepareStatementCommand;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class QueryImpl<T, R extends SqlResult<T>> implements Query<R> {

  static Query<RowSet<Row>> create(SqlClientBase<?> client, boolean autoCommit, boolean singleton, boolean prepared, String sql) {
    SqlResultBuilder<RowSet<Row>, RowSetImpl<Row>, RowSet<Row>> builder = new SqlResultBuilder<>(RowSetImpl.FACTORY, RowSetImpl.COLLECTOR);
    return new QueryImpl<>(client, autoCommit, singleton, prepared, sql, builder);
  }

  private final SqlClientBase<?> scheduler;
  private final boolean autoCommit;
  private final boolean singleton;
  private final boolean prepared;
  private final String sql;
  private final SqlResultBuilder<T, ?, R> builder;

  public QueryImpl(SqlClientBase<?> client, boolean autoCommit, boolean singleton, boolean prepared, String sql, SqlResultBuilder<T, ?, R> builder) {
    this.scheduler = client;
    this.autoCommit = autoCommit;
    this.singleton = singleton;
    this.prepared = prepared;
    this.sql = sql;
    this.builder = builder;
  }

  @Override
  public void execute(Handler<AsyncResult<R>> handler) {
    execute(ArrayTuple.EMPTY, handler);
  }

  @Override
  public Future<R> execute() {
    return execute(ArrayTuple.EMPTY);
  }

  private void execute(Tuple arguments, Promise<R> promise) {
    SqlResultHandler handler = builder.createHandler(promise);
    if (prepared) {
      BiCommand<PreparedStatement, Boolean> abc = new BiCommand<>(new PrepareStatementCommand(sql), ps -> {
        String msg = ps.prepare((TupleInternal) arguments);
        if (msg != null) {
          return Future.failedFuture(msg);
        }
        return Future.succeededFuture(builder.createCommand(ps, autoCommit, arguments, handler));
      });
      scheduler.schedule(abc, handler);
    } else {
      builder.execute(scheduler, sql, autoCommit, singleton, handler);
    }
  }

  @Override
  public void execute(Tuple tuple, Handler<AsyncResult<R>> handler) {
    execute(tuple, scheduler.promise(handler));
  }

  @Override
  public Future<R> execute(Tuple tuple) {
    Promise<R> promise = scheduler.promise();
    execute(tuple, promise);
    return promise.future();
  }

  @Override
  public <U> Query<SqlResult<U>> collecting(Collector<Row, ?, U> collector) {
    SqlResultBuilder<U, SqlResultImpl<U>, SqlResult<U>> builder = new SqlResultBuilder<>(SqlResultImpl::new, collector);
    return new QueryImpl<>(scheduler, autoCommit, singleton, prepared, sql, builder);
  }

  @Override
  public <U> Query<RowSet<U>> mapping(Function<Row, U> mapper) {
    SqlResultBuilder<RowSet<U>, RowSetImpl<U>, RowSet<U>> builder = new SqlResultBuilder<>(RowSetImpl.factory(), RowSetImpl.collector(mapper));
    return new QueryImpl<>(scheduler, autoCommit, singleton, prepared, sql, builder);
  }

  @Override
  public void batch(List<Tuple> batch, Handler<AsyncResult<R>> handler) {
    batch(batch, scheduler.promise(handler));
  }

  @Override
  public Future<R> batch(List<Tuple> batch) {
    Promise<R> promise = scheduler.promise();
    batch(batch, promise);
    return promise.future();
  }

  private void batch(List<Tuple> batch, Promise<R> promise) {
    SqlResultHandler handler = builder.createHandler(promise);
    if (prepared) {
      BiCommand<PreparedStatement, Boolean> abc = new BiCommand<>(new PrepareStatementCommand(sql), ps -> {
        for  (Tuple args : batch) {
          String msg = ps.prepare((TupleInternal) args);
          if (msg != null) {
            return Future.failedFuture(msg);
          }
        }
        return Future.succeededFuture(builder.createBatchCommand(ps, autoCommit, batch, handler));
      });
      scheduler.schedule(abc, handler);
    } else {
      builder.execute(scheduler, sql, autoCommit, singleton, handler);
    }
  }
}
