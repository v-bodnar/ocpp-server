package com.omb.ocpp.groovy.supplier

import com.omb.ocpp.groovy.ConfirmationSupplier
import com.omb.ocpp.gui.Application
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant

class FirmwareStatusNotificationConfirmationSupplier implements ConfirmationSupplier<FirmwareStatusNotificationRequest,
        FirmwareStatusNotificationConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareStatusNotificationConfirmationSupplier.class)
    private static final Instant CLASS_LOAD_DATE = Instant.now()
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

    @Override
    FirmwareStatusNotificationConfirmation getConfirmation(UUID sessionUuid, FirmwareStatusNotificationRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)

        FirmwareStatusNotificationConfirmation confirmation = new FirmwareStatusNotificationConfirmation()

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }

    @Override
    Instant getClassLoadDate(){
        return CLASS_LOAD_DATE;
    }
}
