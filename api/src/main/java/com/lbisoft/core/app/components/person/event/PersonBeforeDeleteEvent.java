package com.lbisoft.core.app.components.person.event;

public class PersonBeforeDeleteEvent extends PersonEvent {
  private static final long serialVersionUID = -6790106475443485479L;

  public PersonBeforeDeleteEvent(Object source, Integer personId) {
    super(source, personId);
  }
}