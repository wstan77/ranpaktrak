package com.lbisoft.core.app.components.roles.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.lookup.service.Lookups;
import com.lbisoft.core.app.components.permission.service.PermissionService;
import com.lbisoft.core.app.components.roles.model.Role;
import com.lbisoft.core.app.components.roles.provider.RoleProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.service.UserRoleService;
import com.lbisoft.core.app.db.mapper.custom.UserPermissionExtendedMapper;
import com.lbisoft.core.app.db.mapper.generated.RoleDbMapper;
import com.lbisoft.core.app.db.model.generated.RoleDb;
import com.lbisoft.core.app.db.model.generated.RoleDbExample;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.utils.BooleanHelper;
import com.lbisoft.core.framework.utils.LoggerHelper;
import com.lbisoft.core.framework.validation.Validator;

@CoreService
public class RoleService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Dao<RoleDb, Integer> dao;
    private final RoleProvider provider;
    private final UserRoleService userRoleService;
    private final UserPermissionExtendedMapper permissionMapper;
    private final PermissionService permissionService;

    private final Lookups lookups;

    @Autowired
    public RoleService(RoleDbMapper mapper, UserRoleService userRoleService,
            UserPermissionExtendedMapper permissionMapper, PermissionService permissionService, @Lazy Lookups lookups) {
        this.userRoleService = userRoleService;
        this.permissionMapper = permissionMapper;
        this.permissionService = permissionService;
        this.lookups = lookups;

        dao = Dao.getDao(mapper, RoleDb::getRoleId);
        provider = new RoleProvider();
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return populateRoles(dao.select(new RoleDbExample()));
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Integer roleId) {
        return dao.select(roleId).flatMap(roleDb -> populateRoles(List.of(roleDb)).stream().findAny());
    }

    private List<Role> populateRoles(List<RoleDb> roleDbs) {
        List<Integer> roleIds = roleDbs.stream().map(RoleDb::getRoleId).distinct().toList();
        List<UserRoleDb> roleUsers = permissionMapper.getUsersForRoles(roleIds);

        return provider.mapAll(roleDbs, roleUsers);
    }

    public Role saveRole(Role role, AppUser user) {
        boolean isNew = (role.getRoleId() == null);
        // validate user
        validateRole(role);

        // save user
        role.setActiveFlg(true);
        if (isNew) {
            role = createRole(role, user);
        } else {
            LoggerHelper.userIs("updating role ID " + role.getRoleId(), LOGGER, user);
            dao.update(provider.reverseMap(role), user);
        }

        // save user-roles
        userRoleService.updateUsersForRole(role.getUserIds(), role.getRoleId(), user);

        // add success messsage
        if (isNew) {
            user.addResponseInfo("Created new role: " + role.getRoleName());
        } else {
            user.addResponseInfo(role.getRoleName() + " has been saved");
        }

        // reload lookups
        lookups.loadRoles();

        return getRoleById(role.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("Role is missing"));
    }

    private Role createRole(Role role, AppUser user) {
        LoggerHelper.userIs("Creating new role: " + role.getRoleName(), LOGGER, user);

        RoleDb roleDb = provider.reverseMap(role);
        dao.insert(roleDb, user);
        // Get role id from inserted record
        role.setRoleId(roleDb.getRoleId());

        permissionService.addDefaultPermissions(role.getRoleId(), user);

        return role;
    }

    private void validateRole(Role role) {
        new Validator()

                .check("Role Name", role.getRoleName()).isNotEmpty().isUnderMaximumCharacters(100).and()

                .check("Short Name", role.getRoleNameShort()).isUnderMaximumCharacters(15).and()

                .validate();
    }

    public Optional<Role> disableRole(Integer roleId, AppUser currentUser) {
        return getRoleById(roleId).map(role -> {
            if (BooleanHelper.isTrue(role.getActiveFlg())) {
                role.setActiveFlg(false);
                dao.update(provider.reverseMap(role), currentUser);
                userRoleService.removeRoleFromAllUsers(roleId, currentUser);
                lookups.loadRoles();
                return role;
            }
            return null;
        });
    }

    public Optional<Role> enableUser(Integer roleId, AppUser currentUser) {
        return getRoleById(roleId).map(role -> {
            if (BooleanHelper.isFalse(role.getActiveFlg())) {
                role.setActiveFlg(true);
                dao.update(provider.reverseMap(role), currentUser);
                lookups.loadRoles();
                return role;
            }
            return role;
        });
    }

}
