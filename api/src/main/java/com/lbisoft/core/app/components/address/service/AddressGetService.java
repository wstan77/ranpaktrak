package com.lbisoft.core.app.components.address.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.address.model.PersonAddress;
import com.lbisoft.core.app.db.mapper.generated.PersonAddressVwMapper;
import com.lbisoft.core.app.db.model.generated.PersonAddressVw;
import com.lbisoft.core.app.db.model.generated.PersonAddressVwExample;
import com.lbisoft.core.app.db.model.generated.PersonAddressVwKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class AddressGetService {
  private final Dao<PersonAddressVw, PersonAddressVwKey> personAddressDao;
  private final AbstractProvider<PersonAddressVw, PersonAddress> personAddressProvider;

  @Autowired
  public AddressGetService(PersonAddressVwMapper personAddressVwMapper) {
    personAddressDao = Dao.getDao(personAddressVwMapper, pa -> pa);
    personAddressProvider = ProviderBuilder.simple(PersonAddressVw.class, PersonAddress.class);
  }

  @Transactional(readOnly = true)
  public List<PersonAddress> getAddressesByPersonId(Integer personId) {
    return getAddressesByPersonIds(List.of(personId));
  }

  @Transactional(readOnly = true)
  public List<PersonAddress> getAddressesByPersonIds(List<Integer> personIds) {
    if (personIds.isEmpty()) {
      return Collections.emptyList();
    }

    PersonAddressVwExample byPersonIds = new PersonAddressVwExample();
    byPersonIds.createCriteria().andPersonIdIn(personIds);

    return personAddressProvider.mapAll(personAddressDao.select(byPersonIds));
  }
}
