package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.Get15118EVCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.Get15118EVCertificateResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class Get15118EVCertificateFeature extends ProfileFeature {
    public Get15118EVCertificateFeature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return Get15118EVCertificateRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return Get15118EVCertificateResponse.class;
    }

    @Override
    public String getAction() {
        return "Get15118EVCertificate";
    }
}
