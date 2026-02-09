package com.lbisoft.core.app.components.user.validator;

import java.util.function.Consumer;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.framework.exceptions.FormValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserValidatorTest {

  private AppUser user;
  private final UserValidator validator = new UserValidator();

  @BeforeEach
  public void before() {
    user = getValidUser();
  }

  public AppUser getValidUser() {
    user = new AppUser();
    user.setUsername("user");
    user.setFirstName("First");
    user.setLastName("Last");
    user.setEmailAddress("user@email.com");

    return user;
  }

  @Test
  public void validUser() {
    validator.forCreate(user).validate();
  }

  @Test
  public void validateUsername() {
    validateNotEmpty(user::setUsername, "user");
  }

  @Test
  public void validateFirstName() {
    validateNotEmpty(user::setFirstName, "first");
  }

  @Test
  public void validateLastName() {
    validateNotEmpty(user::setLastName, "last");
  }

  private void validateNotEmpty(Consumer<String> setter, String value) {
    // set null value
    setter.accept(null);
    try {
      // validate should fail
      validator.forCreate(user).validate();
    } catch (FormValidationException e) {
      // set string and value should now pass
      setter.accept(value);
      validator.forCreate(user).validate();
      return;
    }
    throw new RuntimeException("validation did not fail");
  }
}
