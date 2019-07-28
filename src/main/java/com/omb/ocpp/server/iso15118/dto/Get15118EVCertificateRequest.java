package com.omb.ocpp.server.iso15118.dto;


import com.google.gson.annotations.SerializedName;
import eu.chargetime.ocpp.model.Request;

import java.util.Objects;
import java.util.Optional;

public class Get15118EVCertificateRequest implements Request {

    /**
     * (Required)
     */
    @SerializedName("15118SchemaVersion")
    private String schemaVersion;

    /**
     * (Required)
     */
    @SerializedName("exiRequest")
    private String exiRequest;

    /**
     * (Required)
     */
    @SerializedName("15118SchemaVersion")
    public Optional<String> get15118SchemaVersion() {
        return Optional.of(schemaVersion);
    }

    /**
     * (Required)
     */
    @SerializedName("15118SchemaVersion")
    public void set15118SchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * (Required)
     */
    @SerializedName("exiRequest")
    public Optional<String> getExiRequest() {
        return Optional.of(exiRequest);
    }

    /**
     * (Required)
     */
    @SerializedName("exiRequest")
    public void setExiRequest(String exiRequest) {
        this.exiRequest = exiRequest;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Get15118EVCertificateRequest that = (Get15118EVCertificateRequest) object;
        return Objects.equals(schemaVersion, that.schemaVersion) &&
                Objects.equals(exiRequest, that.exiRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaVersion, exiRequest);
    }

    @Override
    public String toString() {
        return "Get15118EVCertificateRequest{" +
                "schemaVersion='" + schemaVersion + '\'' +
                ", exiRequest='" + exiRequest + '\'' +
                ", additionalProperties=" +
                '}';
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }
}