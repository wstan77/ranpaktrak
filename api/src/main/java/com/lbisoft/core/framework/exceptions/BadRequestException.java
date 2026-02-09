package com.lbisoft.core.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Generic exception thrown for malformed requests * */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  private static final long serialVersionUID = 5644151473605921126L;

  public BadRequestException() {
    super("Malformed request");
  }

  public BadRequestException(String message) {
    super(message);
  }
}
