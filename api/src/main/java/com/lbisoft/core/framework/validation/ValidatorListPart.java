package com.lbisoft.core.framework.validation;

import java.util.List;
import java.util.function.Predicate;

public class ValidatorListPart<T> extends ValidatorPart<List<T>, ValidatorListPart<T>> {

  public ValidatorListPart(Validator validator, List<T> subject, String fieldName) {
    super(validator, subject, fieldName);
  }

  /** Validates that the field is present and has at least one element */
  public ValidatorListPart<T> isNotEmpty() {
    if (subject == null || subject.isEmpty()) {
      validator.addError(fieldName + " must be entered.");
    }

    return this;
  }

  /**
   * Validates that the field has at least N items populated
   *
   * @param n         Number of items required
   * @param itemLabel The label for the items in the error message (e.g. "items"
   *                  in "... must have
   *                  at least 5 items")
   */
  public ValidatorListPart<T> hasAtLeastNItems(int n, String itemLabel) {
    if (subject != null && subject.size() < n) {
      validator.addError("%s must have at least %d %s.", fieldName, n, itemLabel);
    }

    return this;
  }

  /**
   * Validates that the field has at least N items populated
   *
   * @param n Number of items required
   */
  public ValidatorListPart<T> hasAtLeastNItems(int n) {
    return hasAtLeastNItems(n, "items");
  }

  public ValidatorListPart<T> some(Predicate<T> predicate, String message) {
    if (subject != null) {
      if (!subject.stream().anyMatch(predicate)) {
        validator.addError("%s must %s", fieldName, message);
      }
    }
    return this;
  }
}
