package com.lbisoft.core.app.components.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class DummyAuthProvider implements AuthenticationProvider {

  @Override
  public Authentication authenticate(Authentication auth) throws AuthenticationException {
    String username = (String) auth.getPrincipal();
    String pwd = (String) auth.getCredentials();

    if (username == null) {
      throw new BadCredentialsException("No Username");
    }
    if (pwd == null) {
      throw new BadCredentialsException("No Password");
    }

    if ("admin".equals(username) && "admin".equals(pwd)) {
      return auth;
    } else if ("scout".equals(username) && "scout".equals(pwd)) {
      return auth;
    }

    throw new BadCredentialsException("Username or Password is incorrect");
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return true;
  }
}
