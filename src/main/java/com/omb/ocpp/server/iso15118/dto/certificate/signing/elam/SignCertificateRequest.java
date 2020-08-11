package com.omb.ocpp.server.iso15118.dto.certificate.signing.elam;

import com.google.gson.annotations.SerializedName;
import com.omb.ocpp.server.iso15118.dto.certificate.signing.SignCertificateRequestSupport;

import java.util.Objects;

public class SignCertificateRequest implements SignCertificateRequestSupport {

    @SerializedName("csr")
    private String csr;

    @SerializedName("typeOfCertificate")
    private CertificateSigningUseEnumType typeOfCertificate;

    @Override
    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    @Override
    public CertificateSigningUseEnumType getTypeOfCertificate() {
        return typeOfCertificate;
    }

    public void setTypeOfCertificate(CertificateSigningUseEnumType typeOfCertificate) {
        this.typeOfCertificate = typeOfCertificate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(csr, typeOfCertificate);
    }

    @Override
    public String toString() {
        return "SignCertificateRequest{" +
                "csr='" + csr + '\'' +
                ", typeOfCertificate=" + typeOfCertificate +
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
