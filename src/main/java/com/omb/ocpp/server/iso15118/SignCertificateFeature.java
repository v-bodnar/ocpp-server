package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.certificate.signing.SignCertificateRequestFactory;
import com.omb.ocpp.server.iso15118.dto.SignCertificateResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

import java.util.Objects;

public class SignCertificateFeature extends ProfileFeature {

    private final SignCertificateFeatureOperator signCertificateFeatureOperator;

    public SignCertificateFeature(Profile ownerProfile, SignCertificateFeatureOperator signCertificateFeatureOperator) {
        super(ownerProfile);
        this.signCertificateFeatureOperator = Objects.requireNonNull(signCertificateFeatureOperator);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return SignCertificateRequestFactory.create(signCertificateFeatureOperator);
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
