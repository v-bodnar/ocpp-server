package com.omb.ocpp.server.iso15118.dto;

import eu.chargetime.ocpp.model.Confirmation;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class SignedUpdateFirmwareResponse implements Confirmation {

    private UpdateFirmwareStatus status;

    public SignedUpdateFirmwareResponse() {
    }

    public SignedUpdateFirmwareResponse(UpdateFirmwareStatus status) {
        this.status = requireNonNull(status);
    }

    @Override
    public boolean validate() {
        return true;
    }

    public UpdateFirmwareStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SignedUpdateFirmwareResponse)) {
            return false;
        }
        SignedUpdateFirmwareResponse that = (SignedUpdateFirmwareResponse) object;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "SignedUpdateFirmwareResponse{" +
                "status=" + status +
                '}';
    }
}
