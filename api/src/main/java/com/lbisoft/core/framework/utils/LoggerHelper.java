package com.lbisoft.core.framework.utils;

import com.lbisoft.core.app.components.user.model.AppUser;

import org.slf4j.Logger;

public final class LoggerHelper {
  private LoggerHelper() {
  }

  public static void userIs(String msg, final Logger LOGGER, AppUser currentUser) {
    userIs(msg, LOGGER, currentUser, "");
  }

  private static String formatMessage(String msg, AppUser currentUser) {
    String userInfo = currentUser == null ? "Unknown user"
        : currentUser.getUsername() + parenthesis(currentUser.getUserId());
    return userInfo + " is " + msg;
  }

  public static void userIs(String msg, final Logger LOGGER, AppUser currentUser, String prefix) {
    LOGGER.info(prefix + formatMessage(msg, currentUser));
  }

  public static String parenthesis(Object msg) {
    return "(" + msg.toString() + ")";
  }
}
