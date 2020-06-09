package com.omb.ocpp.server.iso15118.dto.spec_2_0;

import com.omb.ocpp.server.iso15118.dto.CertificateSigningUseEnumType;
import eu.chargetime.ocpp.model.Request;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

public class CertificateSignedRequest implements Request {

    @SerializedName("cert")
    private List<String> certs = new ArrayList<>();

    @SerializedName("typeOfCertificate")
    private CertificateSigningUseEnumType certificateType;

    public void setCerts(List<String> certs) {
        this.certs = certs;
    }

    public List<String> getCerts() {
        return unmodifiableList(certs);
    }

    public void setCertificateType(CertificateSigningUseEnumType certificateType) {
        this.certificateType = certificateType;
    }

    public Optional<CertificateSigningUseEnumType> getCertificateType() {
        return Optional.ofNullable(certificateType);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CertificateSignedRequest)) {
            return false;
        }
        CertificateSignedRequest that = (CertificateSignedRequest) object;
        return Objects.equals(certs, that.certs) && certificateType == that.certificateType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(certs, certificateType);
    }

    @Override
    public String toString() {
        return "DerCertificateSignedRequest{" +
                "certs=" + certs +
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
