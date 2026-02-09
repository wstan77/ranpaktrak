package com.lbisoft.core.framework.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lbisoft.core.framework.dao.db.DbMapper;
import com.lbisoft.core.framework.dao.db.DbMapperWithoutPrimaryKey;
import com.lbisoft.core.framework.dao.db.DbModel;
import com.lbisoft.core.framework.exceptions.ModifiedSinceLoadedException;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.user.HasUserId;

public class Dao<M extends DbModel, K> extends DaoWithoutPrimaryKey<M> {

  private final DbMapper<M, K> mapper;
  Function<M, K> primaryKeyFn;

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  public Dao(DbMapper<M, K> mapper, Function<M, K> primaryKeyFn) {
    super(mapper);
    this.mapper = mapper;
    this.primaryKeyFn = primaryKeyFn;
  }

  public Optional<M> select(K key) {
    return Optional.ofNullable(mapper.selectByPrimaryKey(key));
  }

  /**
   * Update the model in the database based on primary key
   *
   * <p>
   * If the primary key isn't found in the database then throws
   * {@link ResourceNotFoundException}
   *
   * <p>
   * If the model has been updated since it was fetched then throws
   * {@link ModifiedSinceLoadedException} (checks fetchDate, with fallback to
   * systemLastChangeDate
   * and lastChangDate if null)
   *
   * @param model      model to update
   * @param updateUser user applying update
   */
  public void update(M model, HasUserId updateUser) {
    setAuditingInfo(model, updateUser);
    int updateCount = 0;
    if (setModifiedFieldsOnly) {
      updateCount = mapper.updateByPrimaryKeyModified(model);
    } else {
      updateCount = mapper.updateByPrimaryKey(model);
    }
    if (updateCount > 0) {
      Optional<M> updated = select(getPrimaryKey(model));
      model.setLastChangeDate(updated.get().getLastChangeDate());
      model.setLastChangeUserId(updated.get().getLastChangeUserId());
    } else {
      handleFailedUpdate(model);
    }
  }

  /**
   * Updates a list of models in the database based on their primary keys
   *
   * <p>
   * If the primary key isn't found in the database then throws
   * {@link ResourceNotFoundException}
   *
   * <p>
   * If the model has been updated since it was fetched then throws
   * {@link ModifiedSinceLoadedException} (checks fetchDate, with fallback to
   * systemLastChangeDate
   * and lastChangDate if null)
   *
   * @param models     models to update
   * @param updateUser user applying update
   */
  public void update(List<? extends M> models, HasUserId updateUser) {
    models.forEach(model -> update(model, updateUser));
  }

  /**
   * Delete model from database by primary key of the given model
   *
   * @param model The model to delete
   *
   * @return 1 if deleted, 0 if nothing to delete
   */
  public int delete(M model) {
    return delete(getPrimaryKey(model));
  }

  /**
   * Delete model from database by primary key
   *
   * @param key The primary key to delete
   *
   * @return 1 if deleted, 0 if nothing to delete
   */
  public int delete(K key) {
    return mapper.deleteByPrimaryKey(key);
  }

  public int insertOrUpdate(M model, HasUserId updateUser) {
    K key = getPrimaryKey(model);
    if (key == null || !select(key).isPresent()) {
      return insert(model, updateUser);
    }
    update(model, updateUser);
    return 1;
  }

  public int insertOrUpdate(List<? extends M> models, HasUserId updateUser) {
    return models.stream().mapToInt(model -> insertOrUpdate(model, updateUser)).sum();
  }

  public K getPrimaryKey(M model) {
    return primaryKeyFn.apply(model);
  }

  public static <M extends DbModel> DaoWithoutPrimaryKey<M> getDao(
      DbMapperWithoutPrimaryKey<M> mapper) {
    return new DaoWithoutPrimaryKey<M>(mapper);
  }

  public static <M extends DbModel, K> Dao<M, K> getDao(DbMapper<M, K> mapper,
      Function<M, K> primaryKeyFn) {
    return new Dao<M, K>(mapper, primaryKeyFn);
  }

  /**
   * Determine why the update failed:
   * <ul>
   * <li>If the primary key isn't in the database then throw
   * {@link ResourceNotFoundException}</li>
   * <li>Otherwise, assume {@link ModifiedSinceLoadedException}</li>
   * </ul>
   *
   * @param instance instance getting updated
   */
  private void handleFailedUpdate(final M instance) {
    final K primaryKey = getPrimaryKey(instance);
    // check to see if the entity exists, if it does not a not found
    // exception will be generated
    Optional<M> savedInstance = select(primaryKey);
    if (!savedInstance.isPresent()) {
      // update the message on the not found exception to be more
      // explicit (this is an update, not a get)
      LOGGER.error("The " + instance.getClass().getName() + "[primaryKey=" + primaryKey
          + ", fetchDate=" + instance.getFetchDate()
          + "] you are attempting to update was not found in the database");
      throw new RuntimeException("The resource you are attempting to update could not be found.");
    }
    // if the entity does exist in the database, then the fact that no
    // rows are updated means there was a concurrency error
    String basicException = "This page has been updated since it was loaded.";
    StringBuilder fullDiagnostic = new StringBuilder(100);
    if (instance != null) {
      fullDiagnostic.append(", instance fetchDate=")
          .append(getFetchDateString(instance.getFetchDate())).append(", savedInstance fetchDate=")
          .append(getFetchDateString(savedInstance.map(M::getFetchDate).orElse(null)));
    }
    LOGGER.error(basicException + fullDiagnostic.toString());
    throw new ModifiedSinceLoadedException();
  }

  private String getFetchDateString(LocalDateTime fetchDate) {
    return fetchDate == null ? "Unknown time" : fetchDate.toString();
  }
}
