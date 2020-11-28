package com.omb.ocpp.server.security.spec16ed2.certificate.signed.feature;

import com.omb.ocpp.server.security.spec16ed2.certificate.signed.dto.CertificateSignedRequest;
import com.omb.ocpp.server.security.spec16ed2.certificate.signed.dto.CertificateSignedResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class CertificateSignedFeature extends ProfileFeature {

    public CertificateSignedFeature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return CertificateSignedRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return CertificateSignedResponse.class;
    }

    @Override
    public String getAction() {
        return "CertificateSigned";
    }
}
