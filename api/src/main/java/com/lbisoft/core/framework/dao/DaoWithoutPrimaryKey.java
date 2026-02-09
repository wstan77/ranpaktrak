package com.lbisoft.core.framework.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.session.RowBounds;

import com.lbisoft.core.framework.dao.db.DbExample;
import com.lbisoft.core.framework.dao.db.DbMapperWithoutPrimaryKey;
import com.lbisoft.core.framework.dao.db.DbModel;
import com.lbisoft.core.framework.user.HasUserId;
import com.lbisoft.core.framework.utils.ObjectHelper;

public class DaoWithoutPrimaryKey<M extends DbModel> {

  private final DbMapperWithoutPrimaryKey<M> mapper;
  protected boolean setModifiedFieldsOnly = true;

  public DaoWithoutPrimaryKey(DbMapperWithoutPrimaryKey<M> mapper) {
    if (mapper == null)
      throw new IllegalArgumentException("Dao cannot be constructed with null mapper");

    this.mapper = mapper;
  }

  public List<M> select(DbExample<M> example) {
    return mapper.selectByExample(example);
  }

  public List<M> select(DbExample<M> example, RowBounds rowBounds) {
    return mapper.selectByExampleWithRowbounds(example, rowBounds);
  }

  public Optional<M> selectOne(DbExample<M> example) {
    return select(example).stream().findFirst();
  }

  public int insert(M model, HasUserId createUser) {
    setAuditingInfo(model, createUser, true);
    if (setModifiedFieldsOnly) {
      return mapper.insertModified(model);
    }
    return mapper.insert(model);
  }

  public int insert(List<? extends M> models, HasUserId createUser) {
    return models.stream().mapToInt(model -> insert(model, createUser)).sum();
  }

  public int update(M model, DbExample<M> example, HasUserId updateUser) {
    setAuditingInfo(model, updateUser);
    if (setModifiedFieldsOnly) {
      return mapper.updateByExampleModified(model, example);
    }
    return mapper.updateByExample(model, example);
  }

  public int delete(DbExample<M> example) {
    return mapper.deleteByExample(example);
  }

  protected void setAuditingInfo(M model, HasUserId user, boolean isNew) {
    model.setFetchDate(ObjectHelper.coalesce(model::getFetchDate, model::getSystemLastChangeDate,
        model::getLastChangeDate));
    model.setLastChangeDate(LocalDateTime.now());
    model.setSystemLastChangeDate(model.getLastChangeDate());
    if (user != null && model.getLastChangeUserId() == null) {
      model.setLastChangeUserId(user.getUserId());
    }
    if (isNew) {
      if (model.getCreateDate() == null) {
        model.setCreateDate(LocalDateTime.now());
      }
      if (user != null && model.getCreateUserId() == null) {
        model.setCreateUserId(user.getUserId());
      }
    }
  }

  protected void setAuditingInfo(M model, HasUserId user) {
    setAuditingInfo(model, user, false);
  }

  public void setModifiedFieldsOnly(boolean setModifiedFieldsOnly) {
    this.setModifiedFieldsOnly = setModifiedFieldsOnly;
  }
}
