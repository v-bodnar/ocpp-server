package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.handler.ISO15118EventHandler;
import com.omb.ocpp.server.iso15118.dto.AuthorizeRequest;
import com.omb.ocpp.server.iso15118.dto.Get15118EVCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.SignCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.SignedUpdateFirmwareRequest;
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
        this.features.add(new AuthorizeFeature(this));
        this.features.add(new Get15118EVCertificateFeature(this));
        this.features.add(new CertificateSignedFeature(this));
        this.features.add(new SignCertificateFeature(this));
        this.features.add(new TriggerMessageFeature(this));
        this.features.add(new SignedUpdateFirmwareFeature(this));
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
        } else if (request instanceof Get15118EVCertificateRequest) {
            return this.eventHandler.handleGetCertificateRequest(uuid, (Get15118EVCertificateRequest) request);
        } else if (request instanceof SignCertificateRequest) {
            return this.eventHandler.handleSignCertificateRequest(uuid, (SignCertificateRequest) request);
        } else if (request instanceof SignedUpdateFirmwareRequest) {
            return this.eventHandler.handleSignedUpdateFirmwareRequest(uuid, (SignedUpdateFirmwareRequest) request);
        } else {
            LOGGER.error("Unknown message for custom Feature arrived");
            return null;
        }
    }
}
