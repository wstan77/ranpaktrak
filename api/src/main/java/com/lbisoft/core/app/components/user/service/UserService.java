package com.lbisoft.core.app.components.user.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.user.event.UserBeforeDeleteEvent;
import com.lbisoft.core.app.components.user.event.UserBeforeDisableEvent;
import com.lbisoft.core.app.components.user.event.UserSavedEvent;
import com.lbisoft.core.app.components.user.exception.UserAlreadyExistsException;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.app.components.user.provider.UserInfoProvider;
import com.lbisoft.core.app.components.user.provider.UserProvider;
import com.lbisoft.core.app.components.user.validator.UserValidator;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.app.db.mapper.custom.UserPermissionExtendedMapper;
import com.lbisoft.core.app.db.mapper.generated.UserDbMapper;
import com.lbisoft.core.app.db.model.generated.UserDb;
import com.lbisoft.core.app.db.model.generated.UserDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.LoggerHelper;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;

@CoreService
public class UserService {
  // TODO: Cache users

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  private final UserProvider provider;
  private final UserInfoProvider userInfoProvider;
  private final Dao<UserDb, Integer> dao;
  private final UserPermissionsService permissionsService;
  private final UserValidator validator;
  private final UserRoleService userRoleService;
  private final UserSubordinateService subordinateService;
  private final UserGroupService userGroupService;
  private final UserPermissionExtendedMapper extendedMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final UserDbMapper mapper;
  private final UserActivitiesService activitiesService;

  @Autowired
  public UserService(UserDbMapper mapper, UserProvider provider, UserInfoProvider userInfoProvider,
      UserPermissionsService permissionsService, UserRoleService userRoleService,
      UserSubordinateService subordinateService, UserPermissionExtendedMapper extendedMapper,
      ApplicationEventPublisher eventPublisher, UserGroupService userGroupService,
      UserActivitiesService activitiesService) {

    this.provider = provider;
    this.userInfoProvider = userInfoProvider;
    this.permissionsService = permissionsService;
    this.userRoleService = userRoleService;
    this.subordinateService = subordinateService;
    this.userGroupService = userGroupService;
    this.extendedMapper = extendedMapper;
    this.eventPublisher = eventPublisher;
    this.mapper = mapper;
    this.activitiesService = activitiesService;

    dao = Dao.getDao(mapper, UserDb::getUserId);
    this.validator = new UserValidator();
  }

  @Transactional(readOnly = true)
  public Optional<AppUser> getUserByUsername(final String username) {
    final UserDbExample byUsername = new UserDbExample();
    byUsername.createCriteria().andUsernameEqualTo(username).andRecordStatusLkEqualTo(RecordStatusLks.APPROVED);

    return dao.selectOne(byUsername).flatMap(this::populateUser);
  }

  @Transactional(readOnly = true)
  public Optional<AppUser> getUserById(Integer userId) {
    final UserDbExample byUserId = new UserDbExample();
    byUserId.createCriteria().andUserIdEqualTo(userId).andRecordStatusLkNotEqualTo(RecordStatusLks.DELETED);
    return dao.selectOne(byUserId).flatMap(this::populateUser);
  }

  @Transactional(readOnly = true)
  public UserInfo getUserInfoById(Integer userId) {
    final UserDbExample byUserId = new UserDbExample();
    byUserId.createCriteria().andUserIdEqualTo(userId).andRecordStatusLkNotEqualTo(RecordStatusLks.DELETED);
    return dao.selectOne(byUserId).map(userInfoProvider::map).orElse(null);
  }

  @Transactional(readOnly = true)
  public List<AppUser> getAllUsers() {
    UserDbExample allRealUsers = new UserDbExample();
    allRealUsers.createCriteria().andUserIdNotEqualTo(Constants.SYSTEM_USER_ID)
        .andRecordStatusLkNotEqualTo(RecordStatusLks.DELETED);
    List<UserDb> userDbs = dao.select(allRealUsers);
    return populateUsers(userDbs);
  }

  @Transactional(readOnly = true)
  public List<UserInfo> getActiveUserInfos() {
    UserDbExample allRealUsers = new UserDbExample();
    allRealUsers.createCriteria().andUserIdNotEqualTo(Constants.SYSTEM_USER_ID)
        .andRecordStatusLkEqualTo(RecordStatusLks.APPROVED);
    allRealUsers.setOrderByClause("LAST_NAME asc, FIRST_NAME asc");
    List<UserDb> userDbs = dao.select(allRealUsers);
    return userInfoProvider.mapAll(userDbs);
  }

  @Transactional(readOnly = true)
  public List<UserInfo> getUserInfos() {
    UserDbExample allRealUsers = new UserDbExample();
    allRealUsers.createCriteria().andUserIdNotEqualTo(Constants.SYSTEM_USER_ID);
    allRealUsers.setOrderByClause("LAST_NAME asc, FIRST_NAME asc");
    List<UserDb> userDbs = dao.select(allRealUsers);
    return userInfoProvider.mapAll(userDbs).stream().map(user -> {
      if (RecordStatusLks.DELETED.equals(user.getRecordStatusLk())) {
        user.setFirstName(Constants.UNKNOWN_USER.getFirstName());
        user.setLastName(Constants.UNKNOWN_USER.getLastName());
      } else if (RecordStatusLks.INACTIVE.equals(user.getRecordStatusLk())) {
        user.setLastName(user.getLastName() + " (Deactivated)");
      }
      return user;
    }).toList();
  }

  public int setLastLoginDate(AppUser user) {
    UserDbExample byUserName = new UserDbExample();
    byUserName.createCriteria().andUsernameEqualTo(user.getUsername());
    List<UserDb> dbs = mapper.selectByExample(byUserName);
    if (dbs.get(0) != null) {
      UserDb db = new UserDb().withUserId(dbs.get(0).getUserId());
      db.setLastLoginDate(LocalDateTime.now());
      return mapper.updateByPrimaryKeyModified(db);
    }
    return 0;
  }

  Optional<AppUser> populateUser(UserDb userDb) {
    return populateUsers(List.of(userDb)).stream().findAny();
  }

  List<AppUser> populateUsers(List<UserDb> userDbs) {
    List<Integer> userIds = userDbs.stream().map(UserDb::getUserId).toList();

    return provider.mapParallel(userDbs,
        () -> subordinateService.getSubordinateUserIdsForUserIds(userIds),
        () -> userIds.size() == 1 ? List.of(permissionsService.getUsersPermissions(userIds.get(0))) : null,
        () -> extendedMapper.getRolesForUsers(userIds),
        () -> userGroupService.getGroupsForUsers(userIds));
  }

  public AppUser createAnonymousUser() {
    String uuid = UUID.randomUUID().toString();

    AppUser user = new AppUser();
    user.setUsername(uuid);
    user.setFirstName("Anonymous");
    user.setLastName("User");
    user.setRecordStatusLk(RecordStatusLks.APPROVED);
    user.setAnonymousFlg(true);

    try {
      return createUser(user, null);
    } catch (UserAlreadyExistsException e) {
      // Extremely unlikely that there will be a collision, but just in case
      // we will try again
      return createAnonymousUser();
    }
  }

  public ResponseObject saveUser(AppUser user, HttpServletRequest request) {
    AppUser currentUser = RequestHelper.getUser(request);

    boolean isNew = (user.getUserId() == null);
    // validate user
    validator.forCreate(user).validate();

    // save user
    user.setRecordStatusLk(RecordStatusLks.APPROVED);
    if (isNew) {
      user = createUser(user, request);
    } else {
      LoggerHelper.userIs("updating a user " + user.getEmailAddress(), LOGGER, currentUser);
      dao.update(provider.reverseMap(user), currentUser);
    }

    // save roles
    userRoleService.updateRolesForUser(user.getUserId(), user.getUserRoles(), currentUser);

    // save subordinates
    subordinateService.updateSubordinatesForUser(user.getUserId(), user.getSubordinateIds(), currentUser);

    // save groups
    userGroupService.saveGroupsForUser(user.getUserId(), user.getUserGroups(), currentUser);

    // add success messsage
    if (isNew) {
      user.addResponseInfo("Created an account for " + user.getFullName());
    } else {
      user.addResponseInfo(user.getFullName() + " has been saved");
    }

    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.SAVE_USER).relatedObject(user).build(),
        request);
    eventPublisher.publishEvent(new UserSavedEvent(this, user.getUserId()));

    return getUserById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User is missing"));
  }

  public AppUser createUser(final AppUser createUser, HttpServletRequest request) {
    AppUser currentUser = request != null ? RequestHelper.getUser(request) : Constants.SYSTEM_USER;

    LoggerHelper.userIs("creating a user " + createUser.getEmailAddress(), LOGGER, currentUser);

    // ensure user does not exist with username
    final UserDbExample example = new UserDbExample();
    example.createCriteria().andUsernameEqualTo(createUser.getUsername());
    final List<UserDb> sameUsername = dao.select(example);
    if (!sameUsername.isEmpty()) {
      throw new UserAlreadyExistsException("User already exists with username: " + createUser.getUsername());
    }
    UserDb db = provider.reverseMap(createUser);
    // save user
    dao.insert(db, currentUser);

    // get user id from inserted record
    createUser.setUserId(db.getUserId());

    if (request != null) {
      activitiesService.logActivity(
          new UserActivityBuilder(ActivityTypes.CREATE_USER).relatedObject(createUser).build(),
          request);
    }
    return createUser;
  }

  public Optional<AppUser> disableUser(Integer userId, AppUser currentUser) {
    return getUserById(userId).map(user -> {
      if (user.isActive()) {
        eventPublisher.publishEvent(new UserBeforeDisableEvent(this, user.getUserId()));

        user.setRecordStatusLk(RecordStatusLks.INACTIVE);
        dao.update(provider.reverseMap(user), currentUser);
        userRoleService.removeUserFromAllRoles(userId, currentUser);
        userGroupService.saveGroupsForUser(userId, Collections.emptyList(), currentUser);
        return user;
      }
      return null;
    });
  }

  public Optional<AppUser> enableUser(Integer userId, AppUser currentUser) {
    return getUserById(userId).map(user -> {
      if (!user.isActive()) {
        user.setRecordStatusLk(RecordStatusLks.APPROVED);
        dao.update(provider.reverseMap(user), currentUser);
        eventPublisher.publishEvent(new UserSavedEvent(this, user.getUserId()));
        return user;
      }
      return user;
    });
  }

  public boolean deleteUser(Integer userId, HttpServletRequest request) {
    AppUser currentUser = RequestHelper.getUser(request);

    return getUserById(userId).map(user -> {
      user.setRecordStatusLk(RecordStatusLks.DELETED);
      eventPublisher.publishEvent(new UserBeforeDeleteEvent(this, user.getUserId()));
      subordinateService.removeSubordinateFromAllUsers(userId, currentUser);
      dao.update(provider.reverseMap(user), currentUser);
      eventPublisher.publishEvent(new UserSavedEvent(this, user.getUserId()));
      activitiesService.logActivityById(ActivityTypes.DELETE_USER, userId, request);
      return true;
    }).orElse(false);
  }

}
