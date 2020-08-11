package com.omb.ocpp.server.iso15118.dto.certificate.signing.elam;

import com.google.gson.annotations.SerializedName;
import com.omb.ocpp.server.iso15118.dto.certificate.signing.CertificateSigningUseEnumTypeSupport;

public enum CertificateSigningUseEnumType implements CertificateSigningUseEnumTypeSupport {

    @SerializedName("ChargingStationCertificate")
    CHARGING_STATION_CERTIFICATE,

    @SerializedName("V_2_G_CERTIFICATE")
    V2G_CERTIFICATE;
}
