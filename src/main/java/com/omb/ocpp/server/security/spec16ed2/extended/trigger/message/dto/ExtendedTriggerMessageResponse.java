package com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;
import java.util.Objects;

public class ExtendedTriggerMessageResponse implements Confirmation {

    @SerializedName("status")
    private ExtendedTriggerMessageStatus status;

    public ExtendedTriggerMessageStatus getStatus() {
        return status;
    }

    public void setStatus(ExtendedTriggerMessageStatus status) {
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
        ExtendedTriggerMessageResponse that = (ExtendedTriggerMessageResponse) object;
        return status == that.status;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "ExtendedTriggerMessageResponse{" +
                "status=" + status +
                '}';
    }

    @Override
    public boolean validate() {
        return true;
    }
}
