package com.omb.ocpp.certificate;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.omb.ocpp.gui.Application.LITHOS_HOME;

public class KeystoreConstants {

    public static final Path KEYSTORE_ROOT_FOLDER_PATH = Paths.get(LITHOS_HOME, "ocpp", "ssl");

    public static final Path KEYSTORE_CERTIFICATE_CONFIG_PATH = Paths.get(KEYSTORE_ROOT_FOLDER_PATH.toString(), "keystore-certificates.config");
}
