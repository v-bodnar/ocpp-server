package com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto;

import com.google.gson.annotations.SerializedName;

public enum CertificateResponseStatus {

    @SerializedName("Accepted")
    ACCEPTED,

    @SerializedName("Rejected")
    REJECTED;
}
