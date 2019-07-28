package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.AuthorizeRequest;
import com.omb.ocpp.server.iso15118.dto.AuthorizeResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class ISO15118Feature extends ProfileFeature {
    public ISO15118Feature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return AuthorizeRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return AuthorizeResponse.class;
    }

    @Override
    public String getAction() {
        return "Authorize";
    }
}
