package com.lbisoft.core.app.components.person.socialprofiles.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.components.person.socialprofiles.model.PersonSocialProfile;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.PersonSocialProfileDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonSocialProfileDb;
import com.lbisoft.core.app.db.model.generated.PersonSocialProfileDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PersonSocialProfileSaveService {
  private final Dao<PersonSocialProfileDb, Integer> personDao;
  private final AbstractProvider<PersonSocialProfileDb, PersonSocialProfile> provider;

  @Autowired
  public PersonSocialProfileSaveService(PersonSocialProfileDbMapper socialMapper) {
    personDao = Dao.getDao(socialMapper, p -> p.getPersonSocialProfileId());
    provider = ProviderBuilder.simple(PersonSocialProfileDb.class, PersonSocialProfile.class);
  }

  public void saveSocialProfilesForPerson(Integer personId, List<PersonSocialProfile> socialProfiles, AppUser user) {
    // Delete Social Profiles
    PersonSocialProfileDbExample deleteExample = new PersonSocialProfileDbExample();
    deleteExample.createCriteria().andPersonIdEqualTo(personId);
    personDao.delete(deleteExample);

    // Save Social Profiles
    personDao.insert(provider.reverseMapAll(socialProfiles), user);
  }
}
