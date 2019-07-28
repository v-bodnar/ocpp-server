
package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Confirmation;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AuthorizeResponse implements Confirmation {

    /**
     * (Required)
     */
    @SerializedName("idTokenInfo")
    @Valid
//    @NotNull  HACK: Elam feature does not fulfill OCPP2.0 standard
    private IdTokenInfo idTokenInfo;

    @SerializedName("certificateStatus")
    private CertificateStatus certificateStatus;

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

    @Override
    public boolean validate() {
        return true;
    }

    public enum CertificateStatus {
        @SerializedName("Accepted")
        ACCEPTED,
        @SerializedName("SignatureError")
        SIGNATUREERROR,
        @SerializedName("CertificateExpired")
        CERTIFICATEEXPIRED,
        @SerializedName("CertificateRevoked")
        CERTIFICATEREVOKED,
        @SerializedName("NoCertificateAvailable")
        NOCERTIFICATEAVAILABLE,
        @SerializedName("CertChainError")
        CERTCHAINERROR,
        @SerializedName("ContractCancelled")
        CONTRACTCANCELLED;
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
                Objects.equals(evseId, that.evseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTokenInfo, certificateStatus, evseId);
    }

    @Override
    public String toString() {
        return "AuthorizeResponse{" +
                "idTokenInfo=" + idTokenInfo +
                ", certificateStatus=" + certificateStatus +
                ", evseId=" + evseId +
                ", additionalProperties=" +
                '}';
    }
}
