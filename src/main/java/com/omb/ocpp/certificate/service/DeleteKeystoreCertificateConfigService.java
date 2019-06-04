package com.omb.ocpp.certificate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.certificate.KeystoreConstants;
import com.omb.ocpp.certificate.api.KeystoreApi;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;

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

    public boolean execute() throws Exception {
        KeystoreCertificatesConfig keystoreCertificatesConfig = keystoreApi.getKeystoreCertificatesConfig();
        if (keystoreCertificatesConfig.deleteKeystoreCertificateConfig(keystoreUUID)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writeConfigToFile(gson, keystoreCertificatesConfig);
            return true;
        }
        return false;
    }

    private void writeConfigToFile(Gson gson, KeystoreCertificatesConfig keystoreCertificatesConfig) throws Exception {
        String configAsJson = gson.toJson(keystoreCertificatesConfig);
        Files.writeString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH, configAsJson);
    }
}
