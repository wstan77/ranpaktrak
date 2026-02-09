package com.lbisoft.core.app.components.person.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.person.event.PersonBeforeDeleteEvent;
import com.lbisoft.core.app.components.person.event.PersonEventPublisher;
import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.provider.PersonProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.app.db.mapper.generated.PersonDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;

@CoreService
public class PersonDeleteService {
  private final Dao<PersonDb, Integer> dao;
  // private final PersonValidator validator;
  private final PersonProvider provider = new PersonProvider();
  // private final PersonEtlProvider etlProvider;

  private final PersonEventPublisher eventPublisher;
  private final UserActivitiesService activitiesService;

  @Autowired
  public PersonDeleteService(PersonDbMapper mapper, PersonEventPublisher eventPublisher,
      UserActivitiesService activitiesService) {
    this.activitiesService = activitiesService;
    dao = Dao.getDao(mapper, PersonDb::getPersonId);

    // validator = new PersonValidator();
    // provider = new PersonProvider();
    this.eventPublisher = eventPublisher;
  }

  public Person deletePerson(Integer personId, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);

    PersonDb db = dao.select(personId).orElseThrow(ResourceNotFoundException::new);
    eventPublisher.publishEvent(new PersonBeforeDeleteEvent(this, personId));

    db.setRecordStatusLk(RecordStatusLks.DELETED);

    dao.update(db, user);

    Person toReturn = provider.map(db);
    toReturn
        .addResponseInfo("Successfully deleted " + toReturn.getDisplayName() + ", ID: " + toReturn.getPersonId() + ".");
    activitiesService.logActivityById(ActivityTypes.DELETE_PERSON, db.getPersonId(), request);
    return toReturn;
  }
}
