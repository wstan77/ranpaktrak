package com.lbisoft.core.framework.permissions;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.provider.UserProvider;
import com.lbisoft.core.app.db.model.generated.UserDb;
import com.lbisoft.core.framework.permissions.model.Permission;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionTarget;
import com.lbisoft.core.framework.permissions.model.UserPermissions;

@ExtendWith(MockitoExtension.class)
public class PermissionsTest {
  private static final String TEST_PERMISSION = "TEST";
  private static final Integer TEST_ROLE_ID = 1;
  private static final Integer TEST_USER_ID = 2;

  private final UserDb user = new UserDb();
  private UserProvider provider = new UserProvider();

  @BeforeEach
  public void init() {
    user.setUserId(TEST_USER_ID);
  }

  // has permission (EDIT, OWN, TEST)
  @Test
  public void testHasPermission() {
    boolean hasPermission = PermissionsHelper.hasPermission(getEditOwnTestUser(),
        PermissionLevel.EDIT, PermissionTarget.OWN, TEST_PERMISSION);
    assertTrue(hasPermission, "Has Permission to Edit Own");
  }

  // has permission lower level (READ, OWN, TEST)
  @Test
  public void testHasPermissionLowerLevel() {
    boolean hasPermission = PermissionsHelper.hasPermission(getEditOwnTestUser(),
        PermissionLevel.READ, PermissionTarget.OWN, TEST_PERMISSION);
    assertTrue(hasPermission, "Has Permission to Read Own with Edit Permissions");
  }

  // user doesn't have level - no permission (DELETE, OWN, TEST)
  @Test
  public void testNoPermissionHigherLevel() {
    boolean hasPermission = PermissionsHelper.hasPermission(getEditOwnTestUser(),
        PermissionLevel.DELETE, PermissionTarget.OWN, TEST_PERMISSION);
    assertFalse(hasPermission, "Doesn't Have Permission to Delete Own with Edit Permissions");
  }

  // user has level, but in restricted target - no permission (EDIT, ALL,
  // TEST)
  @Test
  public void testNoPermissionBroaderTarget() {
    boolean hasPermission = PermissionsHelper.hasPermission(getEditOwnTestUser(),
        PermissionLevel.EDIT, PermissionTarget.ALL, TEST_PERMISSION);
    assertFalse(hasPermission,
        "Doesn't Have Permission to Edit Others Own with Edit Own Permissions");
  }

  // utility method to return user with (EDIT, OWN, TEST) permission
  private AppUser getEditOwnTestUser() {
    Permission permission = new Permission();
    permission.setRoleId(TEST_ROLE_ID);
    permission.setPermissionLk(TEST_PERMISSION);
    permission.setPermissionLevelLk(PermissionLevel.EDIT);
    permission.setPermissionTargetLk(PermissionTarget.OWN);
    return getUserWithPermission(permission);
  }

  private AppUser getUserWithPermission(Permission permission) {
    UserPermissions permissions = UserPermissions.create(user.getUserId(), List.of(permission));
    return provider.map(user, permissions);
  }
}
