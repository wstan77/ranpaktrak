package com.lbisoft.core.app.db.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.model.PersonSearchForm;

@Mapper
public interface PersonExtendedMapper {
  List<Person> search(@Param("form") PersonSearchForm form);

  List<Person> getNewPersonMatches(@Param("newPerson") Person person);
}
