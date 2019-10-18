
package com.omb.ocpp.server.iso15118.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AuthorizeResponse implements Confirmation {

    /**
     * (Required)
     */
    @SerializedName("idTokenInfo")
    @Valid
//    @NotNull  HACK: feature does not fulfill OCPP2.0 standard
    private IdTokenInfo idTokenInfo;

    @SerializedName("certificateStatus")
    private CertificateStatus certificateStatus;

    @SerializedName("cacheExpiryDateTime")
    @Valid
//    @NotNull  HACK: feature does not fulfill OCPP2.0 standard
    private Calendar cacheExpiryDateTime;

    @SerializedName("evseId")
    @Size(min = 1)
    @Valid
    private List<Integer> evseId;

    /**
     * (Required)
     */
    @SerializedName("idTokenInfo")
    public Optional<IdTokenInfo> getIdTokenInfo() {
        return Optional.ofNullable(idTokenInfo);
    }

    /**
     * (Required)
     */
    @SerializedName("idTokenInfo")
    public void setIdTokenInfo(IdTokenInfo idTokenInfo) {
        this.idTokenInfo = idTokenInfo;
    }

    @SerializedName("certificateStatus")
    public Optional<CertificateStatus> getCertificateStatus() {
        return Optional.ofNullable(certificateStatus);
    }

    @SerializedName("certificateStatus")
    public void setCertificateStatus(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    @SerializedName("evseId")
    public Optional<List<Integer>> getEvseId() {
        return Optional.ofNullable(evseId);
    }

    @SerializedName("evseId")
    public void setEvseId(List<Integer> evseId) {
        this.evseId = evseId;
    }

    @SerializedName("cacheExpiryDateTime")
    public Calendar getCacheExpiryDateTime() {
        return cacheExpiryDateTime;
    }

    @SerializedName("cacheExpiryDateTime")
    public void setCacheExpiryDateTime(Calendar cacheExpiryDateTime) {
        this.cacheExpiryDateTime = cacheExpiryDateTime;
    }

    @Override
    public boolean validate() {
        return true;
    }

    public enum CertificateStatus {
        @SerializedName("Accepted")
        ACCEPTED("Accepted"),
        @SerializedName("SignatureError")
        SIGNATURE_ERROR("SignatureError"),
        @SerializedName("CertificateExpired")
        CERTIFICATE_EXPIRED("CertificateExpired"),
        @SerializedName("CertificateRevoked")
        CERTIFICATE_REVOKED("CertificateRevoked"),
        @SerializedName("NoCertificateAvailable")
        NO_CERTIFICATE_AVAILABLE("NoCertificateAvailable"),
        @SerializedName("CertChainError")
        CERT_CHAIN_ERROR("CertChainError"),
        @SerializedName("ContractCancelled")
        CONTRACT_CANCELLED("ContractCancelled");
        private final String value;
        private static final Map<String, CertificateStatus> CONSTANTS = new HashMap<>();

        static {
            for (CertificateStatus c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        CertificateStatus(String value) {
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
        public static CertificateStatus fromValue(String value) {
            CertificateStatus constant = CONSTANTS.get(value);
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
        AuthorizeResponse that = (AuthorizeResponse) object;
        return Objects.equals(idTokenInfo, that.idTokenInfo) &&
                certificateStatus == that.certificateStatus &&
                Objects.equals(cacheExpiryDateTime, that.cacheExpiryDateTime) &&
                Objects.equals(evseId, that.evseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTokenInfo, certificateStatus, cacheExpiryDateTime, evseId);
    }

    @Override
    public String toString() {
        return "AuthorizeResponse{" +
                "idTokenInfo=" + idTokenInfo +
                ", certificateStatus=" + certificateStatus +
                ", cacheExpiryDateTime=" + cacheExpiryDateTime +
                ", evseId=" + evseId +
                '}';
    }
}
