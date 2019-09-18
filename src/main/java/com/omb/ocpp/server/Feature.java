package com.omb.ocpp.server;

public enum Feature {
    FIRMWARE_MANAGEMENT("FirmwareManagement"),
    REMOTE_TRIGGER("RemoteTrigger"),
    LOCAL_AUTH_LIST("LocalAuthList"),
    ISO_15118("ISO15118"),
    SMART_CHARGING("SmartCharging");
    private String key;

    Feature(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
