package com.lbisoft.core.framework.exceptions.auth;

import org.springframework.security.core.AuthenticationException;

public class TwoFactorPinException extends AuthenticationException {

  private static final long serialVersionUID = 6470694298978357135L;
  private boolean isCookieExpired;

  public TwoFactorPinException(String msg) {
    super(msg);
    this.isCookieExpired = false;
  }

  public TwoFactorPinException(String msg, Throwable t) {
    super(msg, t);
    this.isCookieExpired = false;
  }

  public TwoFactorPinException(String msg, boolean isCookieExpired) {
    super(msg);
    this.isCookieExpired = isCookieExpired;
  }

  public boolean isCookieExpired() {
    return isCookieExpired;
  }

  public void setCookieExpired(boolean isCookieExpired) {
    this.isCookieExpired = isCookieExpired;
  }
}
