package com.omb.ocpp.security.certificate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.security.certificate.KeystoreConstants;
import com.omb.ocpp.security.certificate.config.KeystoreConfigRegistry;

import java.nio.file.Files;

public class CreateOrGetKeystoreCertificatesConfigService {

    public KeystoreConfigRegistry execute() throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        createConfigIfNotExists(gson);
        return gson.fromJson(Files.readString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH), KeystoreConfigRegistry.class);
    }

    private void createConfigIfNotExists(Gson gson) throws Exception {
        if (Files.notExists(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH)) {
            KeystoreConfigRegistry keystoreConfigRegistry = new KeystoreConfigRegistry();
            keystoreConfigRegistry.persist();
        }
    }
}
