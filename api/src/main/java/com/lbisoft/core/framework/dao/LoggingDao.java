package com.lbisoft.core.framework.dao;

import java.util.List;
import java.util.function.Function;

import jakarta.servlet.http.HttpServletRequest;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.dao.db.DbExample;
import com.lbisoft.core.framework.dao.db.DbMapper;
import com.lbisoft.core.framework.dao.db.DbModel;
import com.lbisoft.core.framework.exceptions.MethodNotAllowedException;
import com.lbisoft.core.framework.user.HasUserId;
import com.lbisoft.core.framework.useractivities.ActivityType;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

public class LoggingDao<M extends DbModel, K> extends Dao<M, K> {
  private final UserActivitiesService activitiesService;
  protected ActivityType insertActivityType;
  protected ActivityType updateActivityType;
  protected ActivityType deleteActivityType;

  public LoggingDao(DbMapper<M, K> mapper, Function<M, K> primaryKeyFn, UserActivitiesService activitiesService) {
    super(mapper, primaryKeyFn);
    this.activitiesService = activitiesService;
  }

  @Override
  @Deprecated
  public int insert(M model, HasUserId createUser) {
    throw new MethodNotAllowedException("Insert by user not allowed");
  }

  public int insert(M model, HttpServletRequest request) {
    AppUser createUser = RequestHelper.getUser(request);

    int retVal = super.insert(model, createUser);

    UserActivityBuilder activity = generateActivity(model, insertActivityType);

    activitiesService.logActivity(activity.build(), request);

    return retVal;
  }

  @Override
  @Deprecated
  public int insert(List<? extends M> models, HasUserId createUser) {
    throw new MethodNotAllowedException("Insert by user not allowed");
  }

  public int insert(List<? extends M> models, HttpServletRequest request) {
    return models.stream().mapToInt(model -> insert(model, request)).sum();
  }

  @Override
  @Deprecated
  public int update(M model, DbExample<M> example, HasUserId updateUser) {
    throw new MethodNotAllowedException("Update by user not allowed");
  }

  public void update(M model, HttpServletRequest request) {
    AppUser updateUser = RequestHelper.getUser(request);

    super.update(model, updateUser);

    UserActivityBuilder activity = generateActivity(model, updateActivityType);

    activitiesService.logActivity(activity.build(), request);
  }

  public void updateAndLogAsDelete(M model, HttpServletRequest request) {
    AppUser updateUser = RequestHelper.getUser(request);

    super.update(model, updateUser);

    UserActivityBuilder activity = generateActivity(model, deleteActivityType);

    activitiesService.logActivity(activity.build(), request);
  }

  public void updateAndLogAsDelete(List<? extends M> models, HttpServletRequest request) {
    models.forEach(model -> updateAndLogAsDelete(model, request));
  }

  @Override
  @Deprecated
  public int delete(M model) {
    throw new UnsupportedOperationException();
  }

  public int delete(M model, HttpServletRequest request) {
    UserActivityBuilder activity = generateActivity(model, deleteActivityType);

    activitiesService.logActivity(activity.build(), request);

    return super.delete(model);
  }

  protected UserActivityBuilder generateActivity(M model, ActivityType activityType) {
    K key = getPrimaryKey(model);
    UserActivityBuilder activity = new UserActivityBuilder(activityType);
    if (key instanceof Integer) {
      activity.relatedId((Integer) key);
    } else if (key instanceof String) {
      activity.relatedString((String) key);
    }
    activity.relatedObject(model);
    return activity;
  }

  public void setInsertActivityType(ActivityType activityType) {
    this.insertActivityType = activityType;
  }

  public void setDeleteActivityType(ActivityType activityType) {
    this.deleteActivityType = activityType;
  }

  public void setUpdateActivityType(ActivityType activityType) {
    this.updateActivityType = activityType;
  }

  public static <M extends DbModel, K> LoggingDao<M, K> getLoggingDao(DbMapper<M, K> mapper,
      Function<M, K> primaryKeyFn, UserActivitiesService activitiesService) {
    return new LoggingDao<M, K>(mapper, primaryKeyFn, activitiesService);
  }
}
