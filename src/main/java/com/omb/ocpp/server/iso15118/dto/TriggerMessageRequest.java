package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Request;

import java.util.Objects;

public class TriggerMessageRequest implements Request {
    @SerializedName("requestedMessage")
    private MessageTrigger requestedMessage;
    @SerializedName("connectorId")
    private Integer connectorId;

    public MessageTrigger getRequestedMessage() {
        return requestedMessage;
    }

    public void setRequestedMessage(MessageTrigger requestedMessage) {
        this.requestedMessage = requestedMessage;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Integer connectorId) {
        this.connectorId = connectorId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        TriggerMessageRequest that = (TriggerMessageRequest) object;
        return requestedMessage == that.requestedMessage &&
                Objects.equals(connectorId, that.connectorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestedMessage, connectorId);
    }

    @Override
    public String toString() {
        return "TriggerMessageRequest{" +
                "requestedMessage=" + requestedMessage +
                ", connectorId=" + connectorId +
                '}';
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }
}
