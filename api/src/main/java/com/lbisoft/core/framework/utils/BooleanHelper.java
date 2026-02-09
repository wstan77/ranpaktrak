package com.lbisoft.core.framework.utils;

public final class BooleanHelper {
  private BooleanHelper() {}
  
  /**
   * Null safe check if a Boolean is true
   *
   * @return true if the Boolean is not null and is true, false otherwise.
   */
  public static boolean isTrue(Boolean bool) {
    return bool != null && bool.booleanValue();
  }
  
  /**
   * Null safe check if a Boolean is false
   *
   * @return true if the Boolean is null or is false, false otherwise.
   */
  public static boolean isFalse(Boolean bool) {
    return !isTrue(bool);
  }

  public static int compareNullSafeDesc(Boolean a, Boolean b) {
    boolean aVal = isTrue(a);
    boolean bVal = isTrue(b);
    
    if(aVal == bVal){
        return 0;
    }
    if(aVal){
        return -1;
    }
    return 1;
  }
  
  
}
