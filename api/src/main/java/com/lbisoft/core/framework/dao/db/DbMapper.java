package com.lbisoft.core.framework.dao.db;

public interface DbMapper<M extends DbModel, K>
    extends DbMapperWithoutPrimaryKey<M> {

  int updateByPrimaryKey(M record);

  int updateByPrimaryKeyModified(M record);

  int deleteByPrimaryKey(K key);

  M selectByPrimaryKey(K key);
}
