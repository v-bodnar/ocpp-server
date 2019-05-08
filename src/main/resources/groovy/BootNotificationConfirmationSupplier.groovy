package com.omb.ocpp.groovy


import com.omb.ocpp.gui.GuiApplication
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation
import eu.chargetime.ocpp.model.core.BootNotificationRequest
import eu.chargetime.ocpp.model.core.RegistrationStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BootNotificationConfirmationSupplier implements ConfirmationSupplier<BootNotificationRequest,
        BootNotificationConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootNotificationConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = GuiApplication.APPLICATION.getService(OcppServerService.class)

    @Override
    BootNotificationConfirmation getConfirmation(UUID sessionUuid, BootNotificationRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)
        BootNotificationConfirmation confirmation = new BootNotificationConfirmation();
        confirmation.setCurrentTime(Calendar.getInstance())
        confirmation.setInterval(180)
        confirmation.setStatus(RegistrationStatus.Accepted)

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
