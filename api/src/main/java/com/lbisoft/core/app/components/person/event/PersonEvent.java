package com.lbisoft.core.app.components.person.event;

import org.springframework.context.ApplicationEvent;

public class PersonEvent extends ApplicationEvent {
    private static final long serialVersionUID = 7896366078239053222L;

    private final Integer personId;

    public PersonEvent(Object source, Integer personId) {
        super(source);
        this.personId = personId;
    }

    public Integer getPersonId() {
        return personId;
    }
}