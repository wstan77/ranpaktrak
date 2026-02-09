package com.lbisoft.core.framework.exceptions.auth;

import org.springframework.security.core.AuthenticationException;

public class TwoFactorRegistrationException extends AuthenticationException {

  private static final long serialVersionUID = 1346197137660443972L;

  public TwoFactorRegistrationException(String msg) {
    super(msg);
  }

  public TwoFactorRegistrationException(String msg, Throwable t) {
    super(msg, t);
  }
}
