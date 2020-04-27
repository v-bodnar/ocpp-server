package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class StatusInfoType {
    @SerializedName("reasonCode")
    private String reasonCode;
    @SerializedName("additionalInfo")
    private String additionalInfo;

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        StatusInfoType that = (StatusInfoType) object;
        return Objects.equals(reasonCode, that.reasonCode) &&
                Objects.equals(additionalInfo, that.additionalInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reasonCode, additionalInfo);
    }

    @Override
    public String toString() {
        return "StatusInfoType{" +
                "reasonCode='" + reasonCode + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
