package com.lbisoft.core.app.components.lookup.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.lbisoft.core.app.components.lookup.model.Lookup;
import com.lbisoft.core.app.components.systemdates.service.SystemDateLookupService;
import com.lbisoft.core.app.components.systemvalues.model.SystemValue;
import com.lbisoft.core.app.components.systemvalues.service.SystemValueGetService;
import com.lbisoft.core.app.components.user.event.UserBeforeDeleteEvent;
import com.lbisoft.core.app.components.user.event.UserSavedEvent;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.app.components.user.service.UserService;
import com.lbisoft.core.app.db.mapper.generated.*;
import com.lbisoft.core.app.db.model.generated.*;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.dao.db.DbExample;
import com.lbisoft.core.framework.dao.db.DbModel;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.utils.ClassHelper;
import com.lbisoft.core.framework.utils.StringHelper;

@CoreService
@Transactional(readOnly = true)
public class Lookups {
  private static final double NANOSECONDS_PER_SECOND = 1000000000.0;
  private static final String NOT_FOUND_ERROR = "Could not find lookup: ";

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  // Lookup Lists
  private Map<Class<?>, List<?>> lookupListByClass;
  private Map<String, List<?>> lookupListByName;

  // Lookup Tuples
  private Map<Class<?>, List<Lookup<?, ?>>> lookupTuplesByClass;
  private Map<String, List<Lookup<?, ?>>> lookupTuplesByName;

  // Lookup Maps
  private Map<Class<?>, Map<?, ?>> lookupMapByClass;
  private Map<String, Map<?, ?>> lookupMapByName;

  // Mappers
  @Autowired
  private UserService userService;
  @Autowired
  private LkReferenceTypeDbMapper referenceTypeMapper;
  @Autowired
  private LkNoteTypeDbMapper noteTypeMapper;
  @Autowired
  private LkIssueTypeDbMapper issueTypeMapper;
  @Autowired
  private LkPermissionDbMapper permissionMapper;
  @Autowired
  private LkPermissionLevelDbMapper permissionLevelMapper;
  @Autowired
  private LkPermissionTargetDbMapper permissionTargetMapper;
  @Autowired
  private RoleDbMapper roleMapper;
  @Autowired
  private LkLanguageDbMapper languageMapper;
  @Autowired
  private LkGenderDbMapper genderMapper;
  @Autowired
  private LkEmailTypeDbMapper emailTypeMapper;
  @Autowired
  private LkPhoneTypeDbMapper phoneTypeMapper;
  @Autowired
  private LkCountryDbMapper countryMapper;
  @Autowired
  private LkStateDbMapper stateMapper;
  @Autowired
  private LkSourceSystemDbMapper sourceSystemMapper;
  @Autowired
  private LkAddressTypeDbMapper addressTypeMapper;
  @Autowired
  private LkSocialProfileDbMapper socialProfileMapper;
  @Autowired
  private LkPersonTypeDbMapper personTypeMapper;
  @Autowired
  private LkSystemDateTypeDbMapper systemDateTypeMapper;
  @Autowired
  private LkTaskStatusDbMapper taskStatusMapper;
  @Autowired
  private LkJobTypeDbMapper jobTypeMapper;
  @Autowired
  private LkMachineTypeDbMapper machineTypeMapper;
  @Autowired
  private LkRegionDbMapper regionMapper;
  @Autowired
  private LkScanResultTypeDbMapper scanResultTypeMapper;
  @Autowired
  private LkScanTypeDbMapper scanTypeMapper;

  // Services
  @Autowired
  private SystemValueGetService systemValueService;
  @Autowired
  private SystemDateLookupService systemDateLookupService;

  public void loadLookups() {
    final long startTime = System.nanoTime();
    init();

    // Dao lookups

    addLookupByDao(LkReferenceTypeDb.class,
        Dao.getDao(referenceTypeMapper, LkReferenceTypeDb::getReferenceTypeLk));
    addLookupByDao(LkNoteTypeDb.class, Dao.getDao(noteTypeMapper, LkNoteTypeDb::getNoteTypeLk));
    addLookupByDao(LkIssueTypeDb.class, Dao.getDao(issueTypeMapper, LkIssueTypeDb::getIssueTypeLk));
    addLookupByDao(LkPermissionLevelDb.class,
        Dao.getDao(permissionLevelMapper, LkPermissionLevelDb::getPermissionLevelLk));
    addLookupByDao(LkPermissionTargetDb.class,
        Dao.getDao(permissionTargetMapper, LkPermissionTargetDb::getPermissionTargetLk));
    addLookupByDao(LkPermissionDb.class, Dao.getDao(permissionMapper, LkPermissionDb::getPermissionLk));
    addLookupByDao(LkLanguageDb.class, Dao.getDao(languageMapper, LkLanguageDb::getLanguageLk));
    addLookupByDao(LkGenderDb.class, Dao.getDao(genderMapper, LkGenderDb::getGenderLk));
    addLookupByDao(LkEmailTypeDb.class, Dao.getDao(emailTypeMapper, LkEmailTypeDb::getEmailTypeLk));
    addLookupByDao(LkPhoneTypeDb.class, Dao.getDao(phoneTypeMapper, LkPhoneTypeDb::getPhoneTypeLk));
    addLookupByDao(LkCountryDb.class, Dao.getDao(countryMapper, LkCountryDb::getCountryLk));
    addLookupByDao(LkStateDb.class, Dao.getDao(stateMapper, LkStateDb::getStateLk));
    addLookupByDao(LkSourceSystemDb.class, Dao.getDao(sourceSystemMapper, LkSourceSystemDb::getSourceSystemLk));
    addLookupByDao(LkAddressTypeDb.class, Dao.getDao(addressTypeMapper, LkAddressTypeDb::getAddressTypeLk));
    addLookupByDao(LkSocialProfileDb.class, Dao.getDao(socialProfileMapper, LkSocialProfileDb::getSocialProfileLk));
    addLookupByDao(LkPersonTypeDb.class, Dao.getDao(personTypeMapper, LkPersonTypeDb::getPersonTypeLk));
    addLookupByDao(LkSystemDateTypeDb.class,
        Dao.getDao(systemDateTypeMapper, LkSystemDateTypeDb::getSystemDateTypeLk));
    addLookupByDao(LkTaskStatusDb.class, Dao.getDao(taskStatusMapper, LkTaskStatusDb::getTaskStatusLk));
    addLookupByDao(LkJobTypeDb.class, Dao.getDao(jobTypeMapper, LkJobTypeDb::getJobTypeLk));
    addLookupByDao(LkMachineTypeDb.class, Dao.getDao(machineTypeMapper, LkMachineTypeDb::getMachineTypeLk));
    addLookupByDao(LkRegionDb.class, Dao.getDao(regionMapper, LkRegionDb::getRegionLk));
    addLookupByDao(LkScanResultTypeDb.class, Dao.getDao(scanResultTypeMapper, LkScanResultTypeDb::getScanResultTypeLk));
    addLookupByDao(LkScanTypeDb.class, Dao.getDao(scanTypeMapper, LkScanTypeDb::getScanTypeLk));

    // Lookups that change during app lifetime
    loadRoles();
    loadUsers();
    addLookup("systemValues", systemValueService.getSystemValues(), SystemValue::getSystemValueKey);
    addLookup("systemDates", systemDateLookupService.getAllSystemDates(), SystemDateDb::getSystemDateTypeLk);
    addLookup("systemDatesCurrent", systemDateLookupService.getCurrentSystemDates(),
        SystemDateDb::getSystemDateTypeLk);

    LOGGER.info(String.format("Finished loading lookups in %.3f seconds",
        (System.nanoTime() - startTime) / NANOSECONDS_PER_SECOND));
  }

  public void loadRoles() {
    addLookupByDao(RoleDb.class, Dao.getDao(roleMapper, RoleDb::getRoleId));
  }

  @Async
  public Future<List<UserInfo>> loadUsers() {
    addLookup(UserInfo.class, userService.getUserInfos(), UserInfo::getUserId);
    return CompletableFuture.completedFuture(getListByClass(UserInfo.class));
  }

  @Async
  @TransactionalEventListener(fallbackExecution = true)
  public void onUserSaved(UserSavedEvent event) {
    loadUsers();
  }

  @Async
  @EventListener
  public void onUserDeleted(UserBeforeDeleteEvent event) {
    removeLookupItem(UserInfo.class, event.getUserId());
  }

  private void init() {
    lookupListByClass = new HashMap<>();
    lookupListByName = new HashMap<>();
    lookupTuplesByClass = new HashMap<>();
    lookupTuplesByName = new HashMap<>();
    lookupMapByClass = new HashMap<>();
    lookupMapByName = new HashMap<>();
  }

  private String removeDbAtEnd(final String name) {
    return name.endsWith("Db") ? name.substring(0, name.length() - 2) : name;
  }

  @SuppressWarnings("unused")
  private <M, K, E> void addLookupMap(final String lookupName, final Map<M, E> map) {
    lookupMapByName.put(lookupName.toLowerCase(), map);
  }

  private <M, K> void addLookup(final Class<M> modelClass, final List<M> list,
      final Function<M, K> getPrimaryKey) {
    addLookup(null, modelClass, list, getPrimaryKey);
  }

  private <M, K> void addLookup(final String lookupName, final List<M> list, final Function<M, K> getPrimaryKey) {
    addLookup(lookupName, null, list, getPrimaryKey);
  }

  private <M, K> void addLookup(final String lookupName, final Class<M> modelClass, final List<M> list,
      final Function<M, K> getPrimaryKey) {
    final String name = (lookupName == null) ? removeDbAtEnd(modelClass.getSimpleName()) : lookupName;

    lookupListByName.remove(name.toLowerCase());
    lookupTuplesByName.remove(name.toLowerCase());
    lookupMapByName.remove(name.toLowerCase());

    final List<Lookup<?, ?>> tuples = list.stream().map(item -> new Lookup<>(getPrimaryKey.apply(item), item))
        .collect(Collectors.toList());
    final Map<K, M> map = list.stream()
        .collect(Collectors.toMap(getPrimaryKey, item -> item, (a, b) -> a, TreeMap::new));

    lookupListByName.put(name.toLowerCase(), list);
    lookupTuplesByName.put(name.toLowerCase(), tuples);
    lookupMapByName.put(name.toLowerCase(), map);
    if (modelClass != null) {
      lookupListByClass.put(modelClass, list);
      lookupTuplesByClass.put(modelClass, tuples);
      lookupMapByClass.put(modelClass, map);
    }
  }

  private <M, K> void removeLookupItem(String lookupName, Class<M> modelClass, K primaryKey) {
    String name = ((lookupName == null) ? removeDbAtEnd(modelClass.getSimpleName()) : lookupName).toLowerCase();

    lookupTuplesByName.get(name).stream().filter(tuple -> tuple.getPrimaryKey().equals(primaryKey)).findAny()
        .ifPresent(toRemove -> {
          // It's safe to use != on two objects here because all of the lists point to the
          // same
          // reference
          lookupListByName.computeIfPresent(name,
              (key, list) -> list.stream().filter(item -> item != toRemove).toList());
          lookupTuplesByName.computeIfPresent(name,
              (key, list) -> list.stream().filter(item -> !item.getPrimaryKey().equals(primaryKey)).toList());
          lookupMapByName.get(name).remove(primaryKey);
          if (modelClass != null) {
            lookupListByClass.computeIfPresent(modelClass,
                (key, list) -> list.stream().filter(item -> item != toRemove).toList());
            lookupTuplesByClass.computeIfPresent(modelClass,
                (key, list) -> list.stream().filter(item -> !item.getPrimaryKey().equals(primaryKey)).toList());
            lookupMapByClass.get(modelClass).remove(primaryKey);
          }
        });
  }

  private <M, K2> void removeLookupItem(Class<M> modelClass, K2 primaryKey) {
    removeLookupItem(null, modelClass, primaryKey);
  }

  private <M extends DbModel, K2> void addLookupByDao(final Class<M> modelClass, final Dao<M, K2> dao) {
    addLookupByDao(null, modelClass, dao);
  }

  @SuppressWarnings("unchecked")
  private <M extends DbModel, K2> void addLookupByDao(final String lookupName, final Class<M> modelClass,
      final Dao<M, K2> dao) {
    try {
      final DbExample<M> example = (DbExample<M>) modelClass.getDeclaredConstructor().newInstance()
          .createExample();
      String orderByClause = "";
      if (ClassHelper.classHasGetter(modelClass, "seqNo")) {
        orderByClause += "SEQ_NO";
      }
      if (ClassHelper.classHasGetter(modelClass, "description")) {
        orderByClause += ("".equals(orderByClause) ? "DESCRIPTION" : ", DESCRIPTION");
      }
      if (!StringHelper.isEmpty(orderByClause)) {
        example.setOrderByClause(orderByClause);
      }

      final List<M> list = dao.select(example);

      addLookup(lookupName, modelClass, list, dao::getPrimaryKey);

    } catch (IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException
        | NoSuchMethodException | SecurityException e) {
      LOGGER.error("Failed to load lookup", e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getList(final String lookupName) {
    final String name = lookupName.toLowerCase();
    if (lookupListByName.containsKey(name)) {
      return (List<T>) lookupListByName.get(name);
    } else {
      LOGGER.error(NOT_FOUND_ERROR + name);
      return Collections.emptyList();
    }
  }

  public <T> List<T> getList(final String lookupName, final Class<T> lookupClass) {
    return getList(lookupName);
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getListByClass(final Class<T> lookupClass) {
    if (lookupListByClass.containsKey(lookupClass)) {
      return (List<T>) lookupListByClass.get(lookupClass);
    } else {
      LOGGER.error(NOT_FOUND_ERROR + lookupClass.getCanonicalName());
      return Collections.emptyList();
    }
  }

  @SuppressWarnings("unchecked")
  public <K2, V2> List<Lookup<K2, V2>> getTuples(final String lookupName) {
    final String name = lookupName.toLowerCase();

    if (lookupTuplesByName.containsKey(name)) {
      return lookupTuplesByName.get(name).stream().map(o -> (Lookup<K2, V2>) o).toList();
    } else {
      LOGGER.error(NOT_FOUND_ERROR + name);
      return Collections.emptyList();
    }
  }

  @SuppressWarnings("unchecked")
  public <K2, V2> List<Lookup<K2, V2>> getTuplesByClass(final Class<V2> lookupClass) {
    if (lookupTuplesByClass.containsKey(lookupClass)) {
      return lookupTuplesByClass.get(lookupClass).stream().map(o -> (Lookup<K2, V2>) o).toList();
    } else {
      LOGGER.error(NOT_FOUND_ERROR + lookupClass.getCanonicalName());
      return Collections.emptyList();
    }
  }

  @SuppressWarnings("unchecked")
  public <K, V> Map<K, V> getLookupsByPrimaryKey(final String lookupName) {
    final String name = lookupName.toLowerCase();
    if (lookupMapByName.containsKey(name)) {
      return (Map<K, V>) lookupMapByName.get(name);
    } else {
      LOGGER.error(NOT_FOUND_ERROR + name);
      return new HashMap<K, V>();
    }
  }

  @SuppressWarnings("unchecked")
  public <K, V> Map<K, V> getLookupsByPrimaryKey(final Class<V> lookupClass) {
    if (lookupMapByClass.containsKey(lookupClass)) {
      return (Map<K, V>) lookupMapByClass.get(lookupClass);
    } else {
      LOGGER.error(NOT_FOUND_ERROR + lookupClass.getCanonicalName());
      return new HashMap<K, V>();
    }
  }

  public <K, V> V getItem(final Class<V> lookupClass, final K primaryKey) {
    return primaryKey == null ? null : getLookupsByPrimaryKey(lookupClass).get(primaryKey);
  }

  public Object getItem(final String lookupName, final String primaryKey) {
    return primaryKey == null ? null : getLookupsByPrimaryKey(lookupName).get(primaryKey);
  }

  public Object getItem(final String lookupName, final Integer primaryKey) {
    return primaryKey == null ? null : getLookupsByPrimaryKey(lookupName).get(primaryKey);
  }

  public ResponseObject reloadAllLookups(AppUser user) {
    try {
      loadLookups();
      return new ResponseObject().addResponseInfo("Successfully reloaded lookups.");
    } catch (Exception e) {
      LOGGER.error("something went wrong loading lookups...", e);
      throw (e);
    }
  }
}
