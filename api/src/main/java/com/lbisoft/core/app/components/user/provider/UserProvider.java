package com.lbisoft.core.app.components.user.provider;

import org.springframework.stereotype.Component;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.model.UserGroupUser;
import com.lbisoft.core.app.db.model.generated.UserDb;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;
import com.lbisoft.core.app.db.model.generated.UserSubordinateDb;
import com.lbisoft.core.framework.permissions.model.UserPermissions;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

@Component
public class UserProvider extends AbstractProvider<UserDb, AppUser> {

  @Override
  protected void defineClassMap(ClassMapBuilder<UserDb, AppUser> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<AppUser> childrenMap) {
    childrenMap
        .defineOneToOne(UserPermissions.class, AppUser::setPermissions,
            ChildrenMap.matchSimple(AppUser::getUserId, UserPermissions::getUserId))
        .defineList(UserRoleDb.class, AppUser::setUserRoles,
            ChildrenMap.matchSimple(AppUser::getUserId, UserRoleDb::getUserId))
        .defineList(UserSubordinateDb.class, AppUser::setSubordinates,
            ChildrenMap.matchSimple(AppUser::getUserId, UserSubordinateDb::getUserId))
        .defineList(UserGroupUser.class, AppUser::setUserGroupUsers,
            ChildrenMap.matchSimple(AppUser::getUserId, UserGroupUser::getUserId));
  }
}
