package com.lbisoft.core.framework.permissions.model;

public interface Ownable {
  Integer getOwnerUserId();

  String getPermissionLk();
}
