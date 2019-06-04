package com.omb.ocpp.certificate.api;

import com.omb.ocpp.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;
import org.jvnet.hk2.annotations.Contract;

import java.util.UUID;

@Contract
public interface KeystoreApi {

    KeystoreCertificatesConfig getKeystoreCertificatesConfig() throws Exception;

    KeystoreCertificateConfig createKeystoreCertificate() throws Exception;

    Boolean deleteKeystoreCertificate(UUID keystoreUUID) throws Exception;
}
