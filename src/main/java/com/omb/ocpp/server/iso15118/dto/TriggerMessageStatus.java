package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

public enum TriggerMessageStatus {
    @SerializedName("Accepted")
    ACCEPTED,
    @SerializedName("Rejected")
    REJECTED,
    @SerializedName("NotImplemented")
    NOT_IMPLEMENTED,
}
