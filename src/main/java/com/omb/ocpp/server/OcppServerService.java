package com.omb.ocpp.server;

import com.omb.ocpp.security.BaseWssFactoryBuilderWrapper;
import com.omb.ocpp.server.handler.CoreEventHandler;
import com.omb.ocpp.server.handler.FirmwareManagementEventHandler;
import eu.chargetime.ocpp.JSONConfiguration;
import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementProfile;
import eu.chargetime.ocpp.feature.profile.ServerLocalAuthListProfile;
import eu.chargetime.ocpp.feature.profile.ServerRemoteTriggerProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.omb.ocpp.gui.StubRequestsFactory.toJson;

@Service
public class OcppServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcppServerService.class);

    private JSONServer server;
    private Map<UUID, SessionInformation> sessionList = new HashMap<>();
    private SessionsListener sessionsListener = new StubSessionListener();
    private ServerCoreProfile coreProfile;
    private SslKeyStoreConfig sslKeyStoreConfig;
    private Profile firmwareProfile;
    private Profile remoteTriggerProfile;
    private Profile localAuthListProfile;

    @Inject
    public OcppServerService(FirmwareManagementEventHandler firmwareManagementEventHandler,
                             CoreEventHandler coreEventHandler, SslKeyStoreConfig sslKeyStoreConfig) {
        this.coreProfile = new ServerCoreProfile(coreEventHandler);
        this.firmwareProfile = new ServerFirmwareManagementProfile(firmwareManagementEventHandler);
        this.remoteTriggerProfile = new ServerRemoteTriggerProfile();
        this.localAuthListProfile = new ServerLocalAuthListProfile();
        this.sslKeyStoreConfig = sslKeyStoreConfig;
    }

    public void start(String ip, String port, boolean sslEnabled) {
        LOGGER.info("Starting OCPP Server ip: {}, port: {}", ip, port);
        if (server != null) {
            LOGGER.warn("Server already created, no actions will be performed");
            return;
        }
        if (sslEnabled) {
            server = initializeJsonSslServer();
        } else {
            server = initializeJsonServer();
        }

        server.addFeatureProfile(firmwareProfile);
        server.addFeatureProfile(remoteTriggerProfile);
        server.addFeatureProfile(localAuthListProfile);

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
                    "client by session token: %s", sessionToken))), request)
                    .whenComplete((confirmation, throwable) -> {
                        if (throwable == null) {
                            LOGGER.debug("Client responded with: {}", confirmation);
                        } else {
                            LOGGER.error("Error parsing response from client", throwable);
                        }
                    });
        } catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException e) {
            LOGGER.error(String.format("Could not send message: %s to %s", toJson(request), sessionToken), e);
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
            return new JSONServer(coreProfile);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }
    }

    private JSONServer initializeJsonSslServer() {
        try {
            SSLContext sslContext = initializeSslContextWithKeystore(sslKeyStoreConfig);
            WssFactoryBuilder wssFactoryBuilder = BaseWssFactoryBuilderWrapper.builder(sslKeyStoreConfig).ciphers(sslKeyStoreConfig.getKeystoreCiphers()).sslContext(sslContext);
            return new JSONServer(coreProfile, wssFactoryBuilder, JSONConfiguration.get());
        } catch (Exception e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }
    }

    private SSLContext initializeSslContextWithKeystore(SslKeyStoreConfig keyStoreConfig) throws Exception {

        SSLContext context = SSLContext.getInstance(keyStoreConfig.getKeystoreProtocol());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        char[] password = keyStoreConfig.getKeystorePassword().toCharArray();
        KeyStore keyStore = keyStoreConfig.geKeyStore().orElseThrow(() -> new KeyStoreException("KeyStore not found"));
        keyManagerFactory.init(keyStore, password);

        context.init(keyManagerFactory.getKeyManagers(), null, null);

        return context;
    }
}