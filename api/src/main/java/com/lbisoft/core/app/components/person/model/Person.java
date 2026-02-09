package com.lbisoft.core.app.components.person.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lbisoft.core.app.components.address.model.PersonAddress;
import com.lbisoft.core.app.components.email.model.PersonEmail;
import com.lbisoft.core.app.components.person.socialprofiles.model.PersonSocialProfile;
import com.lbisoft.core.app.components.person.xrefs.model.PersonXref;
import com.lbisoft.core.app.components.phone.model.PersonPhoneNumber;
import com.lbisoft.core.framework.response.ResponseObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Person extends ResponseObject {
  private Integer personId;
  private String recordStatusLk;
  private String personTypeLk;
  private String pronouns;
  private String firstName;
  private String legalFirstName;
  private String lastName;
  private String legalLastName;
  private String middleName;
  private String displayName;
  private LocalDate dateOfBirth;
  private String genderLk;
  private String primaryLanguageLk;
  private String secondaryLanguageLk;
  private Integer createUserId;
  private LocalDateTime createDate;
  private Integer lastChangeUserId;
  private LocalDateTime lastChangeDate;
  private LocalDateTime systemLastChangeDate;

  private List<PersonPhoneNumber> phoneNumbers = new ArrayList<>();
  private List<PersonEmail> emails = new ArrayList<>();
  private List<PersonAddress> addresses = new ArrayList<>();
  private List<PersonSocialProfile> socialProfiles = new ArrayList<>();
  private List<PersonXref> xrefs = new ArrayList<>();
}
