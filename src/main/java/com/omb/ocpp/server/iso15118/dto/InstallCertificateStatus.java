package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

public enum InstallCertificateStatus {
    @SerializedName("Accepted")
    ACCEPTED,
    @SerializedName("Rejected")
    REJECTED
}
