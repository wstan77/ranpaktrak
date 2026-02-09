package com.lbisoft.core.app.components.person.model;

import java.time.LocalDate;
import java.util.List;

import com.lbisoft.core.framework.models.AbstractSearchForm;

public class PersonSearchForm extends AbstractSearchForm {
  private String name;
  private LocalDate dateOfBirthFrom;
  private LocalDate dateOfBirthTo;
  private List<String> positionLks;
  private List<String> personTypeLks;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getDateOfBirthFrom() {
    return dateOfBirthFrom;
  }

  public void setDateOfBirthFrom(LocalDate dateOfBirthFrom) {
    this.dateOfBirthFrom = dateOfBirthFrom;
  }

  public LocalDate getDateOfBirthTo() {
    return dateOfBirthTo;
  }

  public void setDateOfBirthTo(LocalDate dateOfBirthTo) {
    this.dateOfBirthTo = dateOfBirthTo;
  }

  public List<String> getPositionLks() {
    return positionLks;
  }

  public void setPositionLks(List<String> positionLks) {
    this.positionLks = positionLks;
  }

  public List<String> getPersonTypeLks() {
    return personTypeLks;
  }

  public void setPersonTypeLks(List<String> personTypeLks) {
    this.personTypeLks = personTypeLks;
  }
}
