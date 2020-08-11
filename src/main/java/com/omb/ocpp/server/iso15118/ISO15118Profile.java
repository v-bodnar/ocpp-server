package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.config.Config;
import com.omb.ocpp.config.ConfigKey;
import com.omb.ocpp.server.handler.ISO15118EventHandler;
import com.omb.ocpp.server.iso15118.dto.AuthorizeRequest;
import com.omb.ocpp.server.iso15118.dto.Get15118EVCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.certificate.signing.SignCertificateRequestSupport;
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

    public ISO15118Profile(ISO15118EventHandler eventHandler, Config config) {
        this.features.add(new AuthorizeFeature(this));
        this.features.add(new Get15118EVCertificateFeature(this));

        OcppCertificateSignedSpecification certificateSignedSpecification = OcppCertificateSignedSpecification.valueOf(config.getString(ConfigKey.CERTIFICATE_SIGNED_SPEC_VERSION));
        if (certificateSignedSpecification == OcppCertificateSignedSpecification.OCPP_2_0) {
            this.features.add(new com.omb.ocpp.server.iso15118.spec_2_0.CertificateSignedFeature(this));
        }
        if (certificateSignedSpecification == OcppCertificateSignedSpecification.OCPP_2_0_1) {
            this.features.add(new com.omb.ocpp.server.iso15118.spec_2_0_1.CertificateSignedFeature(this));
        }

        this.features.add(new SignCertificateFeature(this, SignCertificateFeatureOperator.valueOf(config.getString(ConfigKey.SIGN_CERTIFICATE_FEATURE_OPERATOR))));
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
        } else if (request instanceof SignCertificateRequestSupport) {
            return this.eventHandler.handleSignCertificateRequest(uuid, (SignCertificateRequestSupport) request);
        } else if (request instanceof SignedUpdateFirmwareRequest) {
            return this.eventHandler.handleSignedUpdateFirmwareRequest(uuid, (SignedUpdateFirmwareRequest) request);
        } else {
            LOGGER.error("Unknown message for custom Feature arrived");
            return null;
        }
    }
}
