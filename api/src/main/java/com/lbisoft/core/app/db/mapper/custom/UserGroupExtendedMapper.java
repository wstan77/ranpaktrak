package com.lbisoft.core.app.db.mapper.custom;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserGroupExtendedMapper {
  public void updateActiveFlgs();
}
