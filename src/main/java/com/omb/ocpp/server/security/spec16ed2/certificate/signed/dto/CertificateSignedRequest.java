package com.omb.ocpp.server.security.spec16ed2.certificate.signed.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Request;
import java.util.Objects;

public class CertificateSignedRequest implements Request {

    @SerializedName("certificateChain")
    private String certificateChain;

    public String getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(String certificateChain) {
        this.certificateChain = certificateChain;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        CertificateSignedRequest that = (CertificateSignedRequest) object;
        return Objects.equals(certificateChain, that.certificateChain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateChain);
    }

    @Override
    public String toString() {
        return "CertificateSignedRequest{" +
                "certificateChain='" + certificateChain + '\'' +
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
