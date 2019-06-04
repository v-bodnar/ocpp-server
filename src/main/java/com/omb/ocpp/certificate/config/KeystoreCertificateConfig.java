package com.omb.ocpp.certificate.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class KeystoreCertificateConfig {

    private final UUID uuid;
    private final String keystorePassword;
    private final String keystorePath;

    private KeystoreCertificateConfig(Builder builder) {
        this.uuid = Objects.requireNonNull(builder.uuid);
        this.keystorePassword = Objects.requireNonNull(builder.keystorePassword);
        this.keystorePath = Objects.requireNonNull(builder.keystorePath);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public Path getKeystorePath() {
        return Paths.get(keystorePath);
    }

    public static class Builder {
        private UUID uuid;
        private String keystorePassword;
        private String keystorePath;

        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setKeystorePassword(String password) {
            this.keystorePassword = password;
            return this;
        }

        public Builder setKeystorePath(String keystorePath) {
            this.keystorePath = keystorePath;
            return this;
        }

        public KeystoreCertificateConfig build() {
            return new KeystoreCertificateConfig(this);
        }
    }
}
