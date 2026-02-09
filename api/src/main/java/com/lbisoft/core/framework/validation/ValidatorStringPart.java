package com.lbisoft.core.framework.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorStringPart extends ValidatorComparablePart<String, ValidatorStringPart> {
  private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  public ValidatorStringPart(Validator validator, String subject, String fieldName) {
    super(validator, subject, fieldName);
  }

  /** Validates that the field is present and not empty */
  public ValidatorStringPart isNotEmpty() {
    if (subject == null || subject.isEmpty()) {
      validator.addError(fieldName + " must be entered.");
    }

    return this;
  }

  /** Validates that the field is a correctly formatted email */
  public ValidatorStringPart isEmail() {
    if (subject == null || subject.isEmpty()) {
      return this;
    }

    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(subject);

    if (!matcher.find()) {
      validator.addError(fieldName + " must be a valid email address.");
    }

    return this;
  }

  /**
   * Validate a field is at most a given maximum number of characters
   *
   * @param max Maximum length allowed
   */
  public ValidatorStringPart isUnderMaximumCharacters(int max) {
    int length = (subject == null) ? 0 : subject.trim().length();

    if (length > max) {
      validator.addError(fieldName + " cannot exceed " + max + " characters.");
    }

    return this;
  }
}
