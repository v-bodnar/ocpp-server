package com.omb.ocpp.server.iso15118.dto;

import eu.chargetime.ocpp.model.Request;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class SignedUpdateFirmwareRequest implements Request {

    private Integer requestId;
    private FirmwareType firmware;
    private Integer retries;
    private Integer retryInterval;

    public SignedUpdateFirmwareRequest() {
    }

    public SignedUpdateFirmwareRequest(Integer requestId, FirmwareType firmware) {
        this.requestId = requireNonNull(requestId);
        this.firmware = requireNonNull(firmware);
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public void setFirmware(FirmwareType firmware) {
        this.firmware = firmware;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public FirmwareType getFirmware() {
        return firmware;
    }

    public Integer getRetries() {
        return retries;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    @Override
    public String toString() {
        return "SignedUpdateFirmwareRequest{" +
                "requestId=" + requestId +
                ", firmware=" + firmware +
                ", retries=" + retries +
                ", retryInterval=" + retryInterval +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SignedUpdateFirmwareRequest)) {
            return false;
        }
        SignedUpdateFirmwareRequest request = (SignedUpdateFirmwareRequest) object;
        return Objects.equals(requestId, request.requestId) &&
                Objects.equals(firmware, request.firmware) &&
                Objects.equals(retries, request.retries) &&
                Objects.equals(retryInterval, request.retryInterval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, firmware, retries, retryInterval);
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
