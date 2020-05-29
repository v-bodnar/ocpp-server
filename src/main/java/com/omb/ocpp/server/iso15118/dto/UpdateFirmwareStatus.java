package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

public enum UpdateFirmwareStatus {

    @SerializedName("Accepted")
    ACCEPTED,

    @SerializedName("Rejected")
    REJECTED,

    @SerializedName("AcceptedCanceled")
    ACCEPTED_CANCELED
}
