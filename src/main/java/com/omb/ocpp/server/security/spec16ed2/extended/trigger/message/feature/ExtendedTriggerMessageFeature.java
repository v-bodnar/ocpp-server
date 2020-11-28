package com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.feature;

import com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto.ExtendedTriggerMessageRequest;
import com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto.ExtendedTriggerMessageResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class ExtendedTriggerMessageFeature extends ProfileFeature {

    public ExtendedTriggerMessageFeature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return ExtendedTriggerMessageRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return ExtendedTriggerMessageResponse.class;
    }

    @Override
    public String getAction() {
        return "ExtendedTriggerMessage";
    }
}
