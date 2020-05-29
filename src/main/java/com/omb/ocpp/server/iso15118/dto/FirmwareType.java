package com.omb.ocpp.server.iso15118.dto;

import java.util.Calendar;
import java.util.Objects;

public class FirmwareType {

    private String location;
    private Calendar retrieveDateTime;
    private String signingCertificate;
    private String signature;
    private Calendar installDateTime;

    public FirmwareType() {
    }

    public FirmwareType(
            String location,
            Calendar retrieveDateTime,
            String signingCertificate,
            String signature) {
        this.location = location;
        this.retrieveDateTime = retrieveDateTime;
        this.signingCertificate = signingCertificate;
        this.signature = signature;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRetrieveDateTime(Calendar retrieveDateTime) {
        this.retrieveDateTime = retrieveDateTime;
    }

    public void setSigningCertificate(String signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public void setInstallDateTime(Calendar installDateTime) {
        this.installDateTime = installDateTime;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLocation() {
        return location;
    }

    public Calendar getRetrieveDateTime() {
        return retrieveDateTime;
    }

    public String getSigningCertificate() {
        return signingCertificate;
    }

    public String getSignature() {
        return signature;
    }

    public Calendar getInstallDateTime() {
        return installDateTime;
    }

    @Override
    public String toString() {
        return "FirmwareType{" +
                "location=" + location +
                ", retrieveDateTime=" + retrieveDateTime +
                ", signingCertificate='" + signingCertificate + '\'' +
                ", signature='" + signature + '\'' +
                ", installDateTime=" + installDateTime +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FirmwareType)) {
            return false;
        }
        FirmwareType that = (FirmwareType) object;
        return Objects.equals(location, that.location) &&
                Objects.equals(retrieveDateTime, that.retrieveDateTime) &&
                Objects.equals(signingCertificate, that.signingCertificate) &&
                Objects.equals(signature, that.signature) &&
                Objects.equals(installDateTime, that.installDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, retrieveDateTime, signingCertificate, signature, installDateTime);
    }
}

