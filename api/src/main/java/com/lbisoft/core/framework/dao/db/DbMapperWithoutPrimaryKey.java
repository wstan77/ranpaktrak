package com.lbisoft.core.framework.dao.db;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface DbMapperWithoutPrimaryKey<M extends DbModel> {

  int insert(M record);

  int insertModified(M record);

  int updateByExample(M record, DbExample<M> example);

  int updateByExampleModified(M record, DbExample<M> example);

  int deleteByExample(DbExample<M> example);

  long countByExample(DbExample<M> example);

  List<M> selectByExample(DbExample<M> example);

  List<M> selectByExampleWithRowbounds(DbExample<M> example, RowBounds rowBounds);
}
