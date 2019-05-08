package com.omb.ocpp.groovy

import com.omb.ocpp.gui.ApplicationContext
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DiagnosticsStatusNotificationConfirmationSupplier implements ConfirmationSupplier<DiagnosticsStatusNotificationRequest,
        DiagnosticsStatusNotificationConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticsStatusNotificationConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)

    @Override
    DiagnosticsStatusNotificationConfirmation getConfirmation(UUID sessionUuid, DiagnosticsStatusNotificationRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ApplicationContext.INSTANCE.ocppServerService
                .getSessionInformation(sessionUuid).orElse(unknownSession)

        DiagnosticsStatusNotificationConfirmation confirmation = new DiagnosticsStatusNotificationConfirmation()

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
