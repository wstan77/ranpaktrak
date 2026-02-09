package com.lbisoft.core.app.components.person.provider;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lbisoft.core.app.components.address.model.PersonAddress;
import com.lbisoft.core.app.components.email.model.PersonEmail;
import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.socialprofiles.model.PersonSocialProfile;
import com.lbisoft.core.app.components.person.xrefs.model.PersonXref;
import com.lbisoft.core.app.components.phone.model.PersonPhoneNumber;
import com.lbisoft.core.app.db.model.generated.PersonDb;

public class PersonProviderTest {
  private final PersonProvider provider = new PersonProvider();
  private PersonDb db;
  private Person model;
  private List<PersonAddress> addresses = Collections.emptyList();
  private List<PersonPhoneNumber> phones = Collections.emptyList();
  private List<PersonXref> xrefs = Collections.emptyList();
  private List<PersonEmail> emails = Collections.emptyList();
  private List<PersonSocialProfile> socialProfiles = Collections.emptyList();

  @BeforeEach
  public void setup() {
    db = new PersonDb();
    db.setDateOfBirth(LocalDate.now());
    db.setDisplayName("display");
    db.setFirstName("first");
    db.setGenderLk("gender");
    db.setLastName("last");
    db.setLegalFirstName("legalfirst");
    db.setLegalLastName("legallast");
    db.setMiddleName("middle");
    db.setPersonId(123);
    db.setPersonTypeLk("person");
    db.setPrimaryLanguageLk("language1");
    db.setPronouns("pronoun");
    db.setRecordStatusLk("record");
    db.setSecondaryLanguageLk("language2");

    model = new Person();
    model.setDateOfBirth(db.getDateOfBirth());
    model.setDisplayName(db.getDisplayName());
    model.setFirstName(db.getFirstName());
    model.setGenderLk(db.getGenderLk());
    model.setLastName(db.getLastName());
    model.setLegalFirstName(db.getLegalFirstName());
    model.setLegalLastName(db.getLegalLastName());
    model.setMiddleName(db.getMiddleName());
    model.setPersonId(db.getPersonId());
    model.setPersonTypeLk(db.getPersonTypeLk());
    model.setPrimaryLanguageLk(db.getPrimaryLanguageLk());
    model.setPronouns(db.getPronouns());
    model.setRecordStatusLk(db.getRecordStatusLk());
    model.setSecondaryLanguageLk(db.getSecondaryLanguageLk());

    PersonAddress address = new PersonAddress();
    address.setPersonId(db.getPersonId());
    addresses = List.of(address);

    PersonPhoneNumber phone = new PersonPhoneNumber();
    phone.setPersonId(db.getPersonId());
    phones = List.of(phone);

    PersonXref xref = new PersonXref();
    xref.setPersonId(db.getPersonId());
    xrefs = List.of(xref);

    PersonEmail email = new PersonEmail();
    email.setPersonId(db.getPersonId());
    emails = List.of(email);

    PersonSocialProfile socialProfile = new PersonSocialProfile();
    socialProfile.setPersonId(db.getPersonId());
    socialProfiles = List.of(socialProfile);

    model.setAddresses(addresses);
    model.setEmails(emails);
    model.setPhoneNumbers(phones);
    model.setSocialProfiles(socialProfiles);
    model.setXrefs(xrefs);
  }

  @Test
  public void shouldConvertDbToModel() {
    Person test = provider.map(db, addresses, phones, xrefs, emails, socialProfiles);
    assertThat(test).usingRecursiveComparison().isEqualTo(model);
  }

  @Test
  public void shouldConvertModeltoDb() {
    PersonDb test = provider.reverseMap(model);
    assertThat(test).usingRecursiveComparison().isEqualTo(db);
  }
}
