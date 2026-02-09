package com.lbisoft.core.app.components.roles.provider;

import java.util.ArrayList;
import java.util.List;

import com.lbisoft.core.app.components.roles.model.Role;
import com.lbisoft.core.app.db.model.generated.RoleDb;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class RoleProvider extends AbstractProvider<RoleDb, Role> {

  @Override
  protected void defineClassMap(ClassMapBuilder<RoleDb, Role> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<Role> childrenMap) {
  }

  @Override
  protected Role completeMapAll(RoleDb input, Role output, List<?>... children) {
    for (List<?> list : children) {
      completeMap(input, output, list);
    }
    return output;
  }

  @Override
  protected Role completeMap(RoleDb input, Role output, Object... children) {
    for (Object child : children) {
      if (child instanceof List) {
        List<?> list = (List<?>) child;
        completeMap(input, output, list);
      }
    }
    return output;
  }

  @SuppressWarnings("unchecked")
  private void completeMap(RoleDb input, Role output, List<?> list) {
    output.setUserIds(new ArrayList<>());
    if (list.size() > 0 && list.get(0) instanceof UserRoleDb) {
      output.getUserIds().addAll(getUserIds(input, (List<UserRoleDb>) list));
    }
  }

  private List<Integer> getUserIds(RoleDb role, List<UserRoleDb> userRoles) {
    return userRoles.stream().filter(userRole -> userRole.getRoleId().equals(role.getRoleId()))
        .map(UserRoleDb::getUserId).toList();
  }
}
