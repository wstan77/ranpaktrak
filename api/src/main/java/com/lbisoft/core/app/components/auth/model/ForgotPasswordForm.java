package com.lbisoft.core.app.components.auth.model;

import com.lbisoft.core.framework.response.ResponseObject;

public class ForgotPasswordForm extends ResponseObject {
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
