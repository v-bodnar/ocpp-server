package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

public enum Type {
    @SerializedName("Central")
    CENTRAL,
    @SerializedName("eMAID")
    E_MAID,
    @SerializedName("ISO14443")
    ISO_14443,
    @SerializedName("KeyCode")
    KEY_CODE,
    @SerializedName("Local")
    LOCAL,
    @SerializedName("NoAuthorization")
    NO_AUTHORIZATION,
    @SerializedName("ISO15693")
    ISO_15693;
}