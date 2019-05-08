package com.omb.ocpp.server;

import eu.chargetime.ocpp.model.SessionInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public interface SessionsListener {
    void onSessionsCountChange(Map<UUID, SessionInformation> sessions);
}

class StubSessionListener implements SessionsListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StubSessionListener.class);

    @Override
    public void onSessionsCountChange(Map<UUID, SessionInformation> sessions) {
        LOGGER.warn("Sessions count changed, size: {}, no action performed", sessions.size());
    }
}
