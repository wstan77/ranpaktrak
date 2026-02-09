package com.lbisoft.core.framework.response;

public class ResponseMessage {
  public static enum MessageLevel {
    INFO,
    WARNING
  }

  private String message;
  private MessageLevel level;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public MessageLevel getLevel() {
    return level;
  }

  public void setLevel(MessageLevel level) {
    this.level = level;
  }
}
