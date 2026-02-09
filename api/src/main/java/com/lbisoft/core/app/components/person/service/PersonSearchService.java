package com.lbisoft.core.app.components.person.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.model.PersonSearchForm;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.custom.PersonExtendedMapper;
import com.lbisoft.core.framework.response.ListResponse;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PersonSearchService {
  private final PersonExtendedMapper extendedMapper;

  @Autowired
  public PersonSearchService(PersonExtendedMapper extendedMapper) {
    this.extendedMapper = extendedMapper;
  }

  @Transactional(readOnly = true)
  public ListResponse<Person> search(PersonSearchForm searchForm, AppUser appUser) {
    List<Person> persons = extendedMapper.search(searchForm);
    return new ListResponse<>(persons, Constants.DEFAULT_QUERY_LIMIT);
  }
}
