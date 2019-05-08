package com.omb.ocpp.groovy

import com.omb.ocpp.gui.ApplicationContext
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.AuthorizationStatus
import eu.chargetime.ocpp.model.core.IdTagInfo
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation
import eu.chargetime.ocpp.model.core.StartTransactionRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StartTransactionConfirmationSupplier implements ConfirmationSupplier<StartTransactionRequest, StartTransactionConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartTransactionConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)

    @Override
    StartTransactionConfirmation getConfirmation(UUID sessionUuid, StartTransactionRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ApplicationContext.INSTANCE.ocppServerService
                .getSessionInformation(sessionUuid).orElse(unknownSession)

        IdTagInfo idTagInfo = new IdTagInfo()
        idTagInfo.setExpiryDate(new GregorianCalendar(2220, 1, 1))
        idTagInfo.setStatus(AuthorizationStatus.Accepted)

        StartTransactionConfirmation confirmation = new StartTransactionConfirmation()
        confirmation.setTransactionId(new Random().nextInt())
        confirmation.setIdTagInfo(idTagInfo)

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
