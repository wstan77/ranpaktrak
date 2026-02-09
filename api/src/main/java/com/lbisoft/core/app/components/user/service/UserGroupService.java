package com.lbisoft.core.app.components.user.service;

import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;

import com.lbisoft.core.app.components.user.event.UserGroupsChangedEvent;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.model.UserGroup;
import com.lbisoft.core.app.components.user.model.UserGroupUser;
import com.lbisoft.core.app.db.mapper.custom.UserGroupExtendedMapper;
import com.lbisoft.core.app.db.mapper.generated.UserGroupDbMapper;
import com.lbisoft.core.app.db.mapper.generated.UserGroupUserDbMapper;
import com.lbisoft.core.app.db.model.generated.UserGroupDb;
import com.lbisoft.core.app.db.model.generated.UserGroupDbExample;
import com.lbisoft.core.app.db.model.generated.UserGroupUserDb;
import com.lbisoft.core.app.db.model.generated.UserGroupUserDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.dao.DaoWithoutPrimaryKey;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class UserGroupService {

  private final Dao<UserGroupDb, Integer> dao;
  private final DaoWithoutPrimaryKey<UserGroupUserDb> userDao;

  private final AbstractProvider<UserGroupDb, UserGroup> provider = ProviderBuilder.simple(UserGroupDb.class,
      UserGroup.class);

  private final AbstractProvider<UserGroupUserDb, UserGroupUser> userProvider = new ProviderBuilder<>(
      UserGroupUserDb.class, UserGroupUser.class)
      .field("userGroupId", "userGroup.userGroupId")
      .defineOneToOne(UserGroup.class, UserGroupUser::setUserGroup,
          ChildrenMap.matchNested(UserGroupUser::getUserGroup, UserGroup::getUserGroupId))
      .build();
  private final ApplicationEventPublisher eventPublisher;
  private final UserGroupExtendedMapper extendedMapper;

  public UserGroupService(UserGroupDbMapper groupMapper, UserGroupUserDbMapper userMapper,
      ApplicationEventPublisher eventPublisher,
      UserGroupExtendedMapper extendedMapper) {
    this.eventPublisher = eventPublisher;
    this.extendedMapper = extendedMapper;
    this.dao = new Dao<>(groupMapper, UserGroupDb::getUserGroupId);
    this.userDao = new DaoWithoutPrimaryKey<>(userMapper);
  }

  public UserGroupDb create(String description, AppUser user) {
    UserGroupDb group = new UserGroupDb();
    group.setDescription(description);
    group.setActiveFlg(true);
    dao.insert(group, user);

    eventPublisher.publishEvent(new UserGroupsChangedEvent(this));

    return group;
  }

  public List<UserGroup> getActiveUserGroups() {
    UserGroupDbExample active = new UserGroupDbExample();
    active.createCriteria().andActiveFlgEqualTo(true);
    return provider.mapAll(dao.select(active));
  }

  public List<UserGroupUser> getGroupsForUsers(List<Integer> userIds) {
    if (userIds.isEmpty()) {
      return Collections.emptyList();
    }
    UserGroupUserDbExample byUserIds = new UserGroupUserDbExample();
    byUserIds.createCriteria().andUserIdIn(userIds);
    var userDbs = userDao.select(byUserIds);

    var groupIds = userDbs.stream().map(UserGroupUserDb::getUserGroupId).distinct().toList();
    UserGroupDbExample byGroupIds = new UserGroupDbExample();
    byGroupIds.createCriteria().andUserGroupIdIn(groupIds);
    var groupDbs = groupIds.isEmpty() ? Collections.emptyList() : dao.select(byGroupIds);

    return userProvider.mapAll(userDbs, groupDbs);
  }

  public void saveGroupsForUser(Integer userId, List<UserGroup> groups, AppUser user) {
    UserGroupUserDbExample byUserId = new UserGroupUserDbExample();
    byUserId.createCriteria().andUserIdEqualTo(userId);
    userDao.delete(byUserId);

    groups.forEach(group -> {
      UserGroupUserDb userGroup = new UserGroupUserDb();
      userGroup.setUserId(userId);
      if (group.getUserGroupId() == null) {
        // If the group doesn't exist yet, create a new group
        var newGroup = create(group.getDescription(), user);
        userGroup.setUserGroupId(newGroup.getUserGroupId());
      } else {
        userGroup.setUserGroupId(group.getUserGroupId());
      }
      userDao.insert(userGroup, user);
    });

    extendedMapper.updateActiveFlgs();
    eventPublisher.publishEvent(new UserGroupsChangedEvent(this));
  }
}
