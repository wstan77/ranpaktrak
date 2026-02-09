package com.lbisoft.core.framework.security;

import java.io.IOException;

import org.springframework.security.web.RedirectStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NoRedirectStrategy implements RedirectStrategy {

  @Override
  public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
      throws IOException {
    // No redirects, the backend is purely a REST API
  }
}
