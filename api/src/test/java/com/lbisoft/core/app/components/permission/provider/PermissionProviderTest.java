package com.lbisoft.core.app.components.permission.provider;

import static org.assertj.core.api.Assertions.*;

import com.lbisoft.core.app.db.model.generated.RolePermissionDb;
import com.lbisoft.core.framework.permissions.model.Permission;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionTarget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PermissionProviderTest {
  private final PermissionProvider provider = new PermissionProvider();
  private RolePermissionDb db;
  private Permission model;

  @BeforeEach
  public void setup() {
    String level = "READ";
    String target = "ALL";
    db = (RolePermissionDb) new RolePermissionDb().withPermissionLevelLk(level).withRoleId(1)
        .withPermissionLk("permissionLk").withPermissionTargetLk(target);

    model = new Permission();
    model.setRoleId(db.getRoleId());
    model.setPermissionLk(db.getPermissionLk());
    model.setPermissionLevelLk(PermissionLevel.valueOf(level));
    model.setPermissionTargetLk(PermissionTarget.valueOf(target));

  }

  @Test
  public void shouldConvertDbToModel() {
    Permission test = provider.map(db);
    assertThat(test).usingRecursiveComparison().isEqualTo(model);
  }

  @Test
  public void shouldConvertModeltoDb() {
    RolePermissionDb test = provider.reverseMap(model);
    assertThat(test).usingRecursiveComparison().isEqualTo(db);
  }
}
