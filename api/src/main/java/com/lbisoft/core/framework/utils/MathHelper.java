package com.lbisoft.core.framework.utils;

import java.util.Optional;

public final class MathHelper {


  /**
   * Safely subtract two integers. If both are null, null is returned. If one is null, it is assumed to be 0 and the difference is returned.
   * Otherwise their difference is returned.
   * @param int1
   * @param int2
   * @return
   */
  public static Integer subtract(Integer int1, Integer int2) {
    if (int1 == null && int2 == null) {
      return null;
    } else {
      return Optional.ofNullable(int1).orElse(0) - Optional.ofNullable(int2).orElse(0);
    }
  }

  /**
   * Safely add two or more integers.
   * @param integers
   * @return
   */
  public static int add(Integer...integers) {
    int out = 0;
    for (int i = 0; i < integers.length; i++) {
      if (integers[i] != null) {
        out += integers[i];
      }
    }
    return out;
  }
}
