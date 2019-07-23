package com.omb.ocpp.groovy.supplier

import com.omb.ocpp.groovy.ConfirmationSupplier
import com.omb.ocpp.gui.Application
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation
import eu.chargetime.ocpp.model.core.BootNotificationRequest
import eu.chargetime.ocpp.model.core.RegistrationStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant

class BootNotificationConfirmationSupplier implements ConfirmationSupplier<BootNotificationRequest,
        BootNotificationConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootNotificationConfirmationSupplier.class)
    private static final Instant CLASS_LOAD_DATE = Instant.now()
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

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

    @Override
    Instant getClassLoadDate(){
        return CLASS_LOAD_DATE;
    }
}
