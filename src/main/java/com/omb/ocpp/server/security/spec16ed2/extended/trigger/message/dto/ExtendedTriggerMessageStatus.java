package com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto;

import com.google.gson.annotations.SerializedName;

public enum ExtendedTriggerMessageStatus {

    @SerializedName("Accepted")
    ACCEPTED,

    @SerializedName("Rejected")
    REJECTED,

    @SerializedName("NotImplemented")
    NOT_IMPLEMENTED
}