package com.omb.ocpp.server.iso15118.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;

import java.util.Objects;

public class SignCertificateResponse implements Confirmation {
    @SerializedName("Accepted")
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
        SignCertificateResponse that = (SignCertificateResponse) object;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "SignCertificateResponse{" +
                "status=" + status +
                '}';
    }

    public enum Status {
        @SerializedName("Accepted")
        @JsonProperty("Accepted")
        ACCEPTED,
        @SerializedName("Rejected")
        @JsonProperty("Rejected")
        REJECTED;
    }

    @Override
    public boolean validate() {
        return true;
    }
}