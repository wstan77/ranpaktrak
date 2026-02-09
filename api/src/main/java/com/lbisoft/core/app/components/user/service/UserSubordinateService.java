package com.lbisoft.core.app.components.user.service;

import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.UserSubordinateDbMapper;
import com.lbisoft.core.app.db.model.generated.UserSubordinateDb;
import com.lbisoft.core.app.db.model.generated.UserSubordinateDbExample;
import com.lbisoft.core.app.db.model.generated.UserSubordinateDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class UserSubordinateService {

  private final Dao<UserSubordinateDb, UserSubordinateDbKey> dao;

  public UserSubordinateService(UserSubordinateDbMapper mapper) {
    this.dao = Dao.getDao(mapper, s -> s);
  }

  @Transactional(readOnly = true)
  public List<UserSubordinateDb> getSubordinateUserIdsForUserIds(List<Integer> userIds) {
    if (userIds == null || userIds.isEmpty() || userIds.size() >= 2000) {
      return Collections.emptyList();
    }
    UserSubordinateDbExample byUserId = new UserSubordinateDbExample();
    byUserId.createCriteria().andUserIdIn(userIds);

    return dao.select(byUserId);
  }

  public void updateSubordinatesForUser(Integer userId, List<Integer> subordinateIds, AppUser currentUser) {
    // Remove old subordinates
    UserSubordinateDbExample byUserId = new UserSubordinateDbExample();
    byUserId.createCriteria().andUserIdEqualTo(userId);
    dao.delete(byUserId);
    // Insert new list
    if (subordinateIds != null) {
      List<UserSubordinateDb> dbs = subordinateIds.stream().map(id -> {
        UserSubordinateDb db = new UserSubordinateDb();
        db.setUserId(userId);
        db.setSubordinateUserId(id);
        return db;
      }).toList();

      dao.insert(dbs, currentUser);
    }
  }

  public void removeSubordinateFromAllUsers(Integer subUserId, AppUser currentUser) {
    UserSubordinateDbExample byUserId = new UserSubordinateDbExample();
    byUserId.createCriteria().andSubordinateUserIdEqualTo(subUserId);
    dao.delete(byUserId);
  }
}
