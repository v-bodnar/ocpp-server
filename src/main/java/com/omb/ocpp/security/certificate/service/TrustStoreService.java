package com.omb.ocpp.security.certificate.service;

import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.security.certificate.config.KeystoreConfigRegistry;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.omb.ocpp.security.certificate.KeystoreConstants.TRUST_STORE_PATH;
import static com.omb.ocpp.security.certificate.KeystoreConstants.TRUST_STORE_UUID;

@Service
public class TrustStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrustStoreService.class);

    private final KeystoreApi keystoreApi;
    private KeystoreCertificateConfig trustStoreConfig;
    private KeyStore trustStore;
    private Consumer<Void> certChangeListener = aVoid -> LOGGER.debug("No listeners attached");

    @Inject
    public TrustStoreService(KeystoreApi keystoreApi) {
        this.keystoreApi = keystoreApi;
    }

    @PostConstruct
    public void initialize() {
        if (!TRUST_STORE_PATH.toFile().exists()) {
            createTrustStore();
        } else {
            loadTruststore();
        }

        System.setProperty("javax.net.ssl.keyStore", TRUST_STORE_PATH.toAbsolutePath().toString());
    }

    public void addClientCertificate(X509Certificate certificate) {
        try (OutputStream os = Files.newOutputStream(TRUST_STORE_PATH)) {
            String alias = String.format("%s - %s", certificate.getIssuerDN(), certificate.getNotBefore());
            trustStore.setCertificateEntry(alias, certificate);
            trustStore.store(os, trustStoreConfig.getKeystorePassword().toCharArray());
            certChangeListener.accept(null);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            LOGGER.error("Could not add certificate to TrustStore");
        }
    }

    public void addClientCertificate(InputStream inputStream) {
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(inputStream);
            addClientCertificate(cer);
        } catch (CertificateException e) {
            LOGGER.debug("Could not save client certificate", e);
        }
    }

    public void deleteClientCertificate(X509Certificate certificate) {
        try (OutputStream os = Files.newOutputStream(TRUST_STORE_PATH)) {
            String alias = String.format("%s - %s", certificate.getIssuerDN(), certificate.getNotBefore());
            trustStore.deleteEntry(alias);
            trustStore.store(os, trustStoreConfig.getKeystorePassword().toCharArray());
            certChangeListener.accept(null);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            LOGGER.error("Could not delete certificate from TrustStore");
        }
    }

    public void deleteClientCertificate(String alias) {
        try (OutputStream os = Files.newOutputStream(TRUST_STORE_PATH)) {
            trustStore.deleteEntry(alias);
            trustStore.store(os, trustStoreConfig.getKeystorePassword().toCharArray());
            certChangeListener.accept(null);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            LOGGER.error("Could not delete certificate from TrustStore");
        }
    }

    public String getCertificateAsPem(X509Certificate certificate) {
        try (StringWriter writer = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(certificate);
            pemWriter.flush();
            return writer.toString();
        } catch (IOException e) {
            LOGGER.error("Could not get certificate from TrustStore");
            return "";
        }
    }

    public Set<X509Certificate> getClientCertificates() {
        Set<X509Certificate> certificates = new HashSet<>();
        try {
            Enumeration<String> aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                Certificate certificate = trustStore.getCertificate(aliases.nextElement());
                if (certificate instanceof X509Certificate) {
                    certificates.add((X509Certificate) certificate);
                }
            }
        } catch (KeyStoreException e) {
            LOGGER.error("Could not get certificate from TrustStore");
        }
        return certificates;
    }

    public Optional<List<String>> listAliasses() {
        try {
            return Optional.ofNullable(Collections.list(trustStore.aliases()));
        } catch (KeyStoreException e) {
            LOGGER.error("Could not get certificate from TrustStore");
            return Optional.empty();
        }
    }

    private void createTrustStore() {
        try {
            Files.createFile(TRUST_STORE_PATH);
            KeystoreCertificateConfig keystoreCertificateConfig = new KeystoreCertificateConfig.Builder()
                    .setKeystorePath(TRUST_STORE_PATH.toString())
                    .setKeystorePassword(UUID.randomUUID().toString())
                    .setUuid(UUID.fromString(TRUST_STORE_UUID))
                    .setKeystoreProtocol("TLSv1.2")
                    .build();

            KeystoreConfigRegistry keystoreCertificatesConfig = keystoreApi.getKeystoreConfigRegistry();
            keystoreCertificatesConfig.addKeystoreCertificateConfig(keystoreCertificateConfig);
            keystoreCertificatesConfig.persist();
            this.trustStoreConfig = keystoreCertificateConfig;

            try (OutputStream os = Files.newOutputStream(TRUST_STORE_PATH)) {
                trustStore = KeyStore.getInstance("JKS");
                trustStore.load(null, null);
                trustStore.store(os, keystoreCertificateConfig.getKeystorePassword().toCharArray());
            }
        } catch (Exception e) {
            LOGGER.error("could not create trust store", e);
        }
    }

    private void loadTruststore() {
        try {
            trustStore = keystoreApi.getTrustStore();
            trustStoreConfig = keystoreApi.getTrustStoreCertificateConfig();
        } catch (Exception e) {
            LOGGER.error("could not load TrustStore");
        }
    }

    public void setCertChangeListener(Consumer<Void> certChangeListener) {
        this.certChangeListener = certChangeListener;
    }
}