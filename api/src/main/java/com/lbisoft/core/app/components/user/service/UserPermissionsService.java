package com.lbisoft.core.app.components.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.db.mapper.custom.UserPermissionExtendedMapper;
import com.lbisoft.core.framework.permissions.model.Permission;
import com.lbisoft.core.framework.permissions.model.UserPermissions;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
@Transactional(readOnly = true)
public class UserPermissionsService {

  private final UserPermissionExtendedMapper extendedMapper;

  @Autowired
  public UserPermissionsService(UserPermissionExtendedMapper extendedMapper) {
    this.extendedMapper = extendedMapper;
  }

  public UserPermissions getUsersPermissions(Integer userId) {
    if (userId == null) {
      return null;
    }

    List<Permission> permissions = extendedMapper.getUserPermissions(userId);

    return UserPermissions.create(userId, permissions);
  }
}
