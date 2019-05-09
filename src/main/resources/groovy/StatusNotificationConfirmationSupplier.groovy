package com.omb.ocpp.groovy

import com.omb.ocpp.gui.Application
import com.omb.ocpp.gui.GuiApplication
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation
import eu.chargetime.ocpp.model.core.StatusNotificationRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StatusNotificationConfirmationSupplier implements ConfirmationSupplier<StatusNotificationRequest,
        StatusNotificationConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusNotificationConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

    @Override
    StatusNotificationConfirmation getConfirmation(UUID sessionUuid, StatusNotificationRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)
        StatusNotificationConfirmation confirmation = new StatusNotificationConfirmation()
        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
