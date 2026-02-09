package com.lbisoft.core.app.db.mapper.custom;

import java.util.List;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;
import com.lbisoft.core.framework.permissions.model.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * The UserPermissionExtendedMapper interface provides custom queries for accessing permissions data
 * for specific users
 */
@Mapper
public interface UserPermissionExtendedMapper {

  List<UserRoleDb> getUsersForRoles(List<Integer> roleIds);

  List<UserRoleDb> getRolesForUsers(List<Integer> userIds);

  /**
   * Returns all permissions associated with roles possessed by the specified user as a
   * {@link Sequence} of {@link Permission} objects
   *
   * @param userId unique {@link Integer} identifier of user being queried
   * @return
   */
  List<Permission> getUserPermissions(@Param("userId") Integer userId);
}
