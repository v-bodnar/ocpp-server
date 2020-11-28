package com.omb.ocpp.server.security.spec16ed2.certificate.signed.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;
import java.util.Objects;

public class CertificateSignedResponse implements Confirmation {

    @SerializedName("status")
    private CertificateSignedStatus status;

    public CertificateSignedStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateSignedStatus status) {
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
        CertificateSignedResponse that = (CertificateSignedResponse) object;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "CertificateSignedResponse{" +
                "status=" + status +
                '}';
    }

    @Override
    public boolean validate() {
        return true;
    }
}
