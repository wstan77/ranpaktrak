package com.lbisoft.core.app.components.person.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.address.service.AddressGetService;
import com.lbisoft.core.app.components.email.service.EmailGetService;
import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.provider.PersonProvider;
import com.lbisoft.core.app.components.person.socialprofiles.service.PersonSocialProfileGetService;
import com.lbisoft.core.app.components.person.xrefs.service.PersonXrefGetService;
import com.lbisoft.core.app.components.phone.service.PhoneNumberGetService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.PersonDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PersonGetService {
  private final Dao<PersonDb, Integer> dao;
  private final PersonProvider provider;
  private final PhoneNumberGetService phoneNumberGetService;
  private final AddressGetService addressGetService;
  private final EmailGetService emailGetService;
  private final PersonSocialProfileGetService socialProfileGetService;
  private final PersonXrefGetService xrefGetService;

  @Autowired
  public PersonGetService(PersonDbMapper mapper,
      PhoneNumberGetService phoneNumberGetService, EmailGetService emailGetService, AddressGetService addressGetService,
      PersonSocialProfileGetService socialProfileGetService, PersonXrefGetService xrefGetService) {
    this.dao = Dao.getDao(mapper, PersonDb::getPersonId);
    this.provider = new PersonProvider();
    this.phoneNumberGetService = phoneNumberGetService;
    this.emailGetService = emailGetService;
    this.addressGetService = addressGetService;
    this.socialProfileGetService = socialProfileGetService;
    this.xrefGetService = xrefGetService;
  }

  @Transactional(readOnly = true)
  public Person getPerson(Integer personId, AppUser user) {
    return dao.select(personId).flatMap(this::populatePerson).orElseThrow(ResourceNotFoundException::new);
  }

  Optional<Person> populatePerson(PersonDb db) {
    return populatePersons(List.of(db)).stream().findAny();
  }

  List<Person> populatePersons(List<PersonDb> dbs) {
    List<Integer> ids = dbs.stream().map(PersonDb::getPersonId).toList();

    return provider.mapParallel(dbs,
        () -> phoneNumberGetService.getPhoneNumbersByPersonIds(ids),
        () -> addressGetService.getAddressesByPersonIds(ids),
        () -> emailGetService.getEmailsByPersonIds(ids),
        () -> socialProfileGetService.getSocialProfileByPersonIds(ids),
        () -> xrefGetService.getXrefByPersonIds(ids));
  }
}
