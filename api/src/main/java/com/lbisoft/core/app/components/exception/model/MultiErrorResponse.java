package com.lbisoft.core.app.components.exception.model;

import java.util.List;
import java.util.Map;

/**
 * MultiErrorResponse is used to return a list of messages to the client. This is useful for things like validation
 * when you want to return multiple validation errors. 
 */
public class MultiErrorResponse extends ErrorResponse {

  private List<String> errors;
  
  /**
   * Constructor used with a list of errors
   * @param status - HTTP response status
   * @param errorAttributes - error attributes
   * @param errors - list of erros to return to client
   */
  public MultiErrorResponse(int status, String error, Map<String, Object> errorAttributes, List<String> errors) {
    super(status, error, errorAttributes);
    this.errors = errors;
  }
  
  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }
}
