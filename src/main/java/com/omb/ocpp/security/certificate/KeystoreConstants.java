package com.omb.ocpp.security.certificate;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.omb.ocpp.gui.Application.LITHOS_HOME;

public class KeystoreConstants {
    public static final Path KEYSTORE_ROOT_FOLDER_PATH = Paths.get(LITHOS_HOME, "ocpp", "ssl");
    public static final Path KEYSTORE_CERTIFICATE_CONFIG_PATH = Paths.get(KEYSTORE_ROOT_FOLDER_PATH.toString(), "keystore-certificates.config");
    public static final Path TRUST_STORE_PATH = Paths.get(KEYSTORE_ROOT_FOLDER_PATH.toString(), "trust-store.jks");
    public static final String TRUST_STORE_UUID = "c3b9ddc3-5fd9-4875-a536-366d8490c096";
    public static final String OCPP_SERVER_PRIVATE_KEY = "OCPP_SERVER_PRIVATE_KEY";
    public static final String OCPP_SERVER_CERT = "OCPP_SERVER_CERT";
}
