package com.lbisoft.core.app.components.person.xrefs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.components.person.xrefs.model.PersonXref;
import com.lbisoft.core.app.components.person.xrefs.provider.PersonXrefProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.PersonXrefDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonXrefDb;
import com.lbisoft.core.app.db.model.generated.PersonXrefDbExample;
import com.lbisoft.core.app.db.model.generated.PersonXrefDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PersonXrefSaveService {
  private final Dao<PersonXrefDb, PersonXrefDbKey> personDao;
  private final PersonXrefProvider personProvider;

  @Autowired
  public PersonXrefSaveService(PersonXrefDbMapper xrefMapper) {
    personDao = Dao.getDao(xrefMapper, p -> p);
    personProvider = new PersonXrefProvider();
  }

  public void saveXrefsForPerson(Integer personId, List<PersonXref> xrefs, AppUser user) {
    // Delete Xrefs
    PersonXrefDbExample deleteExample = new PersonXrefDbExample();
    deleteExample.createCriteria().andPersonIdEqualTo(personId);
    personDao.delete(deleteExample);

    // Save Xrefs
    if (xrefs != null) {
      personDao.insert(personProvider.reverseMapAll(xrefs), user);
    }
  }
}
