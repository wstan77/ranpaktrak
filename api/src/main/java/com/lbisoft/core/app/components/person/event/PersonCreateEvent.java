package com.lbisoft.core.app.components.person.event;

import com.lbisoft.core.framework.user.HasUserId;

public class PersonCreateEvent extends PersonEvent {
    private static final long serialVersionUID = 7896366078239053222L;

    private final HasUserId user;

    public PersonCreateEvent(Object source, Integer personId, HasUserId user) {
        super(source, personId);
        this.user = user;
    }

    public HasUserId getUser() {
        return user;
    }
}