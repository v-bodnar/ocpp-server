package com.omb.ocpp.groovy

import com.omb.ocpp.gui.ApplicationContext
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.AuthorizationStatus
import eu.chargetime.ocpp.model.core.IdTagInfo
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation
import eu.chargetime.ocpp.model.core.StopTransactionRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StopTransactionConfirmationSupplier implements ConfirmationSupplier<StopTransactionRequest,
        StopTransactionConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopTransactionConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)

    @Override
    StopTransactionConfirmation getConfirmation(UUID sessionUuid, StopTransactionRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ApplicationContext.INSTANCE.ocppServerService
                .getSessionInformation(sessionUuid).orElse(unknownSession)
        IdTagInfo idTagInfo = new IdTagInfo()
        idTagInfo.setExpiryDate(new GregorianCalendar(2220, 1, 1))
        idTagInfo.setStatus(AuthorizationStatus.Accepted)

        StopTransactionConfirmation confirmation = new StopTransactionConfirmation()
        confirmation.setIdTagInfo(idTagInfo)

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
