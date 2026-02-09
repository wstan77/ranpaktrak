package com.lbisoft.core.framework.permissions;

import java.util.List;
import java.util.Optional;

import com.lbisoft.core.framework.permissions.model.Ownable;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionTarget;
import com.lbisoft.core.framework.user.HasPermissions;

public final class PermissionsHelper {

  private PermissionsHelper() {
  }

  public static boolean hasPermission(HasPermissions user, PermissionLevel level, PermissionTarget target,
      String type) {
    return user.getPermissions().contains(level, PermissionTarget.ALL, type)
        || user.getPermissions().contains(level, target, type);
  }

  public static boolean hasPermission(HasPermissions user, PermissionLevel level, Ownable ownable) {
    if (ownable == null) {
      return false;
    }

    PermissionTarget target = getTargetByUserId(user, ownable.getOwnerUserId());

    return hasPermission(user, level, target, ownable.getPermissionLk());
  }

  public static boolean hasPermission(HasPermissions user, PermissionLevel level, Integer ownerUserId,
      String permissionLk) {
    PermissionTarget target = getTargetByUserId(user, ownerUserId);

    return hasPermission(user, level, target, permissionLk);
  }

  private static PermissionTarget getTargetByUserId(HasPermissions user, Integer ownerUserId) {
    PermissionTarget target = PermissionTarget.ALL;
    if (ownerUserId != null) {
      if (ownerUserId.equals(user.getUserId())) {
        target = PermissionTarget.OWN;
      } else if (user.getSubordinateIds().contains(ownerUserId)) {
        target = PermissionTarget.SUB;
      }
    }
    return target;
  }

  public static <T extends Ownable> Optional<T> filterByPermissions(HasPermissions user, PermissionLevel level,
      T ownable) {
    return hasPermission(user, level, ownable) ? Optional.of(ownable) : Optional.empty();
  }

  public static <T extends Ownable> List<T> filterByPermissions(HasPermissions user, PermissionLevel level,
      List<T> ownables) {
    return ownables.stream().filter(ownable -> hasPermission(user, level, ownable)).toList();
  }

}
