package com.lbisoft.core.framework.utils;

import java.util.Objects;
import java.util.function.Supplier;

public final class ObjectHelper {
  private ObjectHelper() {
  }

  @SafeVarargs
  public static <T> T coalesce(Supplier<T>... items) {
    for (Supplier<T> supplier : items) {
      T value = supplier.get();
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  public static <T> T coalesce(Supplier<T> a, Supplier<T> b) {
    T item = a.get();
    return item == null ? b.get() : item;
  }

  public static <T> T coalesce(Supplier<T> a, Supplier<T> b, Supplier<T> c) {
    T item = a.get();
    if (item == null) {
      item = b.get();
    }
    return item == null ? c.get() : item;
  }

  public static <T> T coalesce(Supplier<T> a, Supplier<T> b, Supplier<T> c, Supplier<T> d) {
    T item = a.get();
    if (item == null) {
      item = b.get();
    }
    if (item == null) {
      item = c.get();
    }
    return item == null ? d.get() : item;
  }

  public static <T> T coalesce(Supplier<T> a, T b) {
    T item = a.get();
    return item == null ? b : item;
  }

  public static <T> T coalesce(T a, T b) {
    return a == null ? b : a;
  }

  /**
   * Checks whether <code>object</code> equals any of the <code>objects</object>.
   * If <code>object</code> is null, true is returned if at least one of the
   * <code>objects</object> is also null.
   *
   * @param object
   * @param objects
   * @return
   */
  @SafeVarargs
  public static <T> boolean equalsAny(T object, T... objects) {
    for (int i = 0; i < objects.length; i++) {
      boolean equals = Objects.equals(object, objects[i]);
      if (equals) {
        return true;
      }
    }
    return false;
  }
}
