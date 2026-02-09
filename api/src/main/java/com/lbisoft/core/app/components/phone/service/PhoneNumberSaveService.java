package com.lbisoft.core.app.components.phone.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.components.phone.model.PersonPhoneNumber;
import com.lbisoft.core.app.components.phone.model.PhoneNumber;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.PersonPhoneNumberDbMapper;
import com.lbisoft.core.app.db.mapper.generated.PhoneNumberDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonPhoneNumberDb;
import com.lbisoft.core.app.db.model.generated.PersonPhoneNumberDbExample;
import com.lbisoft.core.app.db.model.generated.PersonPhoneNumberDbKey;
import com.lbisoft.core.app.db.model.generated.PhoneNumberDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PhoneNumberSaveService {
  private final Dao<PhoneNumberDb, Integer> dao;
  private final Dao<PersonPhoneNumberDb, PersonPhoneNumberDbKey> personDao;
  private final AbstractProvider<PhoneNumberDb, PhoneNumber> provider;

  @Autowired
  public PhoneNumberSaveService(PhoneNumberDbMapper mapper, PersonPhoneNumberDbMapper personPhoneNumberMapper) {
    dao = Dao.getDao(mapper, PhoneNumberDb::getPhoneNumberId);
    personDao = Dao.getDao(personPhoneNumberMapper, p -> p);
    provider = ProviderBuilder.simple(PhoneNumberDb.class, PhoneNumber.class);
  }

  public void savePhoneNumbersForPerson(Integer personId, List<PersonPhoneNumber> phoneNumbers, AppUser user) {

    PersonPhoneNumberDbExample existingPhoneExample = new PersonPhoneNumberDbExample();
    existingPhoneExample.createCriteria().andPersonIdEqualTo(personId);
    List<PersonPhoneNumberDb> existingPersonPhoneNums = personDao.select(existingPhoneExample);

    if (phoneNumbers.size() == existingPersonPhoneNums.size()) {
      // check if phone numbers are the same ids in the same order
      boolean same = true;
      for (int i = 0; i < phoneNumbers.size(); i++) {
        if (!phoneNumbers.get(i).getPhoneNumberId().equals(existingPersonPhoneNums.get(i).getPhoneNumberId())) {
          same = false;
          break;
        }
      }
      if (same) {
        // if they are in the same order then just update, don't delete all and insert.
        List<PhoneNumberDb> mappedPhoneNumsToUpdate = provider.reverseMapAll(phoneNumbers);
        dao.update(mappedPhoneNumsToUpdate, user);
        return;
      }
    }

    // Delete all of existing personPhoneNumbers
    personDao.delete(existingPhoneExample);

    // delete all existing phoneNumbers (actual phone numbers data)
    for (PersonPhoneNumberDb p : existingPersonPhoneNums) {
      dao.delete(p.getPhoneNumberId());
    }

    List<PhoneNumberDb> mappedPhoneNumbers = provider.reverseMapAll(phoneNumbers);
    dao.insert(mappedPhoneNumbers, user);

    List<PersonPhoneNumberDb> personPhoneNumbersToInsert = mappedPhoneNumbers.stream().map(e -> {
      PersonPhoneNumberDb phoneNumberDb = new PersonPhoneNumberDb();
      phoneNumberDb.setPhoneNumberId(e.getPhoneNumberId());
      phoneNumberDb.setPersonId(personId);
      return phoneNumberDb;
    }).toList();
    personDao.insert(personPhoneNumbersToInsert, user);
  }
}
