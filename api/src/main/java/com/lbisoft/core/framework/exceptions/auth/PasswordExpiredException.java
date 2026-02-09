package com.lbisoft.core.framework.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PasswordExpiredException extends AuthenticationException {
  private static final long serialVersionUID = 2089944210275949033L;

  public PasswordExpiredException(String msg) {
    super(msg);
  }
}
