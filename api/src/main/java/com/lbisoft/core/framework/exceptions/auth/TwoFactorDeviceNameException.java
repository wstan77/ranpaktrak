package com.lbisoft.core.framework.exceptions.auth;

import org.springframework.security.core.AuthenticationException;

public class TwoFactorDeviceNameException extends AuthenticationException {

  private static final long serialVersionUID = 6470694298978357135L;

  public TwoFactorDeviceNameException(String msg) {
    super(msg);
  }

  public TwoFactorDeviceNameException(String msg, Throwable t) {
    super(msg, t);
  }
}
