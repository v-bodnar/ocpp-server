package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.handler.ISO15118EventHandler;
import com.omb.ocpp.server.iso15118.dto.AuthorizeRequest;
import eu.chargetime.ocpp.feature.Feature;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.UUID;

public class ISO15118Profile implements Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(ISO15118Profile.class);
    private ISO15118EventHandler eventHandler;
    private ArrayList<Feature> features = new ArrayList();

    public ISO15118Profile(ISO15118EventHandler eventHandler) {
        this.features.add(new ISO15118Feature(this));
        this.eventHandler = eventHandler;
    }

    @Override
    public ProfileFeature[] getFeatureList() {
        return this.features.toArray(new ProfileFeature[0]);
    }

    @Override
    public Confirmation handleRequest(UUID uuid, Request request) {
        if (request instanceof AuthorizeRequest) {
            return this.eventHandler.handleAuthorizeRequest(uuid, (AuthorizeRequest) request);
        } else {
            LOGGER.error("Unknown message for custom Feature arrived");
            return null;
        }
    }
}
