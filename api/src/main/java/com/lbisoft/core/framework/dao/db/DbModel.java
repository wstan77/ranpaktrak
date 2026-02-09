package com.lbisoft.core.framework.dao.db;

import java.time.LocalDateTime;

public interface DbModel {
  DbExample<? extends DbModel> createExample();

  // The following fields are for auditing.
  // These fields need to be change if they aren't on EVERY table in the database.

  LocalDateTime getCreateDate();

  void setCreateDate(LocalDateTime createDate);

  Integer getCreateUserId();

  void setCreateUserId(Integer userId);

  LocalDateTime getLastChangeDate();

  void setLastChangeDate(LocalDateTime lastchDate);

  Integer getLastChangeUserId();

  void setLastChangeUserId(Integer userId);

  LocalDateTime getSystemLastChangeDate();

  void setSystemLastChangeDate(LocalDateTime lastchDate);

  // Used for concurrent modification check, should NOT be in the database
  LocalDateTime getFetchDate();

  void setFetchDate(LocalDateTime fetchDate);
}
