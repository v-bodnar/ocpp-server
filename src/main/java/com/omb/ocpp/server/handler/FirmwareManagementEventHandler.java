package com.omb.ocpp.server.handler;

import com.omb.ocpp.groovy.GroovyService;
import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementEventHandler;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static com.omb.ocpp.server.handler.CoreEventHandler.RECEIVED_REQUEST;

@Service
public class FirmwareManagementEventHandler implements ServerFirmwareManagementEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareManagementEventHandler.class);
    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);
    private final GroovyService groovyService;

    @Inject
    public FirmwareManagementEventHandler(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    @Override
    public DiagnosticsStatusNotificationConfirmation handleDiagnosticsStatusNotificationRequest(UUID uuid, DiagnosticsStatusNotificationRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(uuid, request);
    }

    @Override
    public FirmwareStatusNotificationConfirmation handleFirmwareStatusNotificationRequest(UUID uuid,
                                                                                          FirmwareStatusNotificationRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(uuid, request);
    }
}
