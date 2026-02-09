package com.lbisoft.core.app.components.person.xrefs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.person.xrefs.model.PersonXref;
import com.lbisoft.core.app.components.person.xrefs.provider.PersonXrefProvider;
import com.lbisoft.core.app.db.mapper.generated.PersonXrefDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonXrefDb;
import com.lbisoft.core.app.db.model.generated.PersonXrefDbExample;
import com.lbisoft.core.app.db.model.generated.PersonXrefDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class PersonXrefGetService {
  private final Dao<PersonXrefDb, PersonXrefDbKey> dao;
  private final PersonXrefProvider personProvider;

  @Autowired
  public PersonXrefGetService(PersonXrefDbMapper mapper) {
    dao = Dao.getDao(mapper, x -> x);
    personProvider = new PersonXrefProvider();
  }

  @Transactional(readOnly = true)
  public List<PersonXref> getXrefByPersonId(Integer personId) {
    PersonXrefDbExample example = new PersonXrefDbExample();
    example.createCriteria().andPersonIdEqualTo(personId);
    List<PersonXrefDb> personXrefs = dao.select(example);
    return personProvider.mapAll(personXrefs);
  }

  @Transactional(readOnly = true)
  public List<PersonXref> getXrefByPersonIds(List<Integer> personIds) {
    PersonXrefDbExample byPersonIds = new PersonXrefDbExample();
    byPersonIds.createCriteria().andPersonIdIn(personIds);
    return personProvider.mapAll(dao.select(byPersonIds));
  }
}
