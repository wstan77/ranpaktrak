package com.lbisoft.core.app.components.user.service;

import org.springframework.stereotype.Component;

import com.lbisoft.core.framework.permissions.PermissionsHelper;
import com.lbisoft.core.framework.permissions.model.Ownable;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionTarget;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class allows for defining custom methods for use in securing
 * methods/endpoints with spring
 * security. Methods defined in this class should always return a boolean
 * representing whether
 * permission is granted or not
 */
@Component("userPermissionEvaluator")
public class UserPermissionEvaluator {

  public boolean hasOwnablePermission(
      HttpServletRequest request, String level, String target, Ownable ownable) {
    return PermissionsHelper.hasPermission(
        RequestHelper.getUser(request),
        PermissionLevel.valueOf(level),
        PermissionTarget.valueOf(target),
        ownable.getPermissionLk());
  }
}
