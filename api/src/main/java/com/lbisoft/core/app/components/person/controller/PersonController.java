package com.lbisoft.core.app.components.person.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.person.model.Person;
import com.lbisoft.core.app.components.person.model.PersonSearchForm;
import com.lbisoft.core.app.components.person.service.PersonCreateService;
import com.lbisoft.core.app.components.person.service.PersonDeleteService;
import com.lbisoft.core.app.components.person.service.PersonGetService;
import com.lbisoft.core.app.components.person.service.PersonSaveService;
import com.lbisoft.core.app.components.person.service.PersonSearchService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.response.ListResponse;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/persons")
@Tag(name = PersonController.PERSONS)
public class PersonController {
  static final String PERSONS = "Persons";

  private final PersonGetService personGetService;
  private final PersonSaveService personSaveService;
  private final PersonCreateService personCreateService;
  private final PersonDeleteService personDeleteService;
  private final PersonSearchService personSearchService;
  private final UserActivitiesService activitiesService;

  @Autowired
  public PersonController(PersonGetService personGetService, UserActivitiesService activitiesService,
      PersonSaveService personSaveService, PersonCreateService personCreateService,
      PersonDeleteService personDeleteService,
      PersonSearchService personSearchService) {
    this.personGetService = personGetService;
    this.personSaveService = personSaveService;
    this.activitiesService = activitiesService;
    this.personCreateService = personCreateService;
    this.personDeleteService = personDeleteService;
    this.personSearchService = personSearchService;
  }

  @PreAuthorize("hasAuthority('CAN_READ_PERSONS')")
  @GetMapping("/{personId}")
  @Operation(summary = "Get person by id", tags = { PERSONS })
  public Person getPerson(@PathVariable Integer personId, HttpServletRequest request) {
    activitiesService.logActivityById(ActivityTypes.LOAD_PERSON, personId, request);
    return personGetService.getPerson(personId, RequestHelper.getUser(request));
  }

  @PreAuthorize("hasAuthority('CAN_EDIT_PERSONS')")
  @PostMapping("")
  @Operation(summary = "Create person", tags = { PERSONS })
  public Person createPerson(@RequestBody Person person, HttpServletRequest request) {
    return personCreateService.createPerson(person, request);
  }

  @PreAuthorize("hasAuthority('CAN_EDIT_PERSONS')")
  @PutMapping("")
  @Operation(summary = "Save person", tags = { PERSONS })
  public Person savePerson(@RequestBody Person person, HttpServletRequest request) {
    var db = personSaveService.savePerson(person, request);

    Person toReturn = personGetService.getPerson(db.getPersonId(), RequestHelper.getUser(request));
    toReturn.addResponseInfo("Successfully saved " + toReturn.getDisplayName() + ".");
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.SAVE_PERSON).relatedObject(person).build(),
        request);
    return toReturn;
  }

  @PreAuthorize("hasAuthority('CAN_DELETE_PERSONS')")
  @DeleteMapping("/{personId}")
  @Operation(summary = "Delete person", tags = { PERSONS })
  public Person deletePerson(@PathVariable Integer personId, HttpServletRequest request) {
    return personDeleteService.deletePerson(personId, request);
  }

  /*
   * @PreAuthorize("hasAuthority('CAN_EDIT_PERSONS')")
   *
   * @PutMapping("/{personId}/headshot")
   *
   * @Operation(summary = "Save person photo", tags = { PERSONS })
   * public void savePersonHeadshot(@PathVariable Integer personId, @RequestBody
   * HeadShotForm headshot,
   * HttpServletRequest request) {
   * service.savePersonHeadshot(personId, headshot.getHeadShot(),
   * RequestHelper.getUser(request));
   * }
   *
   * @GetMapping("/{personId}/headshot")
   *
   * @Operation(summary = "Get person photo", tags = { PERSONS })
   * public void getPersonHeadshot(@PathVariable Integer personId,
   * HttpServletRequest request,
   * HttpServletResponse response) {
   * service.sendHeadshot(personId, response, request,
   * RequestHelper.getUser(request));
   * }
   */

  @PreAuthorize("hasAuthority('CAN_READ_PERSONS')")
  @PostMapping("/search")
  @Operation(summary = "Search for persons", tags = { PERSONS })
  public ListResponse<Person> searchPersons(@RequestBody PersonSearchForm form, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.SEARCH_PERSONS).relatedObject(form).build(),
        request);
    return personSearchService.search(form, RequestHelper.getUser(request));
  }

  @PreAuthorize("hasAuthority('CAN_READ_PERSONS')")
  @PostMapping("/new-matches")
  @Operation(summary = "Get list of persons matching new person user is attempting to create", tags = { PERSONS })
  public ListResponse<Person> getNewPersonMatches(@RequestBody Person person, HttpServletRequest request) {
    return personCreateService.getNewPersonMatches(person, request);
  }

}
