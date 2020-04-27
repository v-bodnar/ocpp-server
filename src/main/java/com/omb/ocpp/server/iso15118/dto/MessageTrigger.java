package com.omb.ocpp.server.iso15118.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public enum MessageTrigger {
    @SerializedName("BootNotification")
    @JsonProperty("BootNotification")
    BOOT_NOTIFICATION,
    @SerializedName("DiagnosticsStatusNotification")
    @JsonProperty("DiagnosticsStatusNotification")
    DIAGNOSTICS_STATUS_NOTIFICATION,
    @SerializedName("FirmwareStatusNotification")
    @JsonProperty("FirmwareStatusNotification")
    FIRMWARE_STATUS_NOTIFICATION,
    @SerializedName("Heartbeat")
    @JsonProperty("Heartbeat")
    HEARTBEAT,
    @SerializedName("MeterValues")
    @JsonProperty("MeterValues")
    METER_VALUES,
    @SerializedName("StatusNotification")
    @JsonProperty("StatusNotification")
    STATUS_NOTIFICATION,
    @SerializedName("SignChargingStationCertificate")
    @JsonProperty("SignChargingStationCertificate")
    SIGN_CHARGING_STATION_CERTIFICATE,
    @SerializedName("SignV2GCertificate")
    @JsonProperty("SignV2GCertificate")
    SIGN_V2G_CERTIFICATE
}
