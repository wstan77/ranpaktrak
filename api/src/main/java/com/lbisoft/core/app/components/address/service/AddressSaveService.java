package com.lbisoft.core.app.components.address.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.components.address.model.Address;
import com.lbisoft.core.app.components.address.model.PersonAddress;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.AddressDbMapper;
import com.lbisoft.core.app.db.mapper.generated.PersonAddressDbMapper;
import com.lbisoft.core.app.db.model.generated.AddressDb;
import com.lbisoft.core.app.db.model.generated.PersonAddressDb;
import com.lbisoft.core.app.db.model.generated.PersonAddressDbExample;
import com.lbisoft.core.app.db.model.generated.PersonAddressDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class AddressSaveService {
  private final Dao<AddressDb, Integer> dao;
  private final Dao<PersonAddressDb, PersonAddressDbKey> personDao;
  private final AbstractProvider<AddressDb, Address> provider;

  @Autowired
  public AddressSaveService(AddressDbMapper mapper, PersonAddressDbMapper personMapper) {
    dao = Dao.getDao(mapper, AddressDb::getAddressId);
    personDao = Dao.getDao(personMapper, p -> p);
    provider = ProviderBuilder.simple(AddressDb.class, Address.class);
  }

  public void saveAddressesForPerson(Integer personId, List<PersonAddress> addresses, AppUser user) {

    PersonAddressDbExample existingAddressExample = new PersonAddressDbExample();
    existingAddressExample.createCriteria().andPersonIdEqualTo(personId);
    List<PersonAddressDb> existingPersonAddresses = personDao.select(existingAddressExample);

    if (addresses.size() == existingPersonAddresses.size()) {
      // check if addresses are the same ids in the same order
      boolean same = true;
      for (int i = 0; i < addresses.size(); i++) {
        if (!addresses.get(i).getAddressId().equals(existingPersonAddresses.get(i).getAddressId())) {
          same = false;
          break;
        }
      }
      if (same) {
        List<AddressDb> mappedAddressesToUpdate = provider.reverseMapAll(addresses);
        dao.update(mappedAddressesToUpdate, user);
        return;
      }
    }

    // Delete all of existing personAddresses
    personDao.delete(existingAddressExample);

    // delete all existing addresses (actual address data)
    for (PersonAddressDb p : existingPersonAddresses) {
      dao.delete(p.getAddressId());
    }

    List<AddressDb> mappedAddresses = provider.reverseMapAll(addresses);
    dao.insert(mappedAddresses, user);

    List<PersonAddressDb> personAddressesToInsert = mappedAddresses.stream().map(a -> {
      PersonAddressDb addressDb = new PersonAddressDb();
      addressDb.setAddressId(a.getAddressId());
      addressDb.setPersonId(personId);
      return addressDb;
    }).toList();
    personDao.insert(personAddressesToInsert, user);
  }
}
