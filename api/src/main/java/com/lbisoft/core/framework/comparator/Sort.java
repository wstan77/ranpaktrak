package com.lbisoft.core.framework.comparator;

import java.util.Comparator;
import java.util.function.Function;

public final class Sort {
  public static <T, O extends Comparable<O>> Comparator<T> asc(Function<T, O> propertyGetter) {
    return (a, b) -> {
      if (a == null || b == null) {
        return 0;
      }
      O propertyA = propertyGetter.apply(a);
      O propertyB = propertyGetter.apply(b);
      if (propertyA == null || propertyB == null) {
        return 0;
      }
      return propertyA.compareTo(propertyB);
    };
  }

  public static <T, O extends Comparable<O>> Comparator<T> desc(Function<T, O> propertyGetter) {
    return asc(propertyGetter).reversed();
  }

  private Sort() {
  }
}
