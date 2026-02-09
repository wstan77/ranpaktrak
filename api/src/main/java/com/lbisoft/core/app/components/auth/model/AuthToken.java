package com.lbisoft.core.app.components.auth.model;

public class AuthToken {
  private String token;
  private String error;
  private boolean passwordResetFlg = false;
  private boolean newPasswordRequiredFlg = false;

  public AuthToken(String token) {
    this.token = token;
  }

  public AuthToken() {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean getPasswordResetFlg() {
    return passwordResetFlg;
  }

  public void setPasswordResetFlg(boolean passwordResetFlg) {
    this.passwordResetFlg = passwordResetFlg;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public boolean isNewPasswordRequiredFlg() {
    return newPasswordRequiredFlg;
  }

  public void setNewPasswordRequiredFlg(boolean newPasswordRequiredFlg) {
    this.newPasswordRequiredFlg = newPasswordRequiredFlg;
  }
}
