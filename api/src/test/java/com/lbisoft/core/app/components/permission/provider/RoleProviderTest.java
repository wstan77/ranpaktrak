package com.lbisoft.core.app.components.permission.provider;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lbisoft.core.app.components.roles.model.Role;
import com.lbisoft.core.app.components.roles.provider.RoleProvider;
import com.lbisoft.core.app.db.model.generated.RoleDb;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;

public class RoleProviderTest {
  private final RoleProvider provider = new RoleProvider();
  private RoleDb db;
  private Role model;
  private List<UserRoleDb> userRoles;

  @BeforeEach
  public void setup() {

    db = (RoleDb) new RoleDb().withRoleId(1).withRoleName("roleName").withRoleNameShort("roleNameShort")
        .withSeqNo((short) 2).withActiveFlg(true);

    UserRoleDb goodUserRole = (UserRoleDb) new UserRoleDb().withRoleId(db.getRoleId()).withUserId(123);
    UserRoleDb badUserRole = (UserRoleDb) new UserRoleDb().withRoleId(db.getRoleId() + 1).withUserId(321);
    userRoles = List.of(goodUserRole, badUserRole);

    model = new Role();
    model.setRoleId(db.getRoleId());
    model.setRoleName(db.getRoleName());
    model.setRoleNameShort(db.getRoleNameShort());
    model.setSeqNo(db.getSeqNo());
    model.setActiveFlg(db.getActiveFlg());
    model.setUserIds(List.of(goodUserRole.getUserId()));
  }

  @Test
  public void shouldConvertDbToModel() {
    Role test = provider.map(db, userRoles);
    assertThat(test).usingRecursiveComparison().isEqualTo(model);
  }

  @Test
  public void shouldConvertModeltoDb() {
    RoleDb test = provider.reverseMap(model);
    assertThat(test).usingRecursiveComparison().isEqualTo(db);
  }
}
