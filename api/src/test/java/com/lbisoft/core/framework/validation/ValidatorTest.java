package com.lbisoft.core.framework.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import com.lbisoft.core.framework.exceptions.FormValidationException;

import org.junit.jupiter.api.Test;

public class ValidatorTest {
  private final LocalDate min = LocalDate.of(2018, 6, 1);
  private final LocalDate max = LocalDate.of(2018, 6, 7);
  private static final String BETWEEN_DATE = "Between Date";
  private static final String NOT_BETWEEN_DATES = "Not Between Dates";
  private static final String FOO = "foos";

  @Test
  public void itShouldValidateFieldIsPresentFail() {
    Object exampleObject = null;
    assertFalse(new Validator().check("Empty Object", exampleObject).isPresent().and().isValid());
  }

  @Test
  public void itShouldValidateFieldIsPresentPass() {
    Object exampleObject = new Object();
    assertTrue(new Validator().check("Not Empty Object", exampleObject).isPresent().and().isValid());
  }

  @Test
  public void itShouldValidateStringIsNotEmptyFail() {
    assertFalse(new Validator().check("Empty String", "").isNotEmpty().and().isValid());
  }

  @Test
  public void itShouldValidateStringIsNotEmptyPass() {
    assertTrue(new Validator().check("Not Empty String", "notEmpty").isNotEmpty().and().isValid());
  }

  @Test
  public void itShouldValidateStringIsEmailFail() {
    assertFalse(new Validator().check("Bad Email", "not@email").isEmail().and().isValid());
  }

  @Test
  public void itShouldValidateStringIsEmailPass() {
    assertTrue(new Validator().check("Good Email", "is@email.com").isEmail().and().isValid());
  }

  @Test
  public void itShouldValidateListIsNotEmptyFail() {
    assertFalse(new Validator().check("Empty List", Collections.emptyList()).isNotEmpty().and().isValid());
  }

  @Test
  public void itShouldValidateListIsNotEmptyPass() {
    assertTrue(new Validator().check("Not Empty List", Arrays.asList("something")).isNotEmpty().and().isValid());
  }

  @Test
  public void itShouldValidateListHasAtLeast3ItemsFail() {
    assertFalse(new Validator().check("Too Short List", Arrays.asList(1, 2)).hasAtLeastNItems(3, FOO).and().isValid());
  }

  @Test
  public void itShouldValidateListHasAtLeast3ItemsPass() {
    assertTrue(
        new Validator().check("Right Length List", Arrays.asList(1, 2, 3)).hasAtLeastNItems(3, FOO).and().isValid());
  }

  @Test
  public void itShouldValidateNumberIsAtMost99Fail() {
    assertFalse(new Validator().check("Too High Number", 100).isAtMost(99).and().isValid());
  }

  @Test
  public void itShouldValidateNumberIsAtMost99Pass() {
    assertTrue(new Validator().check("Not Too High Number", 99).isAtMost(99).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenInclusivePass() {
    assertTrue(new Validator().check(BETWEEN_DATE, LocalDate.of(2018, 6, 5)).isBetween(min, max).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenInclusiveSameAsMinPass() {
    assertTrue(new Validator().check(BETWEEN_DATE, min).isBetween(min, max).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenInclusiveSameAsMaxPass() {
    assertTrue(new Validator().check(BETWEEN_DATE, max).isBetween(min, max).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenInclusiveFail() {
    assertFalse(new Validator().check(NOT_BETWEEN_DATES, LocalDate.of(2018, 6, 8)).isBetween(min, max).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenExclusivePass() {
    assertTrue(
        new Validator().check(BETWEEN_DATE, LocalDate.of(2018, 6, 5)).isBetweenExclusive(min, max).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenExclusiveSameAsMinFail() {
    assertFalse(new Validator().check(NOT_BETWEEN_DATES, min).isBetweenExclusive(min, max).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenExclusiveSameAsMaxFail() {
    assertFalse(new Validator().check(NOT_BETWEEN_DATES, max).isBetweenExclusive(min, max).and().isValid());
  }

  @Test
  public void itShouldValidateDateBetweenExclusiveFail() {
    assertFalse(new Validator().check(NOT_BETWEEN_DATES, LocalDate.of(2018, 6, 8)).isBetweenExclusive(min, max).and()
        .isValid());
  }

  @Test
  public void validateShouldThrowFormValidationException() {
    try {
      // setup
      Validator validator = new Validator();
      validator.check("Too Short List", Arrays.asList(1, 2)).hasAtLeastNItems(3, FOO).and()
          .check("Not Too High Number", 100).isAtMost(99);

      // test
      validator.validate();
    } catch (FormValidationException e) {
      // verify / assert
      assertEquals(2, e.getErrors().size(), "There should be two error messages returned to the user");
      return;
    }
    fail("validator should thrown an exception, thus never hitting the line");
  }

  @Test
  public void validateShouldNotThrowFormValidationException() {
    try {
      // setup
      Validator validator = new Validator();
      validator.check("Too Short List", Arrays.asList(1, 2)).hasAtLeastNItems(2, FOO).and()
          .check("Not Too High Number", 100).isAtMost(100);

      // test - this should not throw an exception
      validator.validate();
    } catch (Exception e) {
      fail("this should not throw an exception");
    }
  }
}
