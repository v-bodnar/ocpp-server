package com.omb.ocpp.groovy

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.omb.ocpp.gui.Application
import com.omb.ocpp.server.OcppServerService
import eu.chargetime.ocpp.JSONCommunicator
import eu.chargetime.ocpp.model.SessionInformation
import eu.chargetime.ocpp.model.core.DataTransferConfirmation
import eu.chargetime.ocpp.model.core.DataTransferRequest
import eu.chargetime.ocpp.model.core.DataTransferStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DataTransferConfirmationSupplier implements ConfirmationSupplier<DataTransferRequest, DataTransferConfirmation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransferConfirmationSupplier.class)
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null)
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class)

    @Override
    DataTransferConfirmation getConfirmation(UUID sessionUuid, DataTransferRequest request) {
        SessionInformation unknownSession = new SessionInformation.Builder().Identifier("unknown").build()
        SessionInformation sessionInformation = ocppServerService.getSessionInformation(sessionUuid).orElse(unknownSession)

        DataTransferConfirmation confirmation = null
        // consider using external groovy file for each message
        if (request.getMessageId().equalsIgnoreCase("GetPrice.req")) {
            confirmation = getPrice()
        }

        LOGGER.debug("Responding to {} from client: {} body: {}", request.getClass().simpleName, sessionInformation
                .identifier, jsonCommunicator.packPayload(confirmation))
        return confirmation
    }

    private DataTransferConfirmation getPrice() {
        DataTransferConfirmation confirmation = new DataTransferConfirmation();

        JsonObject levelPrice = new JsonObject();
        levelPrice.addProperty("power", 10);
        levelPrice.addProperty("priceValue", 10);
        levelPrice.addProperty("initialPriceValue", 1);
        levelPrice.addProperty("initialDuration", 10);
        levelPrice.addProperty("authAmount", 100);
        levelPrice.addProperty("rate", "Wh");
        JsonArray levelPrices = new JsonArray();
        levelPrices.add(levelPrice);

        JsonObject intervalPrice = new JsonObject();
        intervalPrice.addProperty("intervalStart", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
        intervalPrice.addProperty("intervalEnd", ZonedDateTime.now(ZoneOffset.UTC).plusYears(1).format(DateTimeFormatter.ISO_INSTANT));
        intervalPrice.add("levelPrice", levelPrices);
        JsonArray intervalPrices = new JsonArray();
        intervalPrices.add(intervalPrice);

        JsonObject connectorPrice = new JsonObject();
        connectorPrice.addProperty("connectorId", 1);
        connectorPrice.add("intervalPrice", intervalPrices);
        JsonObject connectorPrice2 = new JsonObject();
        connectorPrice2.addProperty("connectorId", 2);
        connectorPrice2.add("intervalPrice", intervalPrices);
        JsonObject connectorPrice3 = new JsonObject();
        connectorPrice3.addProperty("connectorId", 3);
        connectorPrice3.add("intervalPrice", intervalPrices);
        JsonArray connectorPrices = new JsonArray();
        connectorPrices.add(connectorPrice);
        connectorPrices.add(connectorPrice2);
        connectorPrices.add(connectorPrice3);

        JsonObject getPriceConfirmation = new JsonObject();
        getPriceConfirmation.addProperty("currency", "EU");
        getPriceConfirmation.addProperty("firstName", "Vasia");
        getPriceConfirmation.addProperty("lastName", "Pupkin");
        getPriceConfirmation.addProperty("planName", "planName");
        getPriceConfirmation.addProperty("planDescription", "planDescription");
        getPriceConfirmation.add("connectorPrice", connectorPrices);

        //confirmation.setData("{\"currency\":\"EU\",\"firstName\":\"Mateusz\",\"lastName\":\"Zebracki\",\"planName\":\"planName\",\"planDescription\":\"description\",\"ConnectorPrice\":\"\"}");
        confirmation.setData(getPriceConfirmation.toString());
        confirmation.setStatus(DataTransferStatus.Accepted);
        return confirmation
    }
}
