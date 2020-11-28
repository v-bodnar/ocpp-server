package com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto;

import com.google.gson.annotations.SerializedName;

public enum ExtendedTriggerMessage {

    @SerializedName("BootNotification")
    BOOT_NOTIFICATION,

    @SerializedName("LogStatusNotification")
    LOG_STATUS_NOTIFICATION,

    @SerializedName("FirmwareStatusNotification")
    FIRMWARE_STATUS_NOTIFICATION,

    @SerializedName("Heartbeat")
    HEARTBEAT,

    @SerializedName("MeterValues")
    METER_VALUES,

    @SerializedName("SignChargePointCertificate")
    SIGN_CHARGE_POINT_CERTIFICATE,

    @SerializedName("StatusNotification")
    STATUS_NOTIFICATION
}
