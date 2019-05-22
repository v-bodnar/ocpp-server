package com.omb.ocpp.server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SslKeystoreConfig {

    private static final String CIPHERS_COMMA_SEPARATOR = ",";

    private String keystorePassword;
    private String keystoreProtocol;
    private Path keystorePath;
    private List<String> keystoreCiphers = new ArrayList<>();
    private boolean clientAuthenticationNeeded;

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

    public boolean isClientAuthenticationNeeded() {
        return clientAuthenticationNeeded;
    }

    public static SslKeystoreConfig loadFromProperties(Properties properties) {
        SslKeystoreConfig config = new SslKeystoreConfig();
        config.keystorePassword = properties.getProperty("keystore.password");
        config.keystoreProtocol = Objects.requireNonNull(properties.getProperty("keystore.protocol"));
        config.keystorePath = Paths.get(Objects.requireNonNull(properties.getProperty("keystore.path")));
        Optional.ofNullable(properties.getProperty("client.authentication.needed")).ifPresent(clientAuthenticationNeeded -> config.clientAuthenticationNeeded = Boolean.parseBoolean(clientAuthenticationNeeded));
        Optional.ofNullable(properties.getProperty("keystore.ciphers")).ifPresent(ciphers -> config.keystoreCiphers = Arrays.asList(ciphers.split(CIPHERS_COMMA_SEPARATOR)));
        return config;
    }
}
