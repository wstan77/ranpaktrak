package com.lbisoft.core.framework.validation;

// Need to take in generic type parameter V as a "this" type to better support chaining
public class ValidatorPart<T, V extends ValidatorPart<T, V>> {
  protected Validator validator;
  protected T subject;
  protected String fieldName;

  public ValidatorPart(Validator validator, T subject, String fieldName) {
    this.validator = validator;
    this.subject = subject;
    this.fieldName = fieldName;
  }

  // Use type V as a "this" type to better support chaining
  @SuppressWarnings("unchecked")
  public V isPresent() {
    if (subject == null) {
      validator.addError(fieldName + " must be entered.");
    }

    return (V) this;
  }

  /** Completes the checks on the current field, returning to the Validator */
  public Validator and() {
    return validator;
  }

  /**
   * Alias for and(). Completes the checks on the current field, returning to the
   * Validator
   */
  public Validator complete() {
    return and();
  }
}
