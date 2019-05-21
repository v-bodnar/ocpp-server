package com.omb.ocpp.server.handler;

import com.omb.ocpp.groovy.GroovyService;
import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

@Service
public class CoreEventHandler implements ServerCoreEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreEventHandler.class);
    public static final String RECEIVED_REQUEST = "Received request {} - {}";
    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);
    private final GroovyService groovyService;

    @Inject
    public CoreEventHandler(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(),
                jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex, BootNotificationRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex, StartTransactionRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex, StatusNotificationRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex, StopTransactionRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(sessionIndex, request);
    }
}
