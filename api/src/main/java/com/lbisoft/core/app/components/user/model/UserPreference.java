package com.lbisoft.core.app.components.user.model;

import com.lbisoft.core.framework.response.ResponseObject;

public class UserPreference extends ResponseObject {
  private Integer userId;

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
