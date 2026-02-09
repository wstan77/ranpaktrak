package com.lbisoft.core.framework.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.lbisoft.core.framework.user.JwtUser;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityHelper {
  private static char[] VALID_SECRET_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456879!$"
      .toCharArray();
  private static double BYTE_MULTIPLIER = VALID_SECRET_CHARACTERS.length / 255.0;

  private SecurityHelper() {
  }

  /** Check to use if a user has the proper role given to registered users */
  public static boolean isAuthenticated(Authentication authentication) {
    for (GrantedAuthority auth : authentication.getAuthorities()) {
      if ("ROLE_USER".equals(auth.getAuthority()))
        return true;
    }
    return false;
  }

  public static JwtUser getUserFromAuthentication(Authentication authentication) {
    return (JwtUser) authentication.getPrincipal();
  }

  /**
   * Give a user ROLE_USER role to a user. This should be given once a user has be
   * authenticated via
   * Two-Factor
   */
  public static Authentication authenticateUser(Authentication authentication) {
    JwtUser userDetails = (JwtUser) authentication.getPrincipal();

    // remove NEEDS_TF role
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (GrantedAuthority gaAuthority : userDetails.getAuthoritySet()) {
      if (!gaAuthority.getAuthority().equals("NEEDS_TF")) {
        authorities.add(gaAuthority);
      }
    }
    // give them permission to use the app now
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

    Authentication newAuth = new UsernamePasswordAuthenticationToken(
        authentication.getPrincipal(), authentication.getCredentials(), authorities);
    SecurityContextHolder.getContext().setAuthentication(newAuth);
    return newAuth;
  }

  public static String generateStrongSecret(int length) throws NoSuchAlgorithmException {
    SecureRandom rand = SecureRandom.getInstanceStrong();
    byte[] randomValues = new byte[length];
    rand.nextBytes(randomValues);

    StringBuilder secretBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int index = (int) ((randomValues[i] + 127) * BYTE_MULTIPLIER);
      secretBuilder.append(VALID_SECRET_CHARACTERS[index]);
    }
    return secretBuilder.toString();
  }
}
