package com.lbisoft.core.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException should be thrown when an entity can not be loaded
 * because it does not exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -7773131841668061262L;

  public ResourceNotFoundException() {
    super("Not found.");
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
