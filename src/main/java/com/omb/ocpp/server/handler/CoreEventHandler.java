package com.omb.ocpp.server.handler;

import com.omb.ocpp.groovy.GroovyService;
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

import javax.inject.Inject;
import java.util.UUID;

@Service
public class CoreEventHandler implements ServerCoreEventHandler {
    private final GroovyService groovyService;

    @Inject
    public CoreEventHandler(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex, BootNotificationRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex, StartTransactionRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex, StatusNotificationRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }

    @Override
    public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex, StopTransactionRequest request) {
        return groovyService.getConfirmation(sessionIndex, request);
    }
}
