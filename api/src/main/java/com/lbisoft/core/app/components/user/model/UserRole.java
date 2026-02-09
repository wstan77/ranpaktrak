package com.lbisoft.core.app.components.user.model;

import java.io.Serializable;

import com.lbisoft.core.framework.user.IRole;

public class UserRole implements IRole, Serializable {
  private static final long serialVersionUID = 6599488097193400508L;

  private Integer userId;
  private Integer roleId;
  private String roleName;
  private String roleNameShort;

  @Override
  public String getRoleLk() {
    return roleNameShort == null ? null : roleNameShort.toUpperCase().replace(' ', '_');
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleNameShort() {
    return roleNameShort;
  }

  public void setRoleNameShort(String roleNameShort) {
    this.roleNameShort = roleNameShort;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer id) {
    this.roleId = id;
  }
}
