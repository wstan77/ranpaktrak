package com.lbisoft.core.app.components.roles.model;

import java.util.List;

public class Role {
  private Integer roleId;
  private String roleName;
  private String roleNameShort;
  private Short seqNo;
  private Boolean activeFlg;
  private List<Integer> userIds;

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

  public Short getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(Short seqNo) {
    this.seqNo = seqNo;
  }

  public Boolean getActiveFlg() {
    return activeFlg;
  }

  public void setActiveFlg(Boolean activeFlg) {
    this.activeFlg = activeFlg;
  }
}
