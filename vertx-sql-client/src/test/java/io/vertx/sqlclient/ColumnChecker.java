package io.vertx.sqlclient;

import junit.framework.AssertionFailedError;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class ColumnChecker {

  private static List<SerializableBiFunction<Tuple, Integer, ?>> tupleMethods = new ArrayList<>();
  private static List<SerializableBiFunction<Row, String, ?>> rowMethods = new ArrayList<>();

  public static SerializableBiFunction<Tuple, Integer, Object> getByIndex(Class<?> type) {
    return (tuple, index) -> tuple.get(type, index);
  }

  public static SerializableBiFunction<Row, String, Object> getByName(Class<?> type) {
    return (row, index) -> {
      int idx = row.getColumnIndex(index);
      return idx == -1 ? null : row.get(type, idx);
    };
  }

  public static SerializableBiFunction<Tuple, Integer, Object> getValuesByIndex(Class<?> type) {
    return (tuple, index) -> tuple.get(Array.newInstance(type, 0).getClass(), index);
  }

  public static SerializableBiFunction<Row, String, Object> getValuesByName(Class<?> type) {
    return (row, index) -> {
      int idx = row.getColumnIndex(index);
      return idx == -1 ? null : row.get(Array.newInstance(type, 0).getClass(), idx);
    };
  }

  public static void load(Supplier<List<SerializableBiFunction<Tuple, Integer, ?>>> tupleMethodsFactory,
                                    Supplier<List<SerializableBiFunction<Row, String, ?>>> rowMethodsFactory) {
    tupleMethods = tupleMethodsFactory.get();
    rowMethods = rowMethodsFactory.get();
  }

  public static ColumnChecker checkColumn(int index, String name) {
    return new ColumnChecker(index, name);
  }

  private final List<Method> blackList = new ArrayList<>();
  private final List<Consumer<? super Row>> expects = new ArrayList<>();
  private final int index;
  private final String name;

  private ColumnChecker(int index, String name) {
    this.index = index;
    this.name = name;
  }

  public ColumnChecker returnsNull() {
    tupleMethods
      .forEach(m -> {
      blackList.add(m.method());
      expects.add(row -> {
        Object v = m.apply(row, index);
        assertNull(v);
      });
    });
    rowMethods
      .forEach(m -> {
        blackList.add(m.method());
        expects.add(row -> {
          Object v = m.apply(row, name);
          assertNull(v);
        });
      });
    return this;
  }

  public <R> ColumnChecker returns(Class<R> type, R expected) {
    return returns(getByIndex(type), getByName(type), expected);
  }

  public <R> ColumnChecker returns(Class<R> type, R[] expected) {
    return returns(getValuesByIndex(type), getValuesByName(type), expected);
  }

  public <R> ColumnChecker returns(SerializableBiFunction<Tuple, Integer, R> byIndexGetter,
                                   SerializableBiFunction<Row, String, R> byNameGetter,
                                   Consumer<R> check) {
    Method byIndexMeth = byIndexGetter.method();
    blackList.add(byIndexMeth);
    Method byNameMeth = byNameGetter.method();
    blackList.add(byNameMeth);
    String nameIndex = name + "/" + index;
    expects.add(row -> {
      Object actual = byIndexGetter.apply(row, index);
      try {
        check.accept((R) actual);
      } catch (AssertionError cause) {
        AssertionFailedError failure = new AssertionFailedError("Expected that " + byIndexMeth + " would not fail for " + nameIndex + ": " + cause.getMessage());
        failure.setStackTrace(failure.getStackTrace());
        throw failure;
      }
      actual = byNameGetter.apply(row, name);
      try {
        check.accept((R) actual);
      } catch (AssertionError cause) {
        AssertionFailedError failure = new AssertionFailedError("Expected that " + byNameMeth + " would not fail for " + nameIndex + ": " + cause.getMessage());
        failure.setStackTrace(failure.getStackTrace());
        throw failure;
      }
    });
    return this;
  }

  public <R> ColumnChecker returns(SerializableBiFunction<Tuple, Integer, R> byIndexGetter,
                            SerializableBiFunction<Row, String, R> byNameGetter,
                            R expected) {
    return this.<R>returns(byIndexGetter, byNameGetter, actual -> assertEquals(expected, actual));
  }

  public ColumnChecker returns(SerializableBiFunction<Tuple, Integer, Object> byIndexGetter,
                            SerializableBiFunction<Row, String, Object> byNameGetter,
                            Object[] expected) {
    Method byIndexMeth = byIndexGetter.method();
    blackList.add(byIndexMeth);
    Method byNameMeth = byNameGetter.method();
    blackList.add(byNameMeth);
    expects.add(row -> {
      Object[] actual = toObjectArray(byIndexGetter.apply(row, index));
      assertArrayEquals("Expected that " + byIndexMeth + " returns " + Arrays.toString(expected) + " instead of " + Arrays.toString(actual), expected, actual);
      actual = toObjectArray(byNameGetter.apply(row, name));
      assertArrayEquals("Expected that " + byNameMeth + " returns " + Arrays.toString(expected) + " instead of " + Arrays.toString(actual), expected, actual);
    });
    return this;
  }

  public ColumnChecker skip(SerializableBiFunction<Tuple, Integer, Object> byIndexGetter,
                            SerializableBiFunction<Row, String, Object> byNameGetter) {
    Method byIndexMeth = byIndexGetter.method();
    blackList.add(byIndexMeth);
    Method byNameMeth = byNameGetter.method();
    blackList.add(byNameMeth);
    return this;
  }

  public ColumnChecker returns(SerializableBiFunction<Tuple, Integer, Double> byIndexGetter,
                            SerializableBiFunction<Row, String, Double> byNameGetter,
                        double expected, double delta) {
    blackList.add(byIndexGetter.method());
    blackList.add(byNameGetter.method());
    expects.add(row -> {
      Object actual = byIndexGetter.apply(row, index);
      assertEquals("Expected that " + byIndexGetter.method() + " returns " + expected + " instead of " + actual, expected, (double)actual, delta);
      actual = byNameGetter.apply(row, name);
      assertEquals("Expected that " + byNameGetter.method() + " returns " + expected + " instead of " + actual, expected, (double)actual, delta);
    });
    return this;
  }

  public ColumnChecker returns(SerializableBiFunction<Tuple, Integer, Float> byIndexGetter,
                        SerializableBiFunction<Row, String, Float> byNameGetter,
                        float expected, float delta) {
    blackList.add(byIndexGetter.method());
    blackList.add(byNameGetter.method());
    expects.add(row -> {
      Object actual = byIndexGetter.apply(row, index);
      assertEquals("Expected that " + byIndexGetter.method() + " returns " + expected + " instead of " + actual, expected, (float)actual, delta);
      actual = byNameGetter.apply(row, name);
      assertEquals("Expected that " + byNameGetter.method() + " returns " + expected + " instead of " + actual, expected, (float)actual, delta);
    });
    return this;
  }

  public <R> ColumnChecker fails(SerializableBiFunction<Tuple, Integer, R> byIndexGetter,
                            SerializableBiFunction<Row, String, R> byNameGetter) {
    blackList.add(byIndexGetter.method());
    blackList.add(byNameGetter.method());
    expects.add(row -> {
      try {
        byIndexGetter.apply(row, index);
        fail("Expected that " + byIndexGetter.method() + " would throw an exception");
      } catch (Exception ignore) {
      }
      try {
        byNameGetter.apply(row, name);
        fail("Expected that " + byNameGetter.method() + " would throw an exception");
      } catch (Exception ignore) {
      }
    });
    return this;
  }

  public void forRow(Row row) {
    for (SerializableBiFunction<Tuple, Integer, ?> m : tupleMethods) {
      if (!blackList.contains(m.method())) {
        try {
          Object v = m.apply(row, index);
          fail("Was expecting " + m.method() + " to throw ClassCastException instead of returning " + v);
        } catch (ClassCastException ignore) {
        }
      }
    }
    for (SerializableBiFunction<Row, String, ?> m : rowMethods) {
      if (!blackList.contains(m.method())) {
        try {
          Object v = m.apply(row, name);
          fail("Was expecting " + m.method() + " to throw ClassCastException instead of returning " + v);
        } catch (ClassCastException ignore) {
        }
      }
    }
    for (Consumer<? super Row> e : expects) {
      e.accept(row);
    }
  }

  interface MethodReferenceReflection {

    //inspired by: http://benjiweber.co.uk/blog/2015/08/17/lambda-parameter-names-with-reflection/

    default SerializedLambda serialized() {
      try {
        Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
        replaceMethod.setAccessible(true);
        return (SerializedLambda) replaceMethod.invoke(this);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    default Class getContainingClass() {
      try {
        String className = serialized().getImplClass().replaceAll("/", ".");
        return Class.forName(className);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    default Method method() {
      SerializedLambda lambda = serialized();
      Class containingClass = getContainingClass();
      return Arrays.stream(containingClass.getDeclaredMethods())
        .filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
        .findFirst()
        .orElseThrow(MethodReferenceReflection.UnableToGuessMethodException::new);
    }

    class UnableToGuessMethodException extends RuntimeException {}
  }

  public interface SerializableBiFunction<O, T, R> extends BiFunction<O, T, R>, Serializable, MethodReferenceReflection {}

  public static Object[] toObjectArray(Object source) {
    if (source instanceof Object[]) {
      return (Object[]) source;
    }
    if (source == null) {
      return new Object[0];
    }
    if (!source.getClass().isArray()) {
      throw new IllegalArgumentException("Source is not an array: " + source);
    }
    int length = Array.getLength(source);
    if (length == 0) {
      return new Object[0];
    }
    Class wrapperType = Array.get(source, 0).getClass();
    Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
    for (int i = 0; i < length; i++) {
      newArray[i] = Array.get(source, i);
    }
    return newArray;
  }
}
