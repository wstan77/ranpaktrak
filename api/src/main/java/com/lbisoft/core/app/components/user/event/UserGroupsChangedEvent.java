package com.lbisoft.core.app.components.user.event;

import org.springframework.context.ApplicationEvent;

public class UserGroupsChangedEvent extends ApplicationEvent {

  public UserGroupsChangedEvent(Object source) {
    super(source);
  }

}
