package com.omb.ocpp.server.handler;

import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.IdTagInfo;

import java.util.Calendar;

public class CustomEventHandler {
    public AuthorizeConfirmation handleAuthorizeRequest(AuthorizeRequest authorizeRequest){
        IdTagInfo idTagInfo = new IdTagInfo();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);
        idTagInfo.setExpiryDate(cal);
        idTagInfo.setStatus(AuthorizationStatus.Accepted);
        AuthorizeConfirmation authorizeConfirmation = new AuthorizeConfirmation();
        authorizeConfirmation.setIdTagInfo(idTagInfo);
        return authorizeConfirmation;
    }
}
