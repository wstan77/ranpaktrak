package com.lbisoft.core.app.components.email.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.email.model.PersonEmail;
import com.lbisoft.core.app.db.mapper.generated.PersonEmailVwMapper;
import com.lbisoft.core.app.db.model.generated.PersonEmailVw;
import com.lbisoft.core.app.db.model.generated.PersonEmailVwExample;
import com.lbisoft.core.app.db.model.generated.PersonEmailVwKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class EmailGetService {
  private final Dao<PersonEmailVw, PersonEmailVwKey> personEmailDao;
  private final AbstractProvider<PersonEmailVw, PersonEmail> personEmailProvider;

  @Autowired
  public EmailGetService(PersonEmailVwMapper personEmailMapper) {
    personEmailDao = Dao.getDao(personEmailMapper, e -> e);
    personEmailProvider = ProviderBuilder.simple(PersonEmailVw.class, PersonEmail.class);
  }

  @Transactional(readOnly = true)
  public List<PersonEmail> getEmailsByPersonId(Integer personId) {
    return getEmailsByPersonIds(List.of(personId));
  }

  @Transactional(readOnly = true)
  public List<PersonEmail> getEmailsByPersonIds(List<Integer> personIds) {
    if (personIds.isEmpty()) {
      return Collections.emptyList();
    }

    PersonEmailVwExample byPersonIds = new PersonEmailVwExample();
    byPersonIds.createCriteria().andPersonIdIn(personIds);

    return personEmailProvider.mapAll(personEmailDao.select(byPersonIds));
  }

}
