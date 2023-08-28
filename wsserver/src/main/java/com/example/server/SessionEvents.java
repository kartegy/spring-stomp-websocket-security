package com.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionEvents {

    final Logger logger = LogManager.getLogger(SessionEvents.class);

    @EventListener
    public void onDestroyed(HttpSessionDestroyedEvent sessionEvent) {
        logger.info("SESSION EVENT: " + sessionEvent.toString());
    }

    @EventListener
    public void onCreated(HttpSessionCreatedEvent sessionEvent) {
        logger.info("SESSION EVENT: " + sessionEvent.toString());
    }
}
