package com.lbisoft.core.app.components.user.service;

import java.util.Collection;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.framework.user.JwtUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

@Component
public class LDAPUserContextMapper implements UserDetailsContextMapper {

  private final UserService userService;

  @Autowired
  public LDAPUserContextMapper(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
      Collection<? extends GrantedAuthority> authorities) {
    AppUser user = userService.getUserByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found in WhoData: " + username));

    return new JwtUser(user);
  }

  @Override
  public void mapUserToContext(UserDetails userDetails, DirContextAdapter context) {
    throw new UnsupportedOperationException();
  }

}
