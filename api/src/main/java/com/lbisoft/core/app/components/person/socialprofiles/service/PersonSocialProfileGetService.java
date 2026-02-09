package com.lbisoft.core.app.components.person.socialprofiles.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.person.socialprofiles.model.PersonSocialProfile;
import com.lbisoft.core.app.db.mapper.generated.PersonSocialProfileDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonSocialProfileDb;
import com.lbisoft.core.app.db.model.generated.PersonSocialProfileDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PersonSocialProfileGetService {
  private final Dao<PersonSocialProfileDb, Integer> dao;
  private final AbstractProvider<PersonSocialProfileDb, PersonSocialProfile> provider;

  @Autowired
  public PersonSocialProfileGetService(PersonSocialProfileDbMapper mapper) {
    dao = Dao.getDao(mapper, PersonSocialProfileDb::getPersonSocialProfileId);
    provider = ProviderBuilder.simple(PersonSocialProfileDb.class, PersonSocialProfile.class);
  }

  @Transactional(readOnly = true)
  public List<PersonSocialProfile> getSocialProfileByPersonId(Integer personId) {
    return getSocialProfileByPersonIds(List.of(personId));
  }

  @Transactional(readOnly = true)
  public List<PersonSocialProfile> getSocialProfileByPersonIds(List<Integer> personIds) {
    PersonSocialProfileDbExample byPersonIds = new PersonSocialProfileDbExample();
    byPersonIds.createCriteria().andPersonIdIn(personIds);
    return provider.mapAll(dao.select(byPersonIds));
  }
}
