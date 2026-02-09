package com.lbisoft.core.framework.utils;

import java.security.Principal;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.framework.user.JwtUser;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestHelper {
  private RequestHelper() {
  }

  public static AppUser getUser(HttpServletRequest request) {
    return getUser(request.getUserPrincipal());
  }

  public static AppUser getUser(Principal principal) {
    if (principal instanceof JwtAuthenticationToken) {
      JwtUser user = (JwtUser) ((JwtAuthenticationToken) principal).getDetails();
      if (user == null || user.getUser() == null) {
        return null;
      }
      AppUser appUser = (AppUser) user.getUser();
      appUser.setImpersonatedBy(user.getImpersonatedBy());
      return appUser;
    }
    return null;
  }

  /**
   * Gets the user id of the true current user. If a user is being impersonated,
   * this will return
   * the user id of the user impersonating them.
   *
   * @return
   */
  public static Integer getTrueUserId(HttpServletRequest request) {
    Principal principal = request.getUserPrincipal();
    if (principal instanceof JwtAuthenticationToken) {
      JwtUser user = (JwtUser) ((JwtAuthenticationToken) principal).getDetails();
      return user.getImpersonatedBy() == null ? user.getUser().getUserId()
          : user.getImpersonatedBy();
    }
    return null;
  }
}
