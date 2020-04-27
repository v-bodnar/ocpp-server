package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.SignCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.SignCertificateResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class SignCertificateFeature extends ProfileFeature {
    public SignCertificateFeature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return SignCertificateRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return SignCertificateResponse.class;
    }

    @Override
    public String getAction() {
        return "SignCertificate";
    }
}
