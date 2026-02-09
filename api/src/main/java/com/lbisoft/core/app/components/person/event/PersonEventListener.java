package com.lbisoft.core.app.components.person.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PersonEventListener implements ApplicationListener<PersonEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(PersonEvent event) {
        LOGGER.info("Received person event - " + event.getPersonId());
    }
}