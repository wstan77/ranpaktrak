package com.lbisoft.core.app.components.user.model;

import java.util.List;

public class RoleUpdateForm {
  private List<Integer> userIds;
  private Integer roleId;

  public List<Integer> getUserIds() {
    return userIds;
  }

  public void setUserIds(List<Integer> userIds) {
    this.userIds = userIds;
  }

  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }
}
