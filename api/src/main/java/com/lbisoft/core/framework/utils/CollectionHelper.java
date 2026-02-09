package com.lbisoft.core.framework.utils;

import java.util.Collection;
import java.util.List;

public final class CollectionHelper {
  /**
   * Checks if a collection is empty.
   * @param collection
   * @return true if the collection is null or its length is 0, false otherwise.
   * @deprecated Use {@link org.apache.commons.collections4.CollectionUtils#isEmpty(Collection)}.
   */
  public static boolean isEmpty(Collection<?> collection){
    return collection == null || collection.isEmpty();
  }

  /**
   * @param collection
   * @return the first element of the list if the list is not null and not empty, otherwise null
   */
  public static <T> T first(Collection<T> collection) {
    return isEmpty(collection) ? null : collection.iterator().next();
  }

  /**
   * @param list
   * @return the last element of the list if the list is not null and not empty, otherwise null
   */
  public static <T> T last(List<T> list) {
    return isEmpty(list) ? null : list.get(list.size()-1);
  }
}
