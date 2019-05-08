package com.omb.ocpp.groovy

import com.omb.ocpp.gui.ApplicationContext
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GetDiagnosticsConfirmationSupplier implements ConfirmationSupplier<GetDiagnosticsRequest,
        GetDiagnosticsConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetDiagnosticsConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)

    @Override
    GetDiagnosticsConfirmation getConfirmation(UUID sessionUuid, GetDiagnosticsRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ApplicationContext.INSTANCE.ocppServerService
                .getSessionInformation(sessionUuid).orElse(unknownSession)

        GetDiagnosticsConfirmation confirmation = new StopTransactionConfirmation()

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
