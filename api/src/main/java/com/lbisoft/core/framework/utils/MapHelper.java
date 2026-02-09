package com.lbisoft.core.framework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public final class MapHelper {
  private MapHelper() {
  }

  /**
   * Creates a {@link Sequence} by applying a given function to each {@link Entry}
   * in the given map.
   *
   * @param map      Map to create the list from
   * @param outputFn Function to convert key and value into the output for the
   *                 list
   * @return A list of transformed entries from the given map
   */
  public static <T, K, V> List<T> zip(Map<K, V> map, BiFunction<K, V, T> outputFn) {
    List<T> output = new ArrayList<T>(map.size());
    for (Entry<K, V> entry : map.entrySet()) {
      output.add(outputFn.apply(entry.getKey(), entry.getValue()));
    }
    return output;
  }
}
