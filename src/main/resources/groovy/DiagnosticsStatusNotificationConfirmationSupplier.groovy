package com.omb.ocpp.groovy.supplier

import com.omb.ocpp.groovy.ConfirmationSupplier
import com.omb.ocpp.gui.Application
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant

class DiagnosticsStatusNotificationConfirmationSupplier implements ConfirmationSupplier<DiagnosticsStatusNotificationRequest,
        DiagnosticsStatusNotificationConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticsStatusNotificationConfirmationSupplier.class)
    private static final Instant CLASS_LOAD_DATE = Instant.now()
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

    @Override
    DiagnosticsStatusNotificationConfirmation getConfirmation(UUID sessionUuid, DiagnosticsStatusNotificationRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)

        DiagnosticsStatusNotificationConfirmation confirmation = new DiagnosticsStatusNotificationConfirmation()

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }

    @Override
    Instant getClassLoadDate(){
        return CLASS_LOAD_DATE;
    }
}
