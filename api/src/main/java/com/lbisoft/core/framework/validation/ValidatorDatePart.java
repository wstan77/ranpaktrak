package com.lbisoft.core.framework.validation;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

public class ValidatorDatePart extends ValidatorComparablePart<ChronoLocalDate, ValidatorDatePart> {

  public ValidatorDatePart(Validator validator, ChronoLocalDate subject, String fieldName) {
    super(validator, subject, fieldName);
  }

  /** Validates that the field is not after the current date */
  public ValidatorDatePart isNotInTheFuture() {
    if (subject != null && subject.isAfter(LocalDate.now())) {
      validator.addError(fieldName + " must not be in the future.");
    }
    return this;
  }

  /** Validates that the field is before the current date */
  public ValidatorDatePart isInThePast() {
    if (subject != null && !subject.isBefore(LocalDate.now())) {
      validator.addError(fieldName + " must be in the past.");
    }
    return this;
  }

  /** Validates that the field is after the current date */
  public ValidatorDatePart isInTheFuture() {
    if (subject != null && subject.isBefore(LocalDate.now())) {
      validator.addError(fieldName + " must be in the future.");
    }
    return this;
  }

  /**
   * Validates that the field is between a given minimum and maximum date (inclusive)
   *
   * @param min Minimum date
   * @param max Maximum date
   * @return
   */
  public ValidatorDatePart isBetween(LocalDate min, LocalDate max) {
    if (subject != null && (subject.isBefore(min) || subject.isAfter(max))) {
      validator.addError(fieldName + " must be between " + min + " and " + max + ".");
    }
    return this;
  }

  /**
   * Validates that the field is between a given minimum and maximum date (exclusive)
   *
   * @param min Minimum date
   * @param max Maximum date
   * @return
   */
  public ValidatorDatePart isBetweenExclusive(LocalDate min, LocalDate max) {
    if (subject != null && !(min.isBefore(subject) && max.isAfter(subject))) {
      validator.addError(fieldName + " must be between " + min + " and " + max + ".");
    }
    return this;
  }
}
