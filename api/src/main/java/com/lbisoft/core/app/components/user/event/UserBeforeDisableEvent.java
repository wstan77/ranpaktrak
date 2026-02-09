package com.lbisoft.core.app.components.user.event;

import org.springframework.context.ApplicationEvent;

public class UserBeforeDisableEvent extends ApplicationEvent {
  private static final long serialVersionUID = -5975730486439112958L;
  private final Integer userId;

  public UserBeforeDisableEvent(Object source, Integer userId) {
    super(source);
    this.userId = userId;
  }

  public Integer getUserId() {
    return userId;
  }
}
