package com.lbisoft.core.app.db.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lbisoft.core.app.components.person.model.QuickSearchPerson;

@Mapper
public interface QuickSearchExtendedMapper {

  public List<QuickSearchPerson> getQuickSearchPersons();

  public QuickSearchPerson getQuickSearchPersonById(@Param("personId") Integer personId);
}
