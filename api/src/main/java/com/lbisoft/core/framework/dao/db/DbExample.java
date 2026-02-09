package com.lbisoft.core.framework.dao.db;

public interface DbExample<T extends DbModel> {
  String getOrderByClause();

  void setOrderByClause(String orderBy);
}
