package com.omb.ocpp.server.handler;

import com.omb.ocpp.groovy.GroovyService;
import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.feature.profile.ClientFirmwareManagementEventHandler;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.omb.ocpp.server.handler.CoreEventHandler.RECEIVED_REQUEST;

@Service
public class FirmwareManagementEventHandler implements ClientFirmwareManagementEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareManagementEventHandler.class);
    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);
    private final GroovyService groovyService;

    @Inject
    public FirmwareManagementEventHandler(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    @Override
    public GetDiagnosticsConfirmation handleGetDiagnosticsRequest(GetDiagnosticsRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(null, request);
    }

    @Override
    public UpdateFirmwareConfirmation handleUpdateFirmwareRequest(UpdateFirmwareRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(null, request);
    }
}
