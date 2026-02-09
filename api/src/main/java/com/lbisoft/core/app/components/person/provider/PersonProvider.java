package com.lbisoft.core.app.components.person.provider;

import com.lbisoft.core.app.components.address.model.PersonAddress;
import com.lbisoft.core.app.components.email.model.PersonEmail;
import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.socialprofiles.model.PersonSocialProfile;
import com.lbisoft.core.app.components.person.xrefs.model.PersonXref;
import com.lbisoft.core.app.components.phone.model.PersonPhoneNumber;
import com.lbisoft.core.app.db.model.generated.PersonDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class PersonProvider extends AbstractProvider<PersonDb, Person> {

  @Override
  protected void defineClassMap(ClassMapBuilder<PersonDb, Person> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<Person> childrenMap) {
    childrenMap.defineList(PersonAddress.class, Person::setAddresses,
        ChildrenMap.matchSimple(Person::getPersonId, PersonAddress::getPersonId));
    childrenMap.defineList(PersonPhoneNumber.class, Person::setPhoneNumbers,
        ChildrenMap.matchSimple(Person::getPersonId, PersonPhoneNumber::getPersonId));
    childrenMap.defineList(PersonXref.class, Person::setXrefs,
        ChildrenMap.matchSimple(Person::getPersonId, PersonXref::getPersonId));
    childrenMap.defineList(PersonEmail.class, Person::setEmails,
        ChildrenMap.matchSimple(Person::getPersonId, PersonEmail::getPersonId));
    childrenMap.defineList(PersonSocialProfile.class, Person::setSocialProfiles,
        ChildrenMap.matchSimple(Person::getPersonId, PersonSocialProfile::getPersonId));

  }

  @Override
  public PersonDb completeReverseMap(Person input, PersonDb output) {
    return output;
  }
}
