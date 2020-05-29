package com.omb.ocpp.server.iso15118;

import com.omb.ocpp.server.iso15118.dto.SignedUpdateFirmwareRequest;
import com.omb.ocpp.server.iso15118.dto.SignedUpdateFirmwareResponse;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

public class SignedUpdateFirmwareFeature extends ProfileFeature {

    public SignedUpdateFirmwareFeature(Profile ownerProfile) {
        super(ownerProfile);
    }

    @Override
    public Class<? extends Request> getRequestType() {
        return SignedUpdateFirmwareRequest.class;
    }

    @Override
    public Class<? extends Confirmation> getConfirmationType() {
        return SignedUpdateFirmwareResponse.class;
    }

    @Override
    public String getAction() {
        return "SignedUpdateFirmware";
    }
}
