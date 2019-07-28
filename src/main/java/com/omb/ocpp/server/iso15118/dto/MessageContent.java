package com.omb.ocpp.server.iso15118.dto;

import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Optional;

public class MessageContent {

    /**
     * (Required)
     */
    @SerializedName("format")
    @NotNull
    private MessageContent.Format format;
    @SerializedName("language")
    @Size(max = 8)
    private String language;
    /**
     * (Required)
     */
    @SerializedName("content")
    @Size(max = 512)
    @NotNull
    private String content;

    /**
     * (Required)
     */
    @SerializedName("format")
    public Optional<Format> getFormat() {
        return Optional.ofNullable(format);
    }

    /**
     * (Required)
     */
    @SerializedName("format")
    public void setFormat(Format format) {
        this.format = format;
    }

    @SerializedName("language")
    public Optional<String> getLanguage() {
        return Optional.ofNullable(language);
    }

    @SerializedName("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * (Required)
     */
    @SerializedName("content")
    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    /**
     * (Required)
     */
    @SerializedName("content")
    public void setContent(String content) {
        this.content = content;
    }

    public enum Format {
        ASCII,
        HTML,
        URI,
        UTF_8;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MessageContent that = (MessageContent) object;
        return format == that.format &&
                Objects.equals(language, that.language) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, language, content);
    }

    @Override
    public String toString() {
        return "MessageContent{" +
                "format=" + format +
                ", language='" + language + '\'' +
                ", content='" + content + '\'' +
                ", additionalProperties=" +
                '}';
    }
}
