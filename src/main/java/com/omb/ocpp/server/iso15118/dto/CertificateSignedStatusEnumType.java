package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

public enum CertificateSignedStatusEnumType {
    @SerializedName("Accepted")
    ACCEPTED,
    @SerializedName("Rejected")
    REJECTED
}
