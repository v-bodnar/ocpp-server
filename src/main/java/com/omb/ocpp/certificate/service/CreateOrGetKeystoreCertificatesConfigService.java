package com.omb.ocpp.certificate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.certificate.KeystoreConstants;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;

import java.nio.file.Files;

public class CreateOrGetKeystoreCertificatesConfigService {

    public KeystoreCertificatesConfig execute() throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        createConfigIfNotExists(gson);
        return gson.fromJson(Files.readString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH), KeystoreCertificatesConfig.class);
    }

    private void createConfigIfNotExists(Gson gson) throws Exception {
        if (Files.notExists(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH)) {
            KeystoreCertificatesConfig keystoreCertificatesConfig = new KeystoreCertificatesConfig();
            String configAsJson = gson.toJson(keystoreCertificatesConfig);
            Files.writeString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH, configAsJson);
        }
    }
}
