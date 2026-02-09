package com.lbisoft.core.app.components.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryAuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;

import com.lbisoft.core.app.components.user.service.LDAPUserContextMapper;
import com.lbisoft.core.app.settings.AuthenticationSettings;
import com.lbisoft.core.app.settings.LDAPSettings;
import com.lbisoft.core.framework.exceptions.auth.PasswordExpiredException;

import jakarta.annotation.PostConstruct;

@Component
public class CompleteAuthProvider implements AuthenticationProvider {

  @Autowired
  private LDAPSettings ldapSettings;
  @Autowired
  private LDAPUserContextMapper userDetailsContextMapper;
  @Autowired
  private AuthenticationSettings authSettings;

  private AuthenticationProvider authProvider;

  @PostConstruct()
  public void init() {
    authProvider = primaryAuthenticationProvider();
  }

  @Override
  public Authentication authenticate(Authentication auth) throws AuthenticationException {
    // 1. Validate credentials via LDAP / AWS Cognito
    Authentication result = null;
    try {
      String principal = (String) auth.getPrincipal();
      String username = principal;
      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
          auth.getCredentials(), auth.getAuthorities());
      result = authProvider.authenticate(token);
    } catch (AuthenticationException e) {
      if (e.getCause() instanceof ActiveDirectoryAuthenticationException) {
        ActiveDirectoryAuthenticationException adException = (ActiveDirectoryAuthenticationException) e.getCause();
        if (adException.getDataCode().equals("773") || adException.getDataCode().equals("532")) {
          throw new PasswordExpiredException("Password Expired");
        }
      }

      throw e;
    }
    return completeAuth(result);
  }

  public AuthenticationProvider primaryAuthenticationProvider() {
    switch (authSettings.getMethod()) {
      case DUMMY:
        return new DummyAuthProvider();
      case LDAP:
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
            ldapSettings.getUserDomain(), "ldaps://" + ldapSettings.getServer() + ":636");

        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUserDetailsContextMapper(userDetailsContextMapper);

        return provider;
    }
    return null;
  }

  private Authentication completeAuth(Authentication auth) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    return new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return true;
  }
}
