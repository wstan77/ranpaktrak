package com.lbisoft.core.app.components.person.service;

import org.springframework.context.ApplicationEventPublisher;

import com.lbisoft.core.app.components.address.service.AddressSaveService;
import com.lbisoft.core.app.components.email.service.EmailSaveService;
import com.lbisoft.core.app.components.person.event.PersonSavedEvent;
import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.provider.PersonProvider;
import com.lbisoft.core.app.components.person.socialprofiles.service.PersonSocialProfileSaveService;
import com.lbisoft.core.app.components.person.xrefs.service.PersonXrefSaveService;
import com.lbisoft.core.app.components.phone.service.PhoneNumberSaveService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.PersonDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;

@CoreService
public class PersonSaveService {
  private final Dao<PersonDb, Integer> dao;
  // private final PersonValidator validator;
  private final PersonProvider provider = new PersonProvider();

  private final ApplicationEventPublisher eventPublisher;

  private final PhoneNumberSaveService phoneNumberSaveService;
  private final EmailSaveService emailSaveService;
  private final AddressSaveService addressSaveService;
  private final PersonSocialProfileSaveService socialProfileSaveService;
  private final PersonXrefSaveService xrefSaveService;

  public PersonSaveService(PersonDbMapper mapper, ApplicationEventPublisher eventPublisher,
      PhoneNumberSaveService phoneNumberSaveService,
      EmailSaveService emailSaveService, AddressSaveService addressSaveService,
      PersonSocialProfileSaveService socialProfileSaveService,
      PersonXrefSaveService xrefSaveService) {
    dao = Dao.getDao(mapper, PersonDb::getPersonId);

    this.eventPublisher = eventPublisher;

    this.phoneNumberSaveService = phoneNumberSaveService;
    this.emailSaveService = emailSaveService;
    this.addressSaveService = addressSaveService;
    this.socialProfileSaveService = socialProfileSaveService;
    this.xrefSaveService = xrefSaveService;
  }

  public PersonDb savePerson(Person person, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);
    // validator.forSave(person).validate();
    // try update
    PersonDb db = provider.reverseMap(person);

    setModifiedFields(db);
    dao.update(db, user);
    saveAdditionalPersonFields(person, user);

    onPersonSave(db.getPersonId(), user);

    return db;
  }

  private void saveAdditionalPersonFields(Person person, AppUser user) {
    phoneNumberSaveService.savePhoneNumbersForPerson(person.getPersonId(), person.getPhoneNumbers(),
        user);
    emailSaveService.saveEmailsForPerson(person.getPersonId(), person.getEmails(), user);
    addressSaveService.saveAddressesForPerson(person.getPersonId(), person.getAddresses(), user);
    socialProfileSaveService.saveSocialProfilesForPerson(person.getPersonId(), person.getSocialProfiles(), user);
    xrefSaveService.saveXrefsForPerson(person.getPersonId(), person.getXrefs(), user);
  }

  private void onPersonSave(Integer personId, AppUser user) {
    eventPublisher.publishEvent(new PersonSavedEvent(this, personId, user));
  }

  private void setModifiedFields(PersonDb db) {
    db.setMiddleNameModified(true);
    db.setLegalFirstNameModified(true);
    db.setLegalLastNameModified(true);
    db.setDateOfBirthModified(true);
    db.setGenderLkModified(true);
  }
}
