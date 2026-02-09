package com.lbisoft.core.app.components.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NewPasswordRequiredException extends RuntimeException{

  private static final long serialVersionUID = 8660024136426579145L;
  
  public NewPasswordRequiredException() {
    super();
  }
  public NewPasswordRequiredException(String msg) {
    super(msg);
  }  
}
