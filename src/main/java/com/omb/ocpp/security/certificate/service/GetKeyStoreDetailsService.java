package com.omb.ocpp.security.certificate.service;

import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.omb.ocpp.security.certificate.KeystoreConstants.TRUST_STORE_UUID;

public class GetKeyStoreDetailsService {

    private final KeystoreApi keystoreApi;

    public GetKeyStoreDetailsService(KeystoreApi keystoreApi) {
        this.keystoreApi = Objects.requireNonNull(keystoreApi);
    }

    public List<KeyStore> getKeyStores() throws Exception {
        List<UUID> keystoreUUIDs = keystoreApi.
                getKeystoreConfigRegistry().
                getKeystoreCertificatesConfig().
                stream().
                filter(keystoreCertificateConfig -> !keystoreCertificateConfig.getUuid().equals(UUID.fromString(TRUST_STORE_UUID))).
                map(KeystoreCertificateConfig::getUuid).
                collect(Collectors.toList());

        return getKeyStores(keystoreUUIDs);
    }

    public List<KeyStore> getKeyStores(List<UUID> keystoreUUIDs) throws Exception {
        List<KeyStore> keyStoreList = new ArrayList<>();
        for (UUID keystoreUUID : keystoreUUIDs) {
            keyStoreList.add(getKeyStores(keystoreUUID));
        }
        return Collections.unmodifiableList(keyStoreList);
    }

    public KeyStore getKeyStores(UUID keystoreUUID) throws Exception {
        KeystoreCertificateConfig keystoreCertificateConfig = keystoreApi.getKeystoreCertificateConfig(keystoreUUID);
        return loadKeyStore(keystoreCertificateConfig);
    }

    public KeyStore getTrustStore() throws Exception {
        KeystoreCertificateConfig keystoreCertificateConfig = keystoreApi.getTrustStoreCertificateConfig();
        return loadKeyStore(keystoreCertificateConfig);
    }

    private KeyStore loadKeyStore(KeystoreCertificateConfig config) throws KeyStoreException, IOException,
            CertificateException, NoSuchAlgorithmException {
        KeyStore keyStoreLocal = KeyStore.getInstance("JKS");
        try (InputStream is = Files.newInputStream(config.getKeystorePath())) {
            keyStoreLocal.load(is, config.getKeystorePassword().toCharArray());
        }
        return keyStoreLocal;
    }
}
