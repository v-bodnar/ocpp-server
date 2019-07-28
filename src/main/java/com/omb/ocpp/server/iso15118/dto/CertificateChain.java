package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CertificateChain {

    /**
     * (Required)
     */
    @SerializedName("certificate")
    @Size(max = 800)
    @NotNull
    private String certificate;
    @SerializedName("childCertificate")
    @Size(min = 1, max = 4)
    @Valid
    private List<String> childCertificate;

    /**
     * (Required)
     */
    @SerializedName("certificate")
    public Optional<String> getCertificate() {
        return Optional.ofNullable(certificate);
    }

    /**
     * (Required)
     */
    @SerializedName("certificate")
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @SerializedName("childCertificate")
    public Optional<List<String>> getChildCertificate() {
        return Optional.ofNullable(childCertificate);
    }

    @SerializedName("childCertificate")
    public void setChildCertificate(List<String> childCertificate) {
        this.childCertificate = childCertificate;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        CertificateChain that = (CertificateChain) object;
        return Objects.equals(certificate, that.certificate) &&
                Objects.equals(childCertificate, that.childCertificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate, childCertificate);
    }

    @Override
    public String toString() {
        return "CertificateChain{" +
                "certificate='" + certificate + '\'' +
                ", childCertificate=" + childCertificate +
                ", additionalProperties=" +
                '}';
    }
}
