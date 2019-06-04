package com.omb.ocpp.certificate.api;

import com.omb.ocpp.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;
import org.jvnet.hk2.annotations.Contract;

import java.security.KeyStore;
import java.util.List;
import java.util.UUID;

@Contract
public interface KeystoreApi {

    KeystoreCertificatesConfig getKeystoreCertificatesConfig() throws Exception;

    KeystoreCertificateConfig getKeystoreCertificateConfig(UUID keystoreUUID) throws Exception;

    KeystoreCertificateConfig createKeystoreCertificate() throws Exception;

    void deleteKeystoreCertificate(UUID keystoreUUID) throws Exception;

    List<KeyStore> getKeyStores() throws Exception;

    KeyStore getKeyStores(UUID keystoreUUID) throws Exception;

    List<KeyStore> getKeyStores(List<UUID> keystoreUUIDs) throws Exception;

}
