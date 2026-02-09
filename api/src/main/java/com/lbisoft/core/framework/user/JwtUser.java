package com.lbisoft.core.framework.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JwtUser implements UserDetails {

  private static final long serialVersionUID = 3359021032306211633L;

  private final IUser user;

  private final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

  private Integer impersonatedBy;

  public JwtUser(IUser user) {
    this.user = user;

    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

    if (user.getPermissions() != null) {
      authorities
          .addAll(user.getPermissions().getPermissionStrings().stream().map(SimpleGrantedAuthority::new).toList());
    }
    if (user.getRoles() != null) {
      authorities.addAll(
          user.getRoles().stream().map(role -> "ROLE_" + role.getRoleLk()).map(SimpleGrantedAuthority::new).toList());
    }
  }

  public IUser getUser() {
    return user;
  }

  @JsonIgnore
  public Set<GrantedAuthority> getAuthoritySet() {
    return authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  @JsonIgnore
  public String getPassword() {
    return "";
  }

  @Override
  @JsonIgnore
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }

  public Integer getImpersonatedBy() {
    return impersonatedBy;
  }

  public void setImpersonatedBy(Integer impersonatedBy) {
    this.impersonatedBy = impersonatedBy;
  }
}
