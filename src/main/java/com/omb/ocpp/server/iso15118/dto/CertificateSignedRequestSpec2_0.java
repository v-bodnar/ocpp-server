package com.omb.ocpp.server.iso15118.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.chargetime.ocpp.model.Request;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public class CertificateSignedRequestSpec2_0 implements Request {

    @JsonProperty("cert")
    private final List<String> certs;

    @JsonProperty("typeOfCertificate")
    private final CertificateSigningUseEnumType certificateType;

    @JsonCreator
    public CertificateSignedRequestSpec2_0(@JsonProperty("cert") List<String> certs, @JsonProperty("typeOfCertificate") CertificateSigningUseEnumType certificateType) {
        this.certs = requireNonNull(certs);
        this.certificateType = certificateType;
    }

    public List<String> getCerts() {
        return unmodifiableList(certs);
    }

    public Optional<CertificateSigningUseEnumType> getCertificateType() {
        return Optional.ofNullable(certificateType);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CertificateSignedRequestSpec2_0)) {
            return false;
        }
        CertificateSignedRequestSpec2_0 that = (CertificateSignedRequestSpec2_0) object;
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
