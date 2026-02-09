package com.lbisoft.core.app.components.auth.model;

import com.lbisoft.core.framework.response.ResponseObject;

public class ChangePasswordForm extends ResponseObject {
  private String username;
  private String password;
  private String newPassword;

  private String token;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
