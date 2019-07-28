package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Optional;

public class GroupIdToken {

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
        GroupIdToken that = (GroupIdToken) object;
        return Objects.equals(idToken, that.idToken) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idToken, type);
    }

    @Override
    public String toString() {
        return "GroupIdToken{" +
                "idToken='" + idToken + '\'' +
                ", type=" + type +
                ", additionalProperties=" +
                '}';
    }
}
