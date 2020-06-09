package com.omb.ocpp.server.iso15118.spec_2_0;

import com.omb.ocpp.server.iso15118.dto.CertificateSignedResponse;
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
        return com.omb.ocpp.server.iso15118.dto.spec_2_0.CertificateSignedRequest.class;
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
