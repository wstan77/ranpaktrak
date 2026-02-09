package com.lbisoft.core.app.components.person.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PersonEventPublisher {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(final PersonEvent event) {
        LOGGER.info("Publishing person event. ");
        applicationEventPublisher.publishEvent(event);
    }
}