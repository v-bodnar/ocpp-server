package com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;
import java.util.Objects;

public class SignCertificateResponse implements Confirmation {

    @SerializedName("status")
    private CertificateResponseStatus status;

    public CertificateResponseStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateResponseStatus status) {
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

    @Override
    public boolean validate() {
        return true;
    }
}