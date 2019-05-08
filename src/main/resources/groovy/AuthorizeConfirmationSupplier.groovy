package com.omb.ocpp.groovy

import com.omb.ocpp.gui.GuiApplication
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.AuthorizationStatus
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation
import eu.chargetime.ocpp.model.core.AuthorizeRequest
import eu.chargetime.ocpp.model.core.IdTagInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AuthorizeConfirmationSupplier implements ConfirmationSupplier<AuthorizeRequest, AuthorizeConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = GuiApplication.APPLICATION.getService(OcppServerService.class)

    @Override
    AuthorizeConfirmation getConfirmation(UUID sessionUuid, AuthorizeRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)

        IdTagInfo idTagInfo = new IdTagInfo()
        idTagInfo.setExpiryDate(new GregorianCalendar(2220, 1, 1))
        idTagInfo.setStatus(AuthorizationStatus.Accepted)

        AuthorizeConfirmation confirmation = new AuthorizeConfirmation()
        confirmation.setIdTagInfo(idTagInfo)

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }
}
