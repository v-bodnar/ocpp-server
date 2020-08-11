package com.omb.ocpp.server.iso15118.dto.certificate.signing;

import com.omb.ocpp.server.iso15118.SignCertificateFeatureOperator;
import com.omb.ocpp.server.iso15118.dto.certificate.signing.ionity.SignCertificateRequest;

public class SignCertificateRequestFactory {

    private SignCertificateRequestFactory() {
    }

    public static Class<? extends SignCertificateRequestSupport> create(SignCertificateFeatureOperator signCertificateFeatureOperator) {
        switch (signCertificateFeatureOperator) {
            case IONITY:
                return SignCertificateRequest.class;
            case ELAM:
                return com.omb.ocpp.server.iso15118.dto.certificate.signing.elam.SignCertificateRequest.class;
            default:
                throw new RuntimeException("Not defined for: " + signCertificateFeatureOperator);
        }
    }
}
