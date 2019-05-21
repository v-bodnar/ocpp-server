package com.omb.ocpp.groovy

import com.omb.ocpp.gui.Application
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant

class TriggerMessageConfirmationSupplier implements ConfirmationSupplier<TriggerMessageRequest, TriggerMessageConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerMessageConfirmationSupplier.class)
    private static final Instant CLASS_LOAD_DATE = Instant.now()
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

    @Override
    TriggerMessageConfirmation getConfirmation(UUID sessionUuid, TriggerMessageRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)
        TriggerMessageConfirmation confirmation = new TriggerMessageConfirmation()
        confirmation.setStatus(TriggerMessageStatus.Accepted)

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }

    @Override
    Instant getClassLoadDate(){
        return CLASS_LOAD_DATE;
    }
}
