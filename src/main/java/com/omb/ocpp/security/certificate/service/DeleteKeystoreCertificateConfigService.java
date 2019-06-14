package com.omb.ocpp.security.certificate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.security.certificate.KeystoreConstants;
import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.security.certificate.config.KeystoreConfigRegistry;

import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

public class DeleteKeystoreCertificateConfigService {

    private final KeystoreApi keystoreApi;

    private final UUID keystoreUUID;

    public DeleteKeystoreCertificateConfigService(KeystoreApi keystoreApi, UUID keystoreUUID) {
        this.keystoreApi = keystoreApi;
        this.keystoreUUID = Objects.requireNonNull(keystoreUUID);
    }

    public void execute() throws Exception {
        KeystoreConfigRegistry keystoreConfigRegistry = keystoreApi.getKeystoreConfigRegistry();
        KeystoreCertificateConfig keystoreCertificateConfig = keystoreConfigRegistry.deleteKeystoreCertificateConfig(keystoreUUID);
        writeConfigToFile(keystoreConfigRegistry);
        Files.delete(keystoreCertificateConfig.getKeystorePath());
    }

    private void writeConfigToFile(KeystoreConfigRegistry keystoreConfigRegistry) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String configAsJson = gson.toJson(keystoreConfigRegistry);
        Files.writeString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH, configAsJson);
    }
}
