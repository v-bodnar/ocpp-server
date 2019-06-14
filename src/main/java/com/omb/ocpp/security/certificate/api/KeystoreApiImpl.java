package com.omb.ocpp.security.certificate.api;

import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.security.certificate.config.KeystoreConfigRegistry;
import com.omb.ocpp.security.certificate.service.CreateKeystoreCertificateService;
import com.omb.ocpp.security.certificate.service.CreateOrGetKeystoreCertificatesConfigService;
import com.omb.ocpp.security.certificate.service.DeleteKeystoreCertificateConfigService;
import com.omb.ocpp.security.certificate.service.GetKeyStoreDetailsService;
import com.omb.ocpp.security.certificate.service.InitializeSslContextService;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import javax.net.ssl.SSLContext;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.omb.ocpp.security.certificate.KeystoreConstants.OCPP_SERVER_CERT;

public class KeystoreApiImpl implements KeystoreApi {

    @Override
    public synchronized KeystoreCertificateConfig createKeystoreCertificate() throws Exception {
        CreateKeystoreCertificateService service = new CreateKeystoreCertificateService(this);
        return service.execute();
    }

    @Override
    public synchronized KeystoreConfigRegistry getKeystoreConfigRegistry() throws Exception {
        CreateOrGetKeystoreCertificatesConfigService service = new CreateOrGetKeystoreCertificatesConfigService();
        return service.execute();
    }

    @Override
    public synchronized KeystoreCertificateConfig getKeystoreCertificateConfig(UUID keystoreUUID) throws Exception {
        return getKeystoreConfigRegistry().getKeystoreCertificateConfig(keystoreUUID);
    }

    @Override
    public synchronized KeystoreCertificateConfig getTrustStoreCertificateConfig() throws Exception {
        return getKeystoreConfigRegistry().getTrustStoreConfig();
    }

    @Override
    public synchronized void deleteKeystoreCertificate(UUID keystoreUUID) throws Exception {
        DeleteKeystoreCertificateConfigService service = new DeleteKeystoreCertificateConfigService(this, keystoreUUID);
        service.execute();
    }

    @Override
    public List<KeyStore> getKeyStores() throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getKeyStores();
    }

    @Override
    public synchronized KeyStore getKeyStores(UUID keystoreUUID) throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getKeyStores(keystoreUUID);
    }

    @Override
    public synchronized List<KeyStore> getKeyStores(List<UUID> keystoreUUIDs) throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getKeyStores(keystoreUUIDs);
    }

    @Override
    public synchronized KeyStore getTrustStore() throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getTrustStore();
    }

    @Override
    public SSLContext initializeSslContext(UUID keystoreUUID) throws Exception {
        InitializeSslContextService service = new InitializeSslContextService(this, keystoreUUID);
        return service.execute();
    }

    @Override
    public Set<X509Certificate> getAllServerCertificates() throws Exception {
        return getKeyStores().stream().map(keyStore -> {
            try {
                Certificate certificate = keyStore.getCertificate(OCPP_SERVER_CERT);
                if (certificate instanceof X509Certificate) {
                    return (X509Certificate) certificate;
                } else {
                    throw new CertificateParsingException("Unsupported certificate type, must be X509Certificate");
                }
            } catch (KeyStoreException | CertificateParsingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public synchronized X509Certificate getServerCertificate(UUID keystoreUUID) throws Exception {
        Certificate certificate = getKeyStores(keystoreUUID).getCertificate(OCPP_SERVER_CERT);
        if (certificate instanceof X509Certificate) {
            return (X509Certificate) certificate;
        } else {
            throw new CertificateParsingException("Unsupported certificate type, must be X509Certificate");
        }
    }

    @Override
    public String getServerCertificatePem(UUID keystoreUUID) throws Exception {
        X509Certificate certificate = getServerCertificate(keystoreUUID);
        try (StringWriter writer = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(certificate);
            pemWriter.flush();
            return writer.toString();
        }

    }

    @Override
    public synchronized UUID getKeyStoreUUIDByCertificate(Certificate certificate) throws Exception {
        for (KeystoreCertificateConfig keyStoreConfig : getKeystoreConfigRegistry().getKeystoreCertificatesConfig()) {
            KeyStore keyStore = getKeyStores(keyStoreConfig.getUuid());
            if (keyStore.getCertificateAlias(certificate) != null) {
                return keyStoreConfig.getUuid();
            }
        }
        throw new KeyStoreException("Certificate not found");
    }
}
