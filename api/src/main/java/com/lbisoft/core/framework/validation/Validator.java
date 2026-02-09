package com.lbisoft.core.framework.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lbisoft.core.framework.exceptions.FormValidationException;

public class Validator {

    private List<String> messages;
    private boolean valid;

    /**
     * Default constructor
     */
    public Validator() {
        messages = new ArrayList<String>();
        valid = true;
    }

    public ValidatorStringPart check(String fieldName, String subject) {
        checkFieldName(fieldName);
        return new ValidatorStringPart(this, subject, fieldName);
    }

    public <T> ValidatorListPart<T> check(String fieldName, List<T> subject) {
        checkFieldName(fieldName);
        return new ValidatorListPart<>(this, subject, fieldName);
    }

    public ValidatorDatePart check(String fieldName, LocalDate subject) {
        checkFieldName(fieldName);
        return new ValidatorDatePart(this, subject, fieldName);
    }

    public ValidatorBigDecimalPart check(String fieldName, BigDecimal subject) {
        checkFieldName(fieldName);
        return new ValidatorBigDecimalPart(this, subject, fieldName);
    }

    public <T extends Comparable<T>> ValidatorComparablePart<T, ?> check(String fieldName,
            T subject) {
        checkFieldName(fieldName);
        return new ValidatorComparablePart<>(this, subject, fieldName);
    }

    public <T> ValidatorPart<T, ?> check(String fieldName, T subject) {
        checkFieldName(fieldName);
        return new ValidatorPart<>(this, subject, fieldName);
    }

    private void checkFieldName(String fieldName) throws AssertionError {
        if (fieldName == null) {
            throw new AssertionError("Field Name must be specified");
        }
    }

    /**
     * Add an error if the given expression is true.
     *
     * @param expression a boolean expression
     * @param message    the error message you would like to see if the expression
     *                   is true
     * @return
     */
    public Validator addErrorIf(boolean expression, String message) {
        if (expression) {
            addError(message);
        }
        return this;
    }

    /**
     * Add an error if the given expression is false.
     *
     * @param expression a boolean expression
     * @param message    the error message you would like to see if the expression
     *                   is false
     * @return
     */
    public Validator addErrorUnless(boolean expression, String message) {
        if (!expression) {
            addError(message);
        }
        return this;
    }

    /**
     * Validates that field value is a multiple of specified multiple
     *
     * @param multiple
     * @param field
     * @param fieldName
     * @return
     */

    /**
     * Adds an error to the errors collection and sets the validator valid state to
     * false. If
     * {@link #nextErrorMessage} is not null, uses that instead of the passed in
     * error string.
     */
    public void addError(String error) {
        messages.add(error);
        valid = false;
    }

    public void addError(String format, Object... args) {
        addError(String.format(format, args));
    }

    /**
     * Returns false if the validator instance has been used to validate at least
     * one invalid
     * argument. Returns false otherwise.
     *
     * @return
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets the current validator to invalid status
     *
     * @return the validator instance
     */
    public Validator setInvalid() {
        valid = false;
        return this;
    }

    /**
     * Should be called at the end of validation. if the validator is not valid then
     * a
     * RuntimeException will be thrown. This exception will contain all the invalid
     * messages for the
     * user to handle.
     */
    public void validate() {
        if (!valid) {
            throw new FormValidationException(messages);
        }
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
