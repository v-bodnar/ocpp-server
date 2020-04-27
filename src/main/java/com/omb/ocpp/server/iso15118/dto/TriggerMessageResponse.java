package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;

import java.util.Objects;

public class TriggerMessageResponse implements Confirmation {
    @SerializedName("status")
    private TriggerMessageStatus status;

    public TriggerMessageStatus getStatus() {
        return status;
    }

    public void setStatus(TriggerMessageStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        TriggerMessageResponse that = (TriggerMessageResponse) object;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "TriggerMessageResponse{" +
                "status=" + status +
                '}';
    }

    @Override
    public boolean validate() {
        return true;
    }
}
