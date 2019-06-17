package com.omb.ocpp.security.certificate.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.security.certificate.KeystoreConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.omb.ocpp.security.certificate.KeystoreConstants.TRUST_STORE_UUID;

public class KeystoreConfigRegistry {

    private final List<KeystoreCertificateConfig> keystoreCertificatesConfig = new ArrayList<>();

    public KeystoreCertificateConfig deleteKeystoreCertificateConfig(UUID keystoreUUID) {
        Objects.requireNonNull(keystoreUUID);
        Optional<KeystoreCertificateConfig> keystoreCertificateConfigOpt = keystoreCertificatesConfig.stream().filter(e -> e.getUuid().equals(keystoreUUID)).findFirst();
        if (keystoreCertificateConfigOpt.isPresent()) {
            KeystoreCertificateConfig keystoreCertificateConfig = keystoreCertificateConfigOpt.get();
            keystoreCertificatesConfig.remove(keystoreCertificateConfig);
            return keystoreCertificateConfig;
        }
        throw new IllegalArgumentException(String.format("Keystore not found for UUID: %s", keystoreUUID.toString()));
    }

    public void addKeystoreCertificateConfig(KeystoreCertificateConfig keystoreCertificateConfig) {
        keystoreCertificatesConfig.add(keystoreCertificateConfig);
    }

    public KeystoreCertificateConfig getKeystoreCertificateConfig(UUID keystoreUUID) {
        return getKeystoreCertificatesConfig().
                stream().
                filter(e -> e.getUuid().equals(keystoreUUID)).
                findFirst().
                orElseThrow(() -> new IllegalArgumentException(String.format("Keystore not found for UUID: %s", keystoreUUID.toString())));
    }

    public KeystoreCertificateConfig getTrustStoreConfig() {
        return getKeystoreCertificatesConfig().
                stream().
                filter(e -> e.getUuid().equals(UUID.fromString(TRUST_STORE_UUID))).
                findFirst().
                orElseThrow(() -> new IllegalArgumentException(String.format("TrustStore not found for UUID: %s",
                        TRUST_STORE_UUID)));
    }

    public List<KeystoreCertificateConfig> getKeystoreCertificatesConfig() {
        return Collections.unmodifiableList(keystoreCertificatesConfig);
    }

    public void persist() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String configAsJson = gson.toJson(this);
        Files.writeString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH, configAsJson);
    }
}
