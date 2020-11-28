package com.omb.ocpp.server.security.spec16ed2.certificate.signed.dto;

import com.google.gson.annotations.SerializedName;

public enum CertificateSignedStatus {

    @SerializedName("Accepted")
    ACCEPTED,

    @SerializedName("Rejected")
    REJECTED
}
