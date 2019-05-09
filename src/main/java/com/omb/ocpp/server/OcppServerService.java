package com.omb.ocpp.server;

import com.omb.ocpp.server.handler.CoreEventHandler;
import com.omb.ocpp.server.handler.FirmwareManagementEventHandler;
import com.omb.ocpp.server.handler.RemoteTriggerEventHandler;
import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.JSONConfiguration;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementProfile;
import eu.chargetime.ocpp.feature.profile.ClientRemoteTriggerProfile;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.wss.BaseWssFactoryBuilder;
import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.*;

import static com.omb.ocpp.gui.StubRequestsFactory.toJson;

@Service
public class OcppServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcppServerService.class);

    private static final String LITHOS_HOME = Optional.ofNullable(System.getenv("LITHOS_HOME")).orElse("/home/bmterra/lithos");
    private static final Path SSL_KEYSTORE_FOLDER = Paths.get(LITHOS_HOME, "ocpp", "ssl", "ssl.properties");

    private Map<UUID, SessionInformation> sessionList = new HashMap<>();
    private SessionsListener sessionsListener = new StubSessionListener();
    private ServerCoreProfile coreProfile;
    private Profile firmwareProfile;
    private Profile remoteTriggerProfile;

    @Inject
    public OcppServerService(FirmwareManagementEventHandler firmwareManagementEventHandler,
                             RemoteTriggerEventHandler remoteTriggerEventHandler, CoreEventHandler coreEventHandler) {
        this.coreProfile = new ServerCoreProfile(coreEventHandler);
        this.firmwareProfile = new ClientFirmwareManagementProfile(firmwareManagementEventHandler);
        this.remoteTriggerProfile = new ClientRemoteTriggerProfile(remoteTriggerEventHandler);
    }

    private JSONServer server;

    public void start(String ip, String port) {
        LOGGER.info("Starting OCPP Server");
        if (server != null) {
            LOGGER.warn("Server already created, no actions will be performed");
            return;
        }
        server = initializeJsonServer();
        server.addFeatureProfile(firmwareProfile);
        server.addFeatureProfile(remoteTriggerProfile);

        LOGGER.info("Ocpp server ip: {}, port: {}", ip, port);
        server.open(ip, Integer.parseInt(port), new ServerEvents() {
            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {
                // sessionIndex is used to send messages.
                LOGGER.debug(String.format("New session: %s information: %s", sessionIndex,
                        information.getIdentifier()));
                sessionList.put(sessionIndex, information);
                sessionsListener.onSessionsCountChange(sessionList);
            }

            @Override
            public void lostSession(UUID sessionIndex) {
                LOGGER.debug("Session {} lost connection", sessionIndex);
                sessionList.remove(sessionIndex);
                sessionsListener.onSessionsCountChange(sessionList);
            }
        });
    }

    public void stop() {
        server.close();
        sessionList.clear();
        sessionsListener.onSessionsCountChange(sessionList);
        server = null;
    }

    public boolean isRunning() {
        return server != null && !server.isClosed();
    }

    public void send(Request request, String sessionToken) {
        String identifier = sessionToken.split(" ")[0];
        String address = sessionToken.split(" ")[1]
                .replace("(", "").replace(")", "");

        Optional<UUID> sessionUUID = sessionList.entrySet().stream()
                .filter(entry -> entry.getValue().getIdentifier().equals(identifier)
                        && entry.getValue().getAddress().toString().equals(address))
                .map(Map.Entry::getKey)
                .findAny();

        try {
            LOGGER.debug("Sending message: {} to {}", toJson(request), sessionToken);
            server.send(sessionUUID.orElseThrow(() -> new IllegalArgumentException(String.format("Could not find " +
                    "client by session token: %s", sessionToken))), request);
        } catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException e) {
            LOGGER.error("Could not send message: {} to {}", toJson(request), sessionToken);
        }

    }

    public void sendToAll(Request request) throws NotConnectedException, OccurenceConstraintException, UnsupportedFeatureException {
        for (Map.Entry<UUID, SessionInformation> entry : sessionList.entrySet()) {
            server.send(entry.getKey(), request);
        }
    }

    public Map<UUID, SessionInformation> getSessionList() {
        return sessionList;
    }

    public void setSessionsListener(SessionsListener sessionsListener) {
        this.sessionsListener = sessionsListener;
    }

    public Optional<SessionInformation> getSessionInformation(UUID sessionUuid) {
        return Optional.ofNullable(sessionList.get(sessionUuid));
    }

    private JSONServer initializeJsonServer() {
        try {
            if (hasSslKeystoreConfig()) {
                SslKeystoreConfig sslKeystoreConfig = getSslKeystoreConfig();
                SSLContext sslContext = initializeSslContextWithKeystore(sslKeystoreConfig);
                WssFactoryBuilder wssFactoryBuilder = BaseWssFactoryBuilder.builder().ciphers(sslKeystoreConfig.getKeystoreCiphers()).sslContext(sslContext);
                return new JSONServer(coreProfile, wssFactoryBuilder, JSONConfiguration.get());
            }
            return new JSONServer(coreProfile);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }
    }

    private boolean hasSslKeystoreConfig() {
        return SSL_KEYSTORE_FOLDER.toFile().exists();
    }

    public SslKeystoreConfig getSslKeystoreConfig() throws IOException {
        File keystoreFile = SSL_KEYSTORE_FOLDER.toFile();
        try (InputStream is = new FileInputStream(keystoreFile)) {
            Properties properties = new Properties();
            properties.load(is);
            return SslKeystoreConfig.loadFromProperties(properties);
        }
    }

    private SSLContext initializeSslContextWithKeystore(SslKeystoreConfig sslKeystoreConfig) throws Exception {

        SSLContext context = SSLContext.getInstance(sslKeystoreConfig.getKeystoreProtocol());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        KeyStore keyStore = KeyStore.getInstance("JKS");

        try (InputStream is = new FileInputStream(sslKeystoreConfig.getKeystorePath().toFile().getPath())) {
            keyStore.load(is, sslKeystoreConfig.getKeystorePassword().toCharArray());
        }

        keyManagerFactory.init(keyStore, sslKeystoreConfig.getKeystorePassword().toCharArray());
        context.init(keyManagerFactory.getKeyManagers(), null, null);

        return context;
    }
}