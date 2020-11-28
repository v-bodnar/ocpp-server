package com.omb.ocpp.server.security.spec16ed2.sign.certificate.feature;

import com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto.SignCertificateRequest;
import com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto.SignCertificateResponse;
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
