package com.omb.ocpp.groovy.supplier

import com.omb.ocpp.groovy.ConfirmationSupplier
import com.omb.ocpp.gui.Application
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.AuthorizationStatus
import eu.chargetime.ocpp.model.core.IdTagInfo
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation
import eu.chargetime.ocpp.model.core.StartTransactionRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant

class StartTransactionConfirmationSupplier implements ConfirmationSupplier<StartTransactionRequest, StartTransactionConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartTransactionConfirmationSupplier.class)
    private static final Instant CLASS_LOAD_DATE = Instant.now()
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

    @Override
    StartTransactionConfirmation getConfirmation(UUID sessionUuid, StartTransactionRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)

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

    @Override
    Instant getClassLoadDate(){
        return CLASS_LOAD_DATE;
    }
}
