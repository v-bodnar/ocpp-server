package com.omb.ocpp.certificate.service;

import com.omb.ocpp.certificate.api.KeystoreApi;
import com.omb.ocpp.certificate.config.KeystoreCertificateConfig;

import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.util.*;
import java.util.stream.Collectors;

public class GetKeyStoreDetailsService {

    private KeystoreApi keystoreApi;

    public GetKeyStoreDetailsService(KeystoreApi keystoreApi) {
        this.keystoreApi = Objects.requireNonNull(keystoreApi);
    }

    public List<KeyStore> execute() throws Exception {
        List<UUID> keystoreUUIDs = keystoreApi.
                getKeystoreCertificatesConfig().
                getKeystoreCertificatesConfig().
                stream().
                map(config -> config.getUuid()).
                collect(Collectors.toList());

        return execute(keystoreUUIDs);
    }

    public List<KeyStore> execute(List<UUID> keystoreUUIDs) throws Exception {
        List<KeyStore> keyStoreList = new ArrayList<>();
        for (UUID keystoreUUID : keystoreUUIDs) {
            keyStoreList.add(execute(keystoreUUID));
        }
        return Collections.unmodifiableList(keyStoreList);
    }

    public KeyStore execute(UUID keystoreUUID) throws Exception {
        KeystoreCertificateConfig keystoreCertificateConfig = keystoreApi.getKeystoreCertificateConfig(keystoreUUID);
        return loadKeyStore(keystoreCertificateConfig);
    }

    private KeyStore loadKeyStore(KeystoreCertificateConfig config) throws Exception {
        KeyStore keyStoreLocal = KeyStore.getInstance("JKS");
        try (InputStream is = Files.newInputStream(config.getKeystorePath())) {
            keyStoreLocal.load(is, config.getKeystorePassword().toCharArray());
        }
        return keyStoreLocal;
    }
}
