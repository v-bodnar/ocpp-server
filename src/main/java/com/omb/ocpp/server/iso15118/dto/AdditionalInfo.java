package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Optional;

public class AdditionalInfo {

    /**
     * (Required)
     */
    @SerializedName("additionalIdToken")
    @Size(max = 36)
    @NotNull
    private String additionalIdToken;
    /**
     * (Required)
     */
    @SerializedName("type")
    @Size(max = 50)
    @NotNull
    private String type;

    /**
     * (Required)
     */
    @SerializedName("additionalIdToken")
    public Optional<String> getAdditionalIdToken() {
        return Optional.ofNullable(additionalIdToken);
    }

    /**
     * (Required)
     */
    @SerializedName("additionalIdToken")
    public void setAdditionalIdToken(String additionalIdToken) {
        this.additionalIdToken = additionalIdToken;
    }

    /**
     * (Required)
     */
    @SerializedName("type")
    public Optional<String> getType() {
        return Optional.ofNullable(type);
    }

    /**
     * (Required)
     */
    @SerializedName("type")
    public void setType(String type) {
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
        AdditionalInfo that = (AdditionalInfo) object;
        return Objects.equals(additionalIdToken, that.additionalIdToken) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(additionalIdToken, type);
    }

    @Override
    public String toString() {
        return "AdditionalInfo{" +
                "additionalIdToken='" + additionalIdToken + '\'' +
                ", type='" + type + '\'' +
                ", additionalProperties=" +
                '}';
    }
}
