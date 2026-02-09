package com.lbisoft.core.app.components.user.event;

import org.springframework.context.ApplicationEvent;

public class UserBeforeDeleteEvent extends ApplicationEvent {
  private static final long serialVersionUID = 4125038014512378887L;

  private final Integer userId;

  public UserBeforeDeleteEvent(Object source, Integer userId) {
    super(source);
    this.userId = userId;
  }

  public Integer getUserId() {
    return userId;
  }
}
