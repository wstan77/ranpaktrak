package com.lbisoft.core.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends RuntimeException {

  private static final long serialVersionUID = 1778719087846313764L;

  public TooManyRequestsException() {
    super("Too many requests. Please try again later.");
  }

  public TooManyRequestsException(String message) {
    super(message);
  }
}
