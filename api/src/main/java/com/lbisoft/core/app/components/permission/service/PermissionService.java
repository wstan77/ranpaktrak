package com.lbisoft.core.app.components.permission.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.lookup.service.Lookups;
import com.lbisoft.core.app.components.permission.provider.PermissionProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.app.db.mapper.generated.RolePermissionDbMapper;
import com.lbisoft.core.app.db.model.generated.LkPermissionDb;
import com.lbisoft.core.app.db.model.generated.LkPermissionTargetDb;
import com.lbisoft.core.app.db.model.generated.RolePermissionDb;
import com.lbisoft.core.app.db.model.generated.RolePermissionDbExample;
import com.lbisoft.core.app.db.model.generated.RolePermissionDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.permissions.model.Permission;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionTarget;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;

@CoreService
public class PermissionService {

  private final Dao<RolePermissionDb, RolePermissionDbKey> dao;
  private final PermissionProvider provider;
  private final Lookups lookups;
  private final UserActivitiesService activitiesService;

  @Autowired
  public PermissionService(RolePermissionDbMapper mapper, @Lazy Lookups lookups,
      UserActivitiesService activitiesService) {
    dao = Dao.getDao(mapper, k -> k);
    provider = new PermissionProvider();
    this.lookups = lookups;
    this.activitiesService = activitiesService;
  }

  @Transactional(readOnly = true)
  public List<Permission> getPermissions(AppUser user) {
    List<LkPermissionDb> lks = lookups.getListByClass(LkPermissionDb.class);
    return provider.mapAll(dao.select(new RolePermissionDbExample()), lks);
  }

  public ResponseObject savePermissions(List<Permission> permissions, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);
    List<Integer> roleIdsUpdated = permissions.stream().map(Permission::getRoleId).distinct().toList();
    if (!roleIdsUpdated.isEmpty()) {
      RolePermissionDbExample byRoleId = new RolePermissionDbExample();
      byRoleId.createCriteria().andRoleIdIn(roleIdsUpdated);
      dao.delete(byRoleId);
    }
    if (!permissions.isEmpty()) {
      List<RolePermissionDb> dbs = provider.reverseMapAll(permissions);
      dao.insert(dbs, user);
    }
    activitiesService.logActivity(
        new UserActivityBuilder(ActivityTypes.SAVE_PERMISSIONS).relatedObject(null).build(),
        request);
    return new ResponseObject().addResponseInfo("Successfully saved permissions");
  }

  public void addDefaultPermissions(Integer roleId, AppUser user) {
    var lks = lookups.getListByClass(LkPermissionDb.class);
    var targets = lookups.getListByClass(LkPermissionTargetDb.class);
    List<Permission> permissions = lks.stream().flatMap(lk -> targets.stream()
        .map(LkPermissionTargetDb::getPermissionTargetLk)
        .filter(target -> lk.getHasTargetFlg() || target.equals(PermissionTarget.ALL.name())).map(target -> {
          Permission p = new Permission();
          p.setRoleId(roleId);
          p.setPermissionLk(lk.getPermissionLk());
          p.setPermissionTargetLk(PermissionTarget.valueOf(target));
          p.setPermissionLevelLk(PermissionLevel.valueOf(lk.getDefaultLevelLk()));
          return p;
        })).toList();
    dao.insert(provider.reverseMapAll(permissions), user);
  }
}
