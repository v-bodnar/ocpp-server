package com.omb.ocpp.server.iso15118.dto.certificate.signing;

import eu.chargetime.ocpp.model.Request;

public interface SignCertificateRequestSupport extends Request {

    String getCsr();

    CertificateSigningUseEnumTypeSupport getTypeOfCertificate();
}
