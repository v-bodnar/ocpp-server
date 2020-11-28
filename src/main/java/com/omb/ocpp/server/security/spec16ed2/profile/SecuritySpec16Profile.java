package com.omb.ocpp.server.security.spec16ed2.profile;

import com.omb.ocpp.server.security.spec16ed2.certificate.signed.feature.CertificateSignedFeature;
import com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.feature.ExtendedTriggerMessageFeature;
import com.omb.ocpp.server.security.spec16ed2.handler.SecuritySpec16EventHandler;
import com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto.SignCertificateRequest;
import com.omb.ocpp.server.security.spec16ed2.sign.certificate.feature.SignCertificateFeature;
import eu.chargetime.ocpp.feature.Feature;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class SecuritySpec16Profile implements Profile {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecuritySpec16Profile.class);

    private final SecuritySpec16EventHandler securitySpec16EventHandler;
    private final List<Feature> features;

    public SecuritySpec16Profile(SecuritySpec16EventHandler securitySpec16EventHandler) {
        this.securitySpec16EventHandler = requireNonNull(securitySpec16EventHandler);
        this.features = createSecuritySpec16Features();
    }

    private List<Feature> createSecuritySpec16Features() {
        List<Feature> features = new ArrayList();
        features.add(new ExtendedTriggerMessageFeature(this));
        features.add(new SignCertificateFeature(this));
        features.add(new CertificateSignedFeature(this));
        return Collections.unmodifiableList(features);
    }

    @Override
    public ProfileFeature[] getFeatureList() {
        return this.features.toArray(new ProfileFeature[0]);
    }

    @Override
    public Confirmation handleRequest(UUID uuid, Request request) {
        if (request instanceof SignCertificateRequest) {
            return this.securitySpec16EventHandler.handleSignCertificateRequest(uuid, (SignCertificateRequest) request);
        } else {
            LOGGER.error("Unknown message for custom Feature arrived");
            return null;
        }
    }
}