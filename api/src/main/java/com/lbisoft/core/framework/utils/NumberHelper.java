package com.lbisoft.core.framework.utils;

import java.util.Optional;

public final class NumberHelper {
  private NumberHelper() {
  }

  public static Optional<Integer> parseInteger(String str) {
    try {
      return Optional.of(Integer.parseInt(str));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public static Optional<Float> parseFloat(String str) {
    try {
      return Optional.of(Float.parseFloat(str));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public static Optional<Double> parseDouble(String str) {
    try {
      return Optional.of(Double.parseDouble(str));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public static int compareNullSafeAsc(Integer a, Integer b) {
    Integer nullResult = forceNullBottom(a, b);
    return nullResult == null ? a - b : nullResult;
  }

  private static Integer forceNullBottom(Integer a, Integer b) {
    if (a == null && b == null) {
      return 0;
    }
    if (b == null) {
      return -1;
    }
    if (a == null) {
      return 1;
    }
    return null;
  }

  public static int compareNullSafeDesc(Integer a, Integer b) {
    Integer nullResult = forceNullBottom(a, b);
    return nullResult == null ? b - a : nullResult;
  }
  public static Integer coalesce(Integer one, Integer two, Integer...integers){
    if(one != null){
        return one;
    }
    if(two != null){
        return two;
    }
    for(Integer integer : integers){
        if(integer != null){
            return integer;
        }
    }
    return null;
}
}
