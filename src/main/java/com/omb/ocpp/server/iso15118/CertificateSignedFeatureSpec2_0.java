package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.CertificateSignedRequestSpec2_0;
import com.omb.ocpp.server.iso15118.dto.CertificateSignedResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class CertificateSignedFeatureSpec2_0 extends ProfileFeature {

    public CertificateSignedFeatureSpec2_0(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return CertificateSignedRequestSpec2_0.class;
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
