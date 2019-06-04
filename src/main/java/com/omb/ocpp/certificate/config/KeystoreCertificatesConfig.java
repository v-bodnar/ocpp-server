package com.omb.ocpp.certificate.config;

import java.util.*;

public class KeystoreCertificatesConfig {

    private final List<KeystoreCertificateConfig> keystoreCertificatesConfig = new LinkedList<>();

    public boolean deleteKeystoreCertificateConfig(UUID keystoreUUID) {
        Objects.requireNonNull(keystoreUUID);
        Optional<KeystoreCertificateConfig> keystoreCertificateConfigOpt = keystoreCertificatesConfig.stream().filter(e -> e.getUuid().equals(keystoreUUID)).findFirst();
        if (keystoreCertificateConfigOpt.isPresent()) {
            keystoreCertificatesConfig.remove(keystoreCertificateConfigOpt.get());
            return true;
        }
        return false;
    }

    public void addKeystoreCertificateConfig(KeystoreCertificateConfig... keystoreCertificatesConfig) {
        Objects.requireNonNull(keystoreCertificatesConfig);
        for (KeystoreCertificateConfig keystoreCertificateConfig : keystoreCertificatesConfig) {
            addKeystoreCertificateConfig(keystoreCertificateConfig);
        }
    }

    public void addKeystoreCertificateConfig(KeystoreCertificateConfig keystoreCertificateConfig) {
        keystoreCertificatesConfig.add(keystoreCertificateConfig);
    }

    public List<KeystoreCertificateConfig> getKeystoreCertificatesConfig() {
        return Collections.unmodifiableList(keystoreCertificatesConfig);
    }
}
