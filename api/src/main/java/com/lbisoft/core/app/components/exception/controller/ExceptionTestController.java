package com.lbisoft.core.app.components.exception.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.framework.exceptions.InsufficientPermissionException;
import com.lbisoft.core.framework.exceptions.ModifiedSinceLoadedException;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.validation.Validator;

@RestController()
@RequestMapping("api/test-exceptions/")
public class ExceptionTestController {
  
  @GetMapping("not-found")
  public void testNotFoundException() { 
    throw new ResourceNotFoundException("Requested user not found.");
  }
  
  @GetMapping("concurrent")
  public void testConcurrentException() { 
    throw new ModifiedSinceLoadedException();
  }
  
  @GetMapping("permission")
  public void testPermissionException() { 
    throw new InsufficientPermissionException();
  }
  
  @GetMapping("form-validation")
  public void testFormValidationException() { 
    Validator validator = new Validator();
    
    validator
      .check("Foo", 101).isAtMost(100).and()
      .check("Bar", "").isNotEmpty();
    
    validator.validate();
  }
  
  
}
