package com.lbisoft.core.framework.utils;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public final class StringHelper {
  private StringHelper() {
  }

  public static String getSafe(String str) {
    return (str == null) ? "" : str;
  }

  public static String join(String separator, String... args) {
    List<String> items = Stream.of(args).filter(i -> !StringHelper.isEmpty(i)).toList();
    return items.isEmpty() ? null : StringUtils.join(items, separator);
  }

  public static boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }

  public static String stripEmailDomain(String email) {
    if (email != null && email.indexOf('@') >= 0) {
      return email.substring(0, email.indexOf('@'));
    }
    return email;
  }

  public static String normalize(String input) {
    if (input == null)
      return null;

    return StringUtils.stripAccents(input.trim()).toUpperCase().replaceAll("[,]\\s*", " ").replaceAll("[^A-Z0-9 ]", "");
  }
}
