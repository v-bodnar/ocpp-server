package com.omb.ocpp.gui;

import com.omb.ocpp.config.Config;
import com.omb.ocpp.config.ConfigKey;
import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.SslContextConfig;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static final String OCPP_SERVER_HOME = System.getenv("OCPP_SERVER_HOME");

    public static final Application APPLICATION = new Application();

    private final ServiceLocator applicationContext = ServiceLocatorUtilities.bind(new ApplicationBinder());

    @Inject
    private GroovyService groovyService;

    @Inject
    private OcppServerService ocppServerService;

    @Inject
    private WebServer webServer;

    @Inject
    private KeystoreApi keystoreApi;

    @Inject
    private Config config;

    public Application() {
        applicationContext.inject(this);
    }

    public static void main(String[] args) {
        try {
            createRootFolder();
            APPLICATION.start();
        } catch (Exception e) {
            LOGGER.error("Critical error, closing app", e);
            System.exit(0);
        }
    }

    private static void createRootFolder() {
        if (OCPP_SERVER_HOME == null) {
            LOGGER.error("Please define OCPP_SERVER_HOME environment variable, closing app...");
            return;
        } else if (!Files.exists(Path.of(OCPP_SERVER_HOME))) {
            try {
                LOGGER.info("Creating {} folder", OCPP_SERVER_HOME);
                Files.createDirectories(Path.of(OCPP_SERVER_HOME));
            } catch (IOException e) {
                LOGGER.error(String.format("Error during %s creation", OCPP_SERVER_HOME), e);
            }
        }
    }

    private void start() throws Exception {
        if (config.getBoolean(ConfigKey.GUI_MODE)) {
            GuiApplication.main(new String[0]);
        } else {
            startNoGui();
        }
    }

    private void startNoGui() throws Exception {
        groovyService.loadGroovyScripts();

        String host = config.getString(ConfigKey.OCPP_SERVER_IP);
        int ocppPort = config.getInt(ConfigKey.OCPP_SERVER_PORT);
        int restPort = config.getInt(ConfigKey.REST_API_PORT);
        boolean sslEnabled = config.getBoolean(ConfigKey.SSL_ENABLED);

        LOGGER.info("Starting server in no GUI mode, host:{}, ocppPort: {}, restPort: {}, sslEnabled: {}", host,
                ocppPort, restPort,
                sslEnabled);

        if (sslEnabled) {
            UUID keystoreUUID = UUID.fromString(config.getString(ConfigKey.SSL_KEYSTORE_UUID));
            SslContextConfig sslContextConfig =
                    new SslContextConfig()
                            .setSslContext(keystoreApi.initializeSslContext(keystoreUUID))
                            .setCiphers(new ArrayList<>(config.getStringCollection(ConfigKey.SSL_KEYSTORE_CIPHERS)))
                            .setClientAuthenticationNeeded(config.getBoolean(ConfigKey.SSL_CLIENT_AUTH));
            ocppServerService.setSslContextConfig(sslContextConfig);
        }

        ocppServerService.start(host, ocppPort);

        try {
            webServer.startServer(restPort);
        } catch (Exception e) {
            LOGGER.error("Can't start REST server", e);
        }
    }

    public ServiceLocator getApplicationContext() {
        return applicationContext;
    }

    public <T> T getService(Class<T> clazz) {
        return getApplicationContext().getService(clazz);
    }
}
