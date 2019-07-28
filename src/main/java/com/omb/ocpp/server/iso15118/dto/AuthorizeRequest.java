
package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AuthorizeRequest implements Request {

    @SerializedName("15118CertificateHashData")
    @Size(min = 1, max = 4)
    @Valid
    private List<OCSPRequestData> certificateHashData;
    /**
     * (Required)
     */
    @SerializedName("idToken")
    @Valid
    @NotNull
    private IdToken idToken;
    @SerializedName("evseId")
    @Size(min = 1)
    @Valid
    private List<Integer> evseId;

    @SerializedName("15118CertificateHashData")
    public Optional<List<OCSPRequestData>> get15118CertificateHashData() {
        return Optional.ofNullable(certificateHashData);
    }

    @SerializedName("15118CertificateHashData")
    public void set15118CertificateHashData(List<OCSPRequestData> certificateHashData) {
        this.certificateHashData = certificateHashData;
    }

    /**
     * (Required)
     */
    @SerializedName("idToken")
    public Optional<IdToken> getIdToken() {
        return Optional.ofNullable(idToken);
    }

    /**
     * (Required)
     */
    @SerializedName("idToken")
    public void setIdToken(IdToken idToken) {
        this.idToken = idToken;
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
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AuthorizeRequest that = (AuthorizeRequest) object;
        return Objects.equals(certificateHashData, that.certificateHashData) &&
                Objects.equals(idToken, that.idToken) &&
                Objects.equals(evseId, that.evseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateHashData, idToken, evseId);
    }

    @Override
    public String toString() {
        return "AuthorizeRequest{" +
                "certificateHashData=" + certificateHashData +
                ", idToken=" + idToken +
                ", evseId=" + evseId +
                ", additionalProperties=" +
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
