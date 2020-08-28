package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Request;

import java.util.Objects;

public class InstallCertificateRequest implements Request {

    @SerializedName("certificateType")
    private String certificateType;

    @SerializedName("certificate")
    private String certificate;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public String toString() {
        return "InstallCertificateRequest{" +
                "certificateType='" + certificateType + '\'' +
                ", certificate='" + certificate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof InstallCertificateRequest)) {
            return false;
        }
        InstallCertificateRequest that = (InstallCertificateRequest) object;
        return Objects.equals(certificateType, that.certificateType) && Objects.equals(certificate, that.certificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateType, certificate);
    }
}
