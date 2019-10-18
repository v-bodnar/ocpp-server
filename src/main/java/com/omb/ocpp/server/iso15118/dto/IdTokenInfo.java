package com.omb.ocpp.server.iso15118.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class IdTokenInfo {

    /**
     * (Required)
     */
    @SerializedName("status")
    @NotNull
    private IdTokenInfo.Status status;
    @SerializedName("cacheExpiryDateTime")
    private Calendar cacheExpiryDateTime;
    @SerializedName("chargingPriority")
    private Integer chargingPriority;
    @SerializedName("groupIdToken")
    @Valid
    private GroupIdToken groupIdToken;
    @SerializedName("language1")
    @Size(max = 8)
    private String language1;
    @SerializedName("language2")
    @Size(max = 8)
    private String language2;
    @SerializedName("personalMessage")
    @Valid
    private MessageContent personalMessage;

    /**
     * (Required)
     */
    @SerializedName("status")
    public Optional<Status> getStatus() {
        return Optional.ofNullable(status);
    }

    /**
     * (Required)
     */
    @SerializedName("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @SerializedName("cacheExpiryDateTime")
    public Optional<Calendar> getCacheExpiryDateTime() {
        return Optional.ofNullable(cacheExpiryDateTime);
    }

    @SerializedName("cacheExpiryDateTime")
    public void setCacheExpiryDateTime(Calendar cacheExpiryDateTime) {
        this.cacheExpiryDateTime = cacheExpiryDateTime;
    }

    @SerializedName("chargingPriority")
    public Optional<Integer> getChargingPriority() {
        return Optional.ofNullable(chargingPriority);
    }

    @SerializedName("chargingPriority")
    public void setChargingPriority(Integer chargingPriority) {
        this.chargingPriority = chargingPriority;
    }

    @SerializedName("groupIdToken")
    public Optional<GroupIdToken> getGroupIdToken() {
        return Optional.ofNullable(groupIdToken);
    }

    @SerializedName("groupIdToken")
    public void setGroupIdToken(GroupIdToken groupIdToken) {
        this.groupIdToken = groupIdToken;
    }

    @SerializedName("language1")
    public Optional<String> getLanguage1() {
        return Optional.ofNullable(language1);
    }

    @SerializedName("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    @SerializedName("language2")
    public Optional<String> getLanguage2() {
        return Optional.ofNullable(language2);
    }

    @SerializedName("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @SerializedName("personalMessage")
    public Optional<MessageContent> getPersonalMessage() {
        return Optional.ofNullable(personalMessage);
    }

    @SerializedName("personalMessage")
    public void setPersonalMessage(MessageContent personalMessage) {
        this.personalMessage = personalMessage;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        IdTokenInfo that = (IdTokenInfo) object;
        return status == that.status &&
                Objects.equals(cacheExpiryDateTime, that.cacheExpiryDateTime) &&
                Objects.equals(chargingPriority, that.chargingPriority) &&
                Objects.equals(groupIdToken, that.groupIdToken) &&
                Objects.equals(language1, that.language1) &&
                Objects.equals(language2, that.language2) &&
                Objects.equals(personalMessage, that.personalMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, cacheExpiryDateTime, chargingPriority, groupIdToken, language1, language2, personalMessage);
    }

    @Override
    public String toString() {
        return "IdTokenInfo{" +
                "status=" + status +
                ", cacheExpiryDateTime=" + cacheExpiryDateTime +
                ", chargingPriority=" + chargingPriority +
                ", groupIdToken=" + groupIdToken +
                ", language1='" + language1 + '\'' +
                ", language2='" + language2 + '\'' +
                ", personalMessage=" + personalMessage +
                ", additionalProperties=" +
                '}';
    }

    public enum Status {
        @SerializedName("Accepted")
        ACCEPTED("Accepted"),
        @SerializedName("Blocked")
        BLOCKED("Blocked"),
        @SerializedName("ConcurrentTx")
        CONCURRENT_T("ConcurrentTx"),
        @SerializedName("Expired")
        EXPIRED("Expired"),
        @SerializedName("Invalid")
        INVALID("Invalid"),
        @SerializedName("NoCredit")
        NO_CREDIT("NoCredit"),
        @SerializedName("NotAllowedTypeEVSE")
        NOT_ALLOWED_TYPE_EVSE("NotAllowedTypeEVSE"),
        @SerializedName("NotAtThisLocation")
        NOT_AT_THIS_LOCATION("NotAtThisLocation"),
        @SerializedName("NotAtThisTime")
        NOT_AT_THIS_TIME("NotAtThisTime"),
        @SerializedName("Unknown")
        UNKNOWN("Unknown");

        private final String value;
        private static final Map<String, Status> CONSTANTS = new HashMap<>();

        static {
            for (Status c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Status fromValue(String value) {
            Status constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

}
