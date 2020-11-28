package com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Request;
import java.util.Objects;

public class ExtendedTriggerMessageRequest implements Request {

    @SerializedName("requestedMessage")
    private ExtendedTriggerMessage messageTriggerType;

    @SerializedName("connectorId")
    private Integer connectorId;

    public ExtendedTriggerMessage getMessageTriggerType() {
        return messageTriggerType;
    }

    public void setMessageTriggerType(ExtendedTriggerMessage messageTriggerType) {
        this.messageTriggerType = messageTriggerType;
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
        ExtendedTriggerMessageRequest that = (ExtendedTriggerMessageRequest) object;
        return messageTriggerType == that.messageTriggerType &&
                Objects.equals(connectorId, that.connectorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageTriggerType, connectorId);
    }

    @Override
    public String toString() {
        return "ExtendedTriggerMessageRequest{" +
                "messageTriggerType=" + messageTriggerType +
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
