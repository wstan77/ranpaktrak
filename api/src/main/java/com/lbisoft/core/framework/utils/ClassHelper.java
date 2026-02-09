package com.lbisoft.core.framework.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClassHelper.class);

  private ClassHelper() {}
  
  /**
   * Returns whether the given class has a getter for the given property. (Uses Java reflection)
   *
   * @param clazz Class to inspect
   * @param propertyName Name of the property to find
   * @return true if the class has a public getter for the given property
   */
  public static boolean classHasGetter(Class<?> clazz, String propertyName) {
    try {
      return clazz != null
          && propertyName != null
          && clazz.getMethod("get" + StringUtils.capitalize(propertyName)) != null;
    } catch (NoSuchMethodException e) {
      return false;
    } catch (SecurityException e) {
      LOGGER.error("Couldn't access class", e);
      return false;
    }
  }
}
