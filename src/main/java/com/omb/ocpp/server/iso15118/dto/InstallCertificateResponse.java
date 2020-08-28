package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;

import java.util.Objects;

public class InstallCertificateResponse implements Confirmation {

    @SerializedName("status")
    private InstallCertificateStatus status;

    public InstallCertificateStatus getStatus() {
        return status;
    }

    public void setStatus(InstallCertificateStatus status) {
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
        InstallCertificateResponse that = (InstallCertificateResponse) object;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "InstallCertificateResponse{" +
                "status=" + status +
                '}';
    }

    @Override
    public boolean validate() {
        return true;
    }
}
