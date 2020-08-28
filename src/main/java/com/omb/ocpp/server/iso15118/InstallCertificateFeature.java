package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.InstallCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.InstallCertificateResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class InstallCertificateFeature extends ProfileFeature {

    public InstallCertificateFeature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return InstallCertificateRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return InstallCertificateResponse.class;
    }

    @Override
    public String getAction() {
        return "InstallCertificate";
    }
}
