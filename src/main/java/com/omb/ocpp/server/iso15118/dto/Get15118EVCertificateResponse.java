
package com.omb.ocpp.server.iso15118.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Get15118EVCertificateResponse implements Confirmation {

    /**
     * (Required)
     */
    @SerializedName("status")
    private Status status;

    /**
     * (Required)
     */
    @SerializedName("exiResponse")
    private String exiResponse;


    @SerializedName("saProvisioningCertificateChain")
    @Valid
    // @NotNull NOTE: according to OCPP 2.0 spec this property is required
    private CertificateChain saProvisioningCertificateChain;

    /**
     * (Required)
     */
    @SerializedName("contractSignatureCertificateChain")
    @Valid
    // @NotNull NOTE: according to OCPP 2.0 spec this property is required
    private CertificateChain contractSignatureCertificateChain;

    /**
     * (Required)
     */
    @SerializedName("status")
    public Optional<Status> getStatus() {
        return Optional.of(status);
    }

    /**
     * (Required)
     */
    @SerializedName("status")
    public void setStatus(Status status) {
        this.status = status;
    }


    /**
     * (Required)
     */
    @SerializedName("exiResponse")
    public Optional<String> getExiResponse() {
        return Optional.of(exiResponse);
    }

    /**
     * (Required)
     */
    @SerializedName("exiResponse")
    public void setExiResponse(String exiResponse) {
        this.exiResponse = exiResponse;
    }

    @SerializedName("saProvisioningCertificateChain")
    public Optional<CertificateChain> getSaProvisioningCertificateChain() {
        return Optional.ofNullable(saProvisioningCertificateChain);
    }

    @SerializedName("saProvisioningCertificateChain")
    public void setSaProvisioningCertificateChain(CertificateChain saProvisioningCertificateChain) {
        this.saProvisioningCertificateChain = saProvisioningCertificateChain;
    }

    @SerializedName("contractSignatureCertificateChain")
    public Optional<CertificateChain> getContractSignatureCertificateChain() {
        return Optional.ofNullable(contractSignatureCertificateChain);
    }

    @SerializedName("contractSignatureCertificateChain")
    public void setContractSignatureCertificateChain(CertificateChain contractSignatureCertificateChain) {
        this.contractSignatureCertificateChain = contractSignatureCertificateChain;
    }

    @Override
    public boolean validate() {
        return true;
    }

    public enum Status {

        ACCEPTED("Accepted"),
        FAILED("Failed");
        private final String value;
        private static final Map<String, Status> CONSTANTS = new HashMap<>();

        static {
            for (Status c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Status fromValue(String value) {
            Status constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Get15118EVCertificateResponse that = (Get15118EVCertificateResponse) object;
        return status == that.status &&
                Objects.equals(exiResponse, that.exiResponse) &&
                Objects.equals(saProvisioningCertificateChain, that.saProvisioningCertificateChain) &&
                Objects.equals(contractSignatureCertificateChain, that.contractSignatureCertificateChain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, exiResponse, saProvisioningCertificateChain, contractSignatureCertificateChain);
    }

    @Override
    public String toString() {
        return "Get15118EVCertificateResponse{" +
                "status=" + status +
                ", exiResponse='" + exiResponse + '\'' +
                ", saProvisioningCertificateChain=" + saProvisioningCertificateChain +
                ", contractSignatureCertificateChain=" + contractSignatureCertificateChain +
                ", additionalProperties=" +
                '}';
    }
}
