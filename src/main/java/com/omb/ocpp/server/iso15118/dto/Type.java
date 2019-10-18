package com.omb.ocpp.server.iso15118.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public enum Type {
    @SerializedName("Central")
    CENTRAL("Central"),
    @SerializedName("eMAID")
    E_MAID("eMAID"),
    @SerializedName("ISO14443")
    ISO_14443("ISO14443"),
    @SerializedName("KeyCode")
    KEY_CODE("KeyCode"),
    @SerializedName("Local")
    LOCAL("Local"),
    @SerializedName("NoAuthorization")
    NO_AUTHORIZATION("NoAuthorization"),
    @SerializedName("ISO15693")
    ISO_15693("ISO15693");

    private final String value;
    private static final Map<String, Type> CONSTANTS = new HashMap<>();

    static {
        for (Type c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    Type(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static Type fromValue(String value) {
        Type constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

    @Override
    public String toString() {
        return this.value;
    }
}