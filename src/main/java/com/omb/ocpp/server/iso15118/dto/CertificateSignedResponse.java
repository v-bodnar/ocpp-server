package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;

import java.util.Objects;

public class CertificateSignedResponse implements Confirmation {
    @SerializedName("status")
    private CertificateSignedStatusEnumType status;
    @SerializedName("statusInfo")
    private StatusInfoType statusInfo;

    public CertificateSignedStatusEnumType getStatus() {
        return status;
    }

    public void setStatus(CertificateSignedStatusEnumType status) {
        this.status = status;
    }

    public StatusInfoType getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(StatusInfoType statusInfo) {
        this.statusInfo = statusInfo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        CertificateSignedResponse that = (CertificateSignedResponse) object;
        return status == that.status &&
                Objects.equals(statusInfo, that.statusInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, statusInfo);
    }

    @Override
    public String toString() {
        return "CertificateSignedResponse{" +
                "status=" + status +
                ", statusInfo=" + statusInfo +
                '}';
    }

    @Override
    public boolean validate() {
        return true;
    }
}
