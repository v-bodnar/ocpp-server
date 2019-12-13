package com.omb.ocpp.server;

import com.omb.ocpp.config.Config;
import com.omb.ocpp.config.ConfigKey;
import com.omb.ocpp.security.BaseWssFactoryBuilderWrapper;
import com.omb.ocpp.server.handler.CoreEventHandler;
import com.omb.ocpp.server.handler.ISO15118EventHandler;
import com.omb.ocpp.server.handler.FirmwareManagementEventHandler;
import com.omb.ocpp.server.iso15118.ISO15118Profile;
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
import eu.chargetime.ocpp.feature.profile.ServerSmartChargingProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
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
    private Profile firmwareProfile;
    private Profile remoteTriggerProfile;
    private Profile localAuthListProfile;
    private Profile iso15118Profile;
    private Profile smartChargingProfile;
    private SslContextConfig sslContextConfig;
    private Config config;

    @Inject
    public OcppServerService(FirmwareManagementEventHandler firmwareManagementEventHandler,
                             CoreEventHandler coreEventHandler, ISO15118EventHandler iso15118EventHandler, Config config) {
        this.config = config;
        this.coreProfile = new ServerCoreProfile(coreEventHandler);
        this.firmwareProfile = new ServerFirmwareManagementProfile(firmwareManagementEventHandler);
        this.remoteTriggerProfile = new ServerRemoteTriggerProfile();
        this.localAuthListProfile = new ServerLocalAuthListProfile();
        this.smartChargingProfile = new ServerSmartChargingProfile();
        this.iso15118Profile = new ISO15118Profile(iso15118EventHandler);
    }

    public void start(String ip, int port) {
        LOGGER.info("Starting OCPP Server ip: {}, port: {}", ip, port);
        if (server != null) {
            LOGGER.warn("Server already created, no actions will be performed");
            return;
        }
        if (sslContextConfig != null) {
            server = initializeJsonSslServer();
        } else {
            server = initializeJsonServer();
        }
        Collection<String> featuresList = config.getStringCollection(ConfigKey.OCPP_FEATURES_PROFILE_LIST);

        server.addFeatureProfile(coreProfile);

        if(featuresList.contains(Feature.FIRMWARE_MANAGEMENT.getKey())) {
            server.addFeatureProfile(firmwareProfile);
        }
        if(featuresList.contains(Feature.REMOTE_TRIGGER.getKey())) {
            server.addFeatureProfile(remoteTriggerProfile);
        }
        if(featuresList.contains(Feature.LOCAL_AUTH_LIST.getKey())) {
            server.addFeatureProfile(localAuthListProfile);
        }
        if(featuresList.contains(Feature.SMART_CHARGING.getKey())) {
            server.addFeatureProfile(smartChargingProfile);
        }
        if(featuresList.contains(Feature.ISO_15118.getKey())) {
            server.addFeatureProfile(iso15118Profile);
        }

        server.open(ip, port, new ServerEvents() {
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
            HandshakeResolver handshakeResolver = new HandshakeResolver(config.getString(ConfigKey.OCPP_AUTH_PASSWORD));
            return new JSONServer(coreProfile, handshakeResolver);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }
    }

    private JSONServer initializeJsonSslServer() {
        try {
            HandshakeResolver handshakeResolver = new HandshakeResolver(config.getString(ConfigKey.OCPP_AUTH_PASSWORD));
            WssFactoryBuilder wssFactoryBuilder = new BaseWssFactoryBuilderWrapper().
                    setCiphers(sslContextConfig.getCiphers()).
                    setClientAuthenticationNeeded(sslContextConfig.isClientAuthenticationNeeded()).
                    setSslContext(sslContextConfig.getSslContext());
            return new JSONServer(coreProfile, wssFactoryBuilder, JSONConfiguration.get(), handshakeResolver);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }
    }

    public void setSslContextConfig(SslContextConfig sslContextConfig) {
        this.sslContextConfig = sslContextConfig;
    }

    public SslContextConfig getSslContextConfig() {
        return sslContextConfig;
    }
}