package com.lbisoft.core.app.components.user.validator;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.framework.validation.Validator;

public class UserValidator {

  public Validator forCreate(AppUser user) {
    Validator validator = new Validator();
    validator.check("Username", user.getUsername()).isNotEmpty().and()
        .check("First Name", user.getFirstName()).isNotEmpty().isUnderMaximumCharacters(100).and()
        .check("Last Name", user.getLastName()).isNotEmpty().isUnderMaximumCharacters(100);
    return validator;
  }
}
