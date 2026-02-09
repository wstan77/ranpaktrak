package com.lbisoft.core.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class MethodNotAllowedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MethodNotAllowedException() {
    super("Method is not allowed");
  }

  public MethodNotAllowedException(String msg) {
    super(msg);
  }
}
