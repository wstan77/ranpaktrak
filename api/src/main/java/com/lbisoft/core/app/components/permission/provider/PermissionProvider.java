package com.lbisoft.core.app.components.permission.provider;

import com.lbisoft.core.app.db.model.generated.RolePermissionDb;
import com.lbisoft.core.framework.permissions.model.Permission;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;
import ma.glasnost.orika.metadata.ClassMapBuilder;

public class PermissionProvider
    extends AbstractProvider<RolePermissionDb, Permission> {

  @Override
  protected void defineClassMap(
      ClassMapBuilder<RolePermissionDb, Permission> classMapBuilder) {
    classMapBuilder.field("permissionTargetLk", "permissionTargetLk").field("permissionLevelLk", "permissionLevelLk");
  }

  @Override
  protected void defineChildren(ChildrenMap<Permission> childrenMap) {
  }
}
