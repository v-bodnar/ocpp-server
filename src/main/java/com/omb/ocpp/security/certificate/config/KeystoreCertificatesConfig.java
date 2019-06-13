package com.omb.ocpp.security.certificate.config;

import java.util.*;

public class KeystoreCertificatesConfig {

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

    public List<KeystoreCertificateConfig> getKeystoreCertificatesConfig() {
        return Collections.unmodifiableList(keystoreCertificatesConfig);
    }
}
