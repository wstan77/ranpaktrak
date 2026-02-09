package com.lbisoft.core.framework.permissions.model;

import java.io.Serializable;

public class Permission implements Serializable {
  public static enum PermissionLevel {
    NONE, READ, EDIT, DELETE
  }

  public static enum PermissionTarget {
    OWN, SUB, ALL
  }

  private static final long serialVersionUID = -6561207479716754106L;

  private Integer roleId;
  private String permissionLk;
  private PermissionLevel permissionLevelLk;
  private PermissionTarget permissionTargetLk;

  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

  public String getPermissionLk() {
    return permissionLk;
  }

  public void setPermissionLk(String permissionLk) {
    this.permissionLk = permissionLk;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public PermissionLevel getPermissionLevelLk() {
    return permissionLevelLk;
  }

  public void setPermissionLevelLk(PermissionLevel permissionLevelLk) {
    this.permissionLevelLk = permissionLevelLk;
  }

  public PermissionTarget getPermissionTargetLk() {
    return permissionTargetLk;
  }

  public void setPermissionTargetLk(PermissionTarget permissionTargetLk) {
    this.permissionTargetLk = permissionTargetLk;
  }


}
