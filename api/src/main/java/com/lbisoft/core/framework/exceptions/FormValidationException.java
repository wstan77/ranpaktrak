package com.lbisoft.core.framework.exceptions;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * FormValidationException  should be thrown when a business rule is broken or a form is invalid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FormValidationException extends RuntimeException {
  private static final String DEFAULT_MESSAGE = "Invalid Form:";
  private static final long serialVersionUID = 8474675925640872371L;
  private List<String> errors;

  /**
   * Default Constructor
   */
  public FormValidationException() {
    super(DEFAULT_MESSAGE);
  }

  public FormValidationException(String message) {
    super(message);
  }


  /**
   * Constructor to be used when passing a list of errors to the user.
   * @param errors - list of errors
   */
  public FormValidationException(List<String> errors) {
    super(DEFAULT_MESSAGE);
    this.errors = errors;
  }

  public FormValidationException(String message, List<String> errors) {
    super(message);
    this.errors = errors;
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  @Override
  public String toString() {
    return super.toString() + ": " + StringUtils.join(errors, ", ");
  }

}
