package com.lbisoft.core.framework.user;

import java.util.List;

import com.lbisoft.core.framework.permissions.model.UserPermissions;

public interface HasPermissions extends HasUserId {

  UserPermissions getPermissions();

  List<Integer> getSubordinateIds();
}
