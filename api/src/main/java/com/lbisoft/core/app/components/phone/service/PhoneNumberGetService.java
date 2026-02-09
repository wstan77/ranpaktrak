package com.lbisoft.core.app.components.phone.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.phone.model.PersonPhoneNumber;
import com.lbisoft.core.app.db.mapper.generated.PersonPhoneNumberVwMapper;
import com.lbisoft.core.app.db.model.generated.PersonPhoneNumberVw;
import com.lbisoft.core.app.db.model.generated.PersonPhoneNumberVwExample;
import com.lbisoft.core.app.db.model.generated.PersonPhoneNumberVwKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PhoneNumberGetService {
  private final Dao<PersonPhoneNumberVw, PersonPhoneNumberVwKey> personPhoneNumberDao;
  private final AbstractProvider<PersonPhoneNumberVw, PersonPhoneNumber> personPhoneNumberProvider;

  @Autowired
  public PhoneNumberGetService(PersonPhoneNumberVwMapper personPnMapper) {
    this.personPhoneNumberDao = Dao.getDao(personPnMapper, pn -> pn);
    this.personPhoneNumberProvider = ProviderBuilder.simple(PersonPhoneNumberVw.class, PersonPhoneNumber.class);
  }

  @Transactional(readOnly = true)
  public List<PersonPhoneNumber> getPhoneNumbersByPersonId(Integer personId) {
    return getPhoneNumbersByPersonIds(List.of(personId));
  }

  @Transactional(readOnly = true)
  public List<PersonPhoneNumber> getPhoneNumbersByPersonIds(List<Integer> personIds) {
    if (personIds.isEmpty()) {
      return Collections.emptyList();
    }

    PersonPhoneNumberVwExample byPersonIds = new PersonPhoneNumberVwExample();
    byPersonIds.createCriteria().andPersonIdIn(personIds);

    return personPhoneNumberProvider.mapAll(personPhoneNumberDao.select(byPersonIds));
  }
}
