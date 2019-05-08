package com.omb.ocpp.server;

import com.omb.ocpp.server.handler.CoreEventHandler;
import com.omb.ocpp.server.handler.FirmwareManagementEventHandler;
import com.omb.ocpp.server.handler.RemoteTriggerEventHandler;
import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementProfile;
import eu.chargetime.ocpp.feature.profile.ClientRemoteTriggerProfile;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.omb.ocpp.gui.StubRequestsFactory.toJson;

public class OcppServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OcppServerService.class);
    private ServerCoreProfile core = new ServerCoreProfile(new CoreEventHandler());
    private Map<UUID, SessionInformation> sessionList = new HashMap<>();
    private Profile firmwareProfile = new ClientFirmwareManagementProfile(new FirmwareManagementEventHandler());
    private Profile remoteTriggerProfile = new ClientRemoteTriggerProfile(new RemoteTriggerEventHandler());
    private SessionsListener sessionsListener = new StubSessionListener();

    private JSONServer server;

    public void start(String ip, String port) {
        LOGGER.info("Starting OCPP Server");
        if (server != null) {
            LOGGER.warn("Server already created, no actions will be performed");
            return;
        }
        server = new JSONServer(core);
        server.addFeatureProfile(firmwareProfile);
        server.addFeatureProfile(remoteTriggerProfile);

        LOGGER.info("Ocpp server ip: {}, port: {}", ip, port);
        server.open(ip, Integer.parseInt(port), new ServerEvents() {
            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {
                // sessionIndex is used to send messages.
                LOGGER.debug("New session " + sessionIndex + ": " + information.getIdentifier());
                sessionList.put(sessionIndex, information);
                sessionsListener.onSessionsCountChange(sessionList);
            }

            @Override
            public void lostSession(UUID sessionIndex) {
                LOGGER.debug("Session " + sessionIndex + " lost connection");
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
                .map(uuidSessionInformationEntry -> uuidSessionInformationEntry.getKey())
                .findAny();
        if (!sessionUUID.isPresent()) {
            LOGGER.error("Could not find client by session token: {}", sessionToken);
            return;
        }

        try {
            LOGGER.debug("Sending message: {} to {}", toJson(request), sessionToken);
            server.send(sessionUUID.get(), request);
        } catch (OccurenceConstraintException e) {
            e.printStackTrace();
        } catch (UnsupportedFeatureException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
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

    public Optional<SessionInformation> getSessionInformation(UUID sessionUuid){
        return Optional.ofNullable(sessionList.get(sessionUuid));
    }
}