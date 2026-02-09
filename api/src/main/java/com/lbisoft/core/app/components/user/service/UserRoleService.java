package com.lbisoft.core.app.components.user.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.RoleDbMapper;
import com.lbisoft.core.app.db.mapper.generated.UserRoleDbMapper;
import com.lbisoft.core.app.db.model.generated.RoleDb;
import com.lbisoft.core.app.db.model.generated.RoleDbExample;
import com.lbisoft.core.app.db.model.generated.UserRoleDb;
import com.lbisoft.core.app.db.model.generated.UserRoleDbExample;
import com.lbisoft.core.app.db.model.generated.UserRoleDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class UserRoleService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Dao<UserRoleDb, UserRoleDbKey> dao;
    private final Dao<RoleDb, Integer> roleDao;

    @Autowired
    public UserRoleService(UserRoleDbMapper mapper, RoleDbMapper roleMapper) {
        this.dao = Dao.getDao(mapper, s -> s);
        this.roleDao = Dao.getDao(roleMapper, RoleDb::getRoleId);
    }

    @Transactional(readOnly = true)
    public List<UserRoleDb> getRolesForUsers(List<Integer> userIds) {
        UserRoleDbExample example = new UserRoleDbExample();
        example.createCriteria().andUserIdIn(userIds);

        return dao.select(example);
    }

    @Transactional(readOnly = true)
    public List<UserRoleDb> getUserForRoles(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        UserRoleDbExample example = new UserRoleDbExample();
        example.createCriteria().andRoleIdIn(roleIds);

        return dao.select(example);
    }

    public ResponseObject updateUsersForRole(List<Integer> userIds, Integer roleId, AppUser user) {
        if (userIds == null) {
            return new ResponseObject().addResponseWarning("No users specified");
        }
        LOGGER.info("UR01: Adding " + userIds.size() + " users to role_id=" + roleId);
        // delete role for old users
        UserRoleDbExample example = new UserRoleDbExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        dao.delete(example);

        // insert role for new users
        userIds.forEach(userId -> {
            LOGGER.info("\tUR01: user_id=" + userId);
            UserRoleDb db = new UserRoleDb();
            db.setUserId(userId);
            db.setRoleId(roleId);
            dao.insert(db, user);
        });
        return new ResponseObject().addResponseInfo("Roles have been updated");
    }

    public void removeRoleFromAllUsers(Integer roleId, AppUser user) {
        LOGGER.info("UR02: Removing role roleId=" + roleId + " from all users");

        // delete roles in case this is a dupe
        UserRoleDbExample example = new UserRoleDbExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        dao.delete(example);
    }

    public void removeUserFromAllRoles(Integer userId, AppUser user) {
        LOGGER.info("UR03: Removing user userId=" + userId + " from all roles");

        // delete roles in case this is a dupe
        UserRoleDbExample example = new UserRoleDbExample();
        example.createCriteria().andUserIdEqualTo(userId);
        dao.delete(example);
    }

    @Transactional(readOnly = true)
    public List<UserRoleDb> getRolesForUser(Integer userId) {
        UserRoleDbExample example = new UserRoleDbExample();
        example.createCriteria().andUserIdEqualTo(userId);

        return dao.select(example);
    }

    public ResponseObject updateRolesForUser(Integer userId, List<UserRoleDb> roles, AppUser currentUser) {
        roles.forEach(role -> role.setUserId(userId));

        UserRoleDbExample example = new UserRoleDbExample();
        example.createCriteria().andUserIdEqualTo(userId);
        dao.delete(example);

        dao.insert(roles, currentUser);
        return new ResponseObject().addResponseInfo("Successfully saved user roles.");

    }

    public void assignRoleToUser(String rolename, AppUser user) {
        // load role record
        RoleDbExample ex = new RoleDbExample();
        ex.createCriteria().andRoleNameEqualTo(rolename);
        RoleDb r = roleDao.selectOne(ex).orElse(null);
        // insert user role record
        if (r != null && user != null && user.getUserId() != null) {
            UserRoleDb rDb = new UserRoleDb();
            rDb.setUserId(user.getUserId());
            rDb.setRoleId(r.getRoleId());
            dao.insert(rDb, user);
        }
    }

}
