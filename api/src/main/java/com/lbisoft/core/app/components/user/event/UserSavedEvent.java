package com.lbisoft.core.app.components.user.event;

import org.springframework.context.ApplicationEvent;

public class UserSavedEvent extends ApplicationEvent {
  private static final long serialVersionUID = 6830961831016892230L;

  private final Integer userId;

  public UserSavedEvent(Object source, Integer userId) {
    super(source);
    this.userId = userId;
  }

  public Integer getUserId() {
    return userId;
  }
}
