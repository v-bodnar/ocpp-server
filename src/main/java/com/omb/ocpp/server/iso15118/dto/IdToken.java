package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IdToken {

    @SerializedName("additionalInfo")
    @Size(min = 1)
    @Valid
    private List<AdditionalInfo> additionalInfo;
    /**
     * (Required)
     */
    @SerializedName("idToken")
    @Size(max = 36)
    @NotNull
    private String idToken;
    /**
     * (Required)
     */
    @SerializedName("type")
    @NotNull
    private Type type;

    @SerializedName("additionalInfo")
    public Optional<List<AdditionalInfo>> getAdditionalInfo() {
        return Optional.ofNullable(additionalInfo);
    }

    @SerializedName("additionalInfo")
    public void setAdditionalInfo(List<AdditionalInfo> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * (Required)
     */
    @SerializedName("idToken")
    public Optional<String> getIdToken() {
        return Optional.ofNullable(idToken);
    }

    /**
     * (Required)
     */
    @SerializedName("idToken")
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    /**
     * (Required)
     */
    @SerializedName("type")
    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    /**
     * (Required)
     */
    @SerializedName("type")
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        IdToken idToken1 = (IdToken) object;
        return Objects.equals(additionalInfo, idToken1.additionalInfo) &&
                Objects.equals(idToken, idToken1.idToken) &&
                type == idToken1.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(additionalInfo, idToken, type);
    }

    @Override
    public String toString() {
        return "IdToken{" +
                "additionalInfo=" + additionalInfo +
                ", idToken='" + idToken + '\'' +
                ", type=" + type +
                ", additionalProperties=" +
                '}';
    }
}
