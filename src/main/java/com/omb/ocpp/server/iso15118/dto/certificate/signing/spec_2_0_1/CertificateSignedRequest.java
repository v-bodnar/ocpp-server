package com.omb.ocpp.server.iso15118.dto.certificate.signing.spec_2_0_1;

import com.google.gson.annotations.SerializedName;
import com.omb.ocpp.server.iso15118.dto.certificate.signing.CertificateSigningUseEnumTypeSupport;
import eu.chargetime.ocpp.model.Request;

import java.util.Objects;

public class CertificateSignedRequest implements Request {

    @SerializedName("certificateChain")
    private String certificateChain;

    @SerializedName("certificateType")
    private CertificateSigningUseEnumTypeSupport certificateType;

    public String getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(String certificateChain) {
        this.certificateChain = certificateChain;
    }

    public CertificateSigningUseEnumTypeSupport getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateSigningUseEnumTypeSupport certificateType) {
        this.certificateType = certificateType;
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
        return Objects.equals(certificateChain, that.certificateChain) &&
                certificateType == that.certificateType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateChain, certificateType);
    }

    @Override
    public String toString() {
        return "CertificateSignedRequest{" +
                "certificateChain='" + certificateChain + '\'' +
                ", certificateType=" + certificateType +
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
