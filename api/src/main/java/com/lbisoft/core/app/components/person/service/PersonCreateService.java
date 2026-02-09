package com.lbisoft.core.app.components.person.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.person.event.PersonEventPublisher;
import com.lbisoft.core.app.components.person.event.PersonSavedEvent;
import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.provider.PersonProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.app.db.mapper.custom.PersonExtendedMapper;
import com.lbisoft.core.app.db.mapper.generated.PersonDbMapper;
import com.lbisoft.core.app.db.model.generated.PersonDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.response.ListResponse;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;

@CoreService
public class PersonCreateService {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  private final Dao<PersonDb, Integer> dao;
  // private final PersonValidator validator;
  private final PersonProvider provider = new PersonProvider();

  private final PersonEventPublisher eventPublisher;
  private final UserActivitiesService activitiesService;
  private final PersonExtendedMapper extendedMapper;

  public PersonCreateService(PersonDbMapper mapper, PersonEventPublisher eventPublisher,
      UserActivitiesService activitiesService,
      PersonExtendedMapper extendedMapper) {
    this.activitiesService = activitiesService;
    dao = Dao.getDao(mapper, PersonDb::getPersonId);

    this.eventPublisher = eventPublisher;
    this.extendedMapper = extendedMapper;
  }

  public Person createPerson(Person person, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);
    // validator.forCreate(person).validate();

    PersonDb db = provider.reverseMap(person);
    db.setRecordStatusLk(RecordStatusLks.APPROVED);
    db.setDisplayName(db.getFirstName() + " " + db.getLastName());
    dao.insert(db, user);

    LOGGER.info("Created person " + person.getDisplayName() + ", ID: " + db.getPersonId());
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.CREATE_PERSON).relatedObject(person).build(),
        request);

    eventPublisher.publishEvent(new PersonSavedEvent(this, db.getPersonId(), user));
    return (Person) provider.map(db).addResponseInfo("Successfully Created Person: " + db.getDisplayName());
  }

  public ListResponse<Person> getNewPersonMatches(Person person, HttpServletRequest request) {
    // validator.forCreate(person).validate();
    List<Person> personMatches = extendedMapper.getNewPersonMatches(person);
    return new ListResponse<Person>(personMatches);
  }
}
