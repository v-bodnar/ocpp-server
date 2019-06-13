package com.omb.ocpp.security.certificate.api;

import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.security.certificate.config.KeystoreCertificatesConfig;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface KeystoreApi {

    KeystoreCertificatesConfig getKeystoreCertificatesConfig() throws Exception;

    KeystoreCertificateConfig getKeystoreCertificateConfig(UUID keystoreUUID) throws Exception;

    KeystoreCertificateConfig createKeystoreCertificate() throws Exception;

    void deleteKeystoreCertificate(UUID keystoreUUID) throws Exception;

    List<KeyStore> getKeyStores() throws Exception;

    KeyStore getKeyStores(UUID keystoreUUID) throws Exception;

    List<KeyStore> getKeyStores(List<UUID> keystoreUUIDs) throws Exception;

    SSLContext initializeSslContext(UUID keystoreUUID) throws Exception;

    Set<X509Certificate> getAllServerCertificates() throws Exception;

    X509Certificate getServerCertificate(UUID keystoreUUID) throws Exception;

    String getServerCertificatePem(UUID keystoreUUID) throws Exception;

    UUID getKeyStoreUUIDByCertificate(Certificate certificate) throws Exception;
}
