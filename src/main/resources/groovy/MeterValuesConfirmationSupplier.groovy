package com.omb.ocpp.groovy

import com.omb.ocpp.gui.Application
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation
import eu.chargetime.ocpp.model.core.MeterValuesRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MeterValuesConfirmationSupplier implements ConfirmationSupplier<MeterValuesRequest, MeterValuesConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeterValuesConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

    @Override
    MeterValuesConfirmation getConfirmation(UUID sessionUuid, MeterValuesRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)
        MeterValuesConfirmation confirmation = new MeterValuesConfirmation()
        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}