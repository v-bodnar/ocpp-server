package com.omb.ocpp.server.iso15118.dto.certificate.signing.elam;

import com.google.gson.annotations.SerializedName;
import com.omb.ocpp.server.iso15118.dto.certificate.signing.CertificateSigningUseEnumTypeSupport;

public enum CertificateSigningUseEnumType implements CertificateSigningUseEnumTypeSupport {

    @SerializedName("ChargingStationCertificate")
    CHARGING_STATION_CERTIFICATE,

    @SerializedName("V2GCertificate")
    V2G_CERTIFICATE;
}
