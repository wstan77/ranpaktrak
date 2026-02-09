package com.lbisoft.core.app.components.person.model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.Constants.ReferenceTypeLks;
import com.lbisoft.core.app.components.quicksearch.model.Taggable;
import com.lbisoft.core.framework.utils.DateHelper;
import com.lbisoft.core.framework.utils.NormalizeUtils;

public class QuickSearchPerson extends Person implements Taggable {
  @JsonIgnore
  private String positionName;

  public String getPositionName() {
    return positionName;
  }

  public void setPositionName(String positionName) {
    this.positionName = positionName;
  }

  @Override
  public Integer getId() {
    return getPersonId();
  }

  @Override
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public List<String> getDetails() {
    Double age = DateHelper.getAgeAsOf(this.getDateOfBirth(), LocalDate.now());
    String type = "Person";
    return Stream.of(type, age != null ? Integer.toString((int) Math.floor(age)) : null)
        .filter(t -> t != null).toList();
  }

  @Override
  public String getIcon() {
    return null;
  }

  @Override
  public boolean equals(Object person) {
    if (person == null || !(person instanceof QuickSearchPerson)) {
      return false;
    }

    return this.getId().equals(((QuickSearchPerson) person).getId());
  }

  @Override
  public String getType() {
    return ReferenceTypeLks.PERSON;
  }

  @Override
  public List<String> getSearchTerms() {
    return Stream.of(getFirstName(), getLastName(), getLegalFirstName(), getLegalLastName()).filter(s -> s != null)
        .flatMap(term -> NormalizeUtils.splitNameParts(term).stream())
        .toList();
  }

  @Override
  public boolean getActiveFlg() {
    return !Constants.RecordStatusLks.DELETED.equals(getRecordStatusLk());
  }

}
