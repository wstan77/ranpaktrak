package com.lbisoft.core.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * InsufficientPermissionException  should be thrown when a user does not have permission to perform an action.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InsufficientPermissionException extends RuntimeException {

  private static final long serialVersionUID = 3322651200301987919L;
  
  public InsufficientPermissionException() {
    super("Permission denied.");
  }
  
  public InsufficientPermissionException(String message) {
    super(message);
  }
}
