package com.omb.ocpp.security.certificate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.security.certificate.KeystoreConstants;
import com.omb.ocpp.security.certificate.config.KeystoreConfigRegistry;

import java.io.IOException;
import java.nio.file.Files;

public class CreateOrGetKeystoreCertificatesConfigService {

    public KeystoreConfigRegistry execute() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        createConfigIfNotExists();
        return gson.fromJson(Files.readString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH), KeystoreConfigRegistry.class);
    }

    private void createConfigIfNotExists() throws IOException {
        if (Files.notExists(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH)) {
            KeystoreConfigRegistry keystoreConfigRegistry = new KeystoreConfigRegistry();
            keystoreConfigRegistry.persist();
        }
    }
}
