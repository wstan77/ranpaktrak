package com.lbisoft.core.framework.validation;

// Need to take in generic type parameter V as a "this" type to better support chaining
public class ValidatorComparablePart<T extends Comparable<T>, V extends ValidatorComparablePart<T, V>>
    extends ValidatorPart<T, V> {

  public ValidatorComparablePart(Validator validator, T subject, String fieldName) {
    super(validator, subject, fieldName);
  }

  @Override
  @SuppressWarnings("unchecked")
  public V isPresent() {
    super.isPresent();
    return (V) this;
  }

  /** Validates that the field is less than or equal to given maximum */
  @SuppressWarnings("unchecked")
  public V isAtMost(T max) {
    if (subject != null && subject.compareTo(max) > 0) {
      validator.addError(fieldName + " cannot exceed " + max + ".");
    }
    return (V) this;
  }

  @SuppressWarnings("unchecked")
  public V isAtLeast(T min) {
    if (subject != null && subject.compareTo(min) < 0) {
      validator.addError(fieldName + " cannot be below " + min + ".");
    }
    return (V) this;
  }
}
