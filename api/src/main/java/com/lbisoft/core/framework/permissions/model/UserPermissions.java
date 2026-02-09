package com.lbisoft.core.framework.permissions.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lbisoft.core.framework.comparator.Sort;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionTarget;

public class UserPermissions implements Serializable {
  private static final long serialVersionUID = -4003025589304664730L;

  @JsonIgnore
  private Integer userId;

  private static final List<PermissionLevel> PERMISSION_LEVELS = List.of(PermissionLevel.values())
      .stream().filter(level -> level != PermissionLevel.NONE).toList();
  private List<Permission> permissions = Collections.emptyList();
  private Map<String, Map<String, Permission>> byPermissionAndTarget = Collections.emptyMap();

  public List<String> getPermissionStrings() {
    return permissions.stream().flatMap(permission -> {
      return PERMISSION_LEVELS.stream().filter(level -> level.compareTo(permission.getPermissionLevelLk()) <= 0)
          .flatMap(level -> {
            return Stream.of(String.format("CAN_%s_%s", level.toString(), permission.getPermissionLk()), String
                .format("CAN_%s_%s_%s", level.toString(), permission.getPermissionTargetLk().toString(),
                    permission.getPermissionLk()));
          });
    }).distinct().toList();
  }

  public List<Permission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<Permission> permissions) {
    this.permissions = permissions;
  }

  public Map<String, Map<String, Permission>> getByPermissionAndTarget() {
    return byPermissionAndTarget;
  }

  public void setByPermissionAndTarget(Map<String, Map<String, Permission>> byPermissionAndTarget) {
    this.byPermissionAndTarget = byPermissionAndTarget;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public boolean contains(PermissionLevel level, PermissionTarget target, String type) {
    return Optional.ofNullable(byPermissionAndTarget.get(type)).map(byTarget -> byTarget.get(target.toString()))
        .map(permission -> level.ordinal() <= permission.getPermissionLevelLk().ordinal()).orElse(false);
  }

  public static UserPermissions create(Integer userId, List<Permission> permissions) {
    UserPermissions userPermissions = new UserPermissions();
    Map<String, Map<String, Permission>> map = permissions.stream()
        .sorted(Sort.asc(UserPermissions::getPermissionLevelOrder))
        .collect(Collectors.groupingBy(Permission::getPermissionLk,
            Collectors.toMap(permission -> permission.getPermissionTargetLk().toString(), permission -> permission)));

    userPermissions.setUserId(userId);
    userPermissions.setByPermissionAndTarget(map);
    userPermissions.setPermissions(permissions);

    return userPermissions;
  }

  private static Integer getPermissionLevelOrder(Permission permission) {
    return Integer.valueOf(permission.getPermissionLevelLk().ordinal());
  }
}
