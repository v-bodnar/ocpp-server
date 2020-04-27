package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.TriggerMessageRequest;
import com.omb.ocpp.server.iso15118.dto.TriggerMessageResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class TriggerMessageFeature extends ProfileFeature {
    public TriggerMessageFeature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return TriggerMessageRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return TriggerMessageResponse.class;
    }

    @Override
    public String getAction() {
        return "TriggerMessage";
    }
}
