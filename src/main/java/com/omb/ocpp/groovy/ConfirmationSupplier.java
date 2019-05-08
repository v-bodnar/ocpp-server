package com.omb.ocpp.groovy;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

import java.util.UUID;

@FunctionalInterface
public interface ConfirmationSupplier<REQUEST extends Request, RESPONSE extends Confirmation> {
    RESPONSE getConfirmation(UUID sessionUuid, REQUEST request);
}
