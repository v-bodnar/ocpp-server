package com.omb.ocpp.server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SslKeystoreConfig {

    private static final String CIPHERS_COMMA_SEPARATOR = ",";

    private String keystorePassword;
    private String keystoreProtocol;
    private Path keystorePath;
    private List<String> keystoreCiphers;

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public Path getKeystorePath() {
        return keystorePath;
    }

    public List<String> getKeystoreCiphers() {
        return keystoreCiphers;
    }

    public String getKeystoreProtocol() {
        return keystoreProtocol;
    }

    public static SslKeystoreConfig loadFromProperties(Properties properties) {
        SslKeystoreConfig config = new SslKeystoreConfig();
        config.keystorePassword = Objects.requireNonNull(properties.getProperty("keystore.password"));
        config.keystoreProtocol = Objects.requireNonNull(properties.getProperty("keystore.protocol"));
        config.keystorePath = Paths.get(Objects.requireNonNull(properties.getProperty("keystore.path")));
        Optional.ofNullable(properties.getProperty("keystore.ciphers")).ifPresent(ciphers -> config.keystoreCiphers = Arrays.asList(ciphers.split(CIPHERS_COMMA_SEPARATOR)));
        return config;
    }
}
