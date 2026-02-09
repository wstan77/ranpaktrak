package com.lbisoft.core.framework.service;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public class CoreServices {
  @Retention(RUNTIME)
  @Target({TYPE})
  @Service
  @Transactional
  public @interface CoreService {}
}
