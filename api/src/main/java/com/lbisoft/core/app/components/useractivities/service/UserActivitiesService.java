package com.lbisoft.core.app.components.useractivities.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbisoft.core.app.components.useractivities.provider.UserActivityProvider;
import com.lbisoft.core.app.db.mapper.generated.UserActivityDbMapper;
import com.lbisoft.core.app.db.model.generated.UserActivityDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.user.HasUserId;
import com.lbisoft.core.framework.useractivities.AbstractUserActivityService;
import com.lbisoft.core.framework.useractivities.ActivityType;
import com.lbisoft.core.framework.useractivities.UserActivity;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The UserActivitiesService class provides logic for logging user activity
 * within the application
 */
@CoreService
public class UserActivitiesService extends AbstractUserActivityService {
  private final Dao<UserActivityDb, Integer> dao;
  private final UserActivityProvider provider;
  private final ObjectMapper objectMapper;

  /**
   * Class constructor
   *
   * @param mapper       database mapper for {@link UserActivityDb}
   * @param provider     maps EeUserActivityDb to and from {@link UserActivity}
   *                     objects
   * @param objectMapper jackson object mapper
   */
  @Autowired
  public UserActivitiesService(
      UserActivityDbMapper mapper,
      UserActivityProvider provider,
      ObjectMapper objectMapper) {
    this.dao = Dao.getDao(mapper, UserActivityDb::getUserActivityId);
    this.provider = provider;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void saveActivity(UserActivity activity, HasUserId user) {
    UserActivityDb activityDb = provider.reverseMap(activity);
    dao.insert(activityDb, user);
  }

  public void logActivityById(ActivityType activityType, Integer relatedId, HttpServletRequest request) {
    logActivity(new UserActivityBuilder(activityType).relatedId(relatedId).build(), request);
  }

  @Override
  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

}
