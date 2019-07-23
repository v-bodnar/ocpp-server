package com.omb.ocpp.server;

import com.omb.ocpp.server.handler.CustomEventHandler;
import eu.chargetime.ocpp.feature.Feature;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.UUID;

public class CustomProfile implements Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomProfile.class);
    private CustomEventHandler eventHandler;
    private ArrayList<Feature> features = new ArrayList();

    public CustomProfile(CustomEventHandler eventHandler) {
        this.features.add(new CustomFeature(this));
        this.eventHandler = eventHandler;
    }

    @Override
    public ProfileFeature[] getFeatureList() {
        return this.features.toArray(new ProfileFeature[0]);
    }

    @Override
    public Confirmation handleRequest(UUID uuid, Request request) {
        if (request instanceof AuthorizeRequest) {
            return this.eventHandler.handleAuthorizeRequest((AuthorizeRequest) request);
        } else {
            LOGGER.error("Unknown message for custom Feature arrived");
            return null;
        }
    }
}
