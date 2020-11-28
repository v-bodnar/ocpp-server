package com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Request;
import java.util.Objects;

public class SignCertificateRequest implements Request {

    @SerializedName("csr")
    private String csr;

    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    @Override
    public int hashCode() {
        return Objects.hash(csr);
    }

    @Override
    public String toString() {
        return "SignCertificateRequest{" +
                "csr='" + csr + '\'' +
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
