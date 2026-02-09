package com.lbisoft.core.app.components.quicksearch.util;

import com.lbisoft.core.app.components.quicksearch.model.ReferenceTag;
import com.lbisoft.core.framework.utils.NumberHelper;

public final class QuickSearchUtil {
  private QuickSearchUtil() {
  }

  public static ReferenceTag parseTag(String key) {
    if (key != null && key.charAt(0) == '[' && key.endsWith("]")) {
      key = key.substring(1, key.length() - 1);
      int splitPoint = key.lastIndexOf('_');
      if (splitPoint > 0) {
        String type = key.substring(0, splitPoint);
        String idStr = key.substring(splitPoint + 1);
        return NumberHelper.parseInteger(idStr).map(id -> new ReferenceTag(type, id)).orElse(null);
      }
    }
    return null;
  }
}
