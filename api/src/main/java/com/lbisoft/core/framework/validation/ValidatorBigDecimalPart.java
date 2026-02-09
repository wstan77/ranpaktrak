package com.lbisoft.core.framework.validation;

import java.math.BigDecimal;

public class ValidatorBigDecimalPart
    extends ValidatorComparablePart<BigDecimal, ValidatorBigDecimalPart> {

  public ValidatorBigDecimalPart(Validator validator, BigDecimal subject, String fieldName) {
    super(validator, subject, fieldName);
  }

  /** Validates that the field is a multiple of the given divisor */
  public ValidatorBigDecimalPart isMultipleOf(BigDecimal multiple) {
    if (subject != null && subject.remainder(multiple).compareTo(BigDecimal.ZERO) == 0) {
      validator.addError(fieldName + " must be a multiple of " + multiple.toString() + ".");
    }
    return this;
  }
}
