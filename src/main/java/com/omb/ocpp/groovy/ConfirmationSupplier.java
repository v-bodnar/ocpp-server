package com.omb.ocpp.groovy;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

import java.time.Instant;
import java.util.UUID;

public interface ConfirmationSupplier<REQUEST extends Request, RESPONSE extends Confirmation> {
    RESPONSE getConfirmation(UUID sessionUuid, REQUEST request);

    Instant getClassLoadDate();
}
