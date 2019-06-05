package com.omb.ocpp.server;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static com.omb.ocpp.gui.Application.LITHOS_HOME;

// TODO will be deleted
@Service
public class SslKeyStoreConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslKeyStoreConfig.class);
    public static final Path SSL_PROPERTIES = Paths.get(LITHOS_HOME, "ocpp", "ssl", "ssl.properties");
    private static final Path keystorePath = Paths.get(LITHOS_HOME, "ocpp", "ssl", "ocppCsSrvKeystore.jks");
    private static final String CIPHERS_COMMA_SEPARATOR = ",";

    private String keystorePassword;
    private String keystoreProtocol;
    private KeyStore keyStore;

    private List<String> keystoreCiphers = new ArrayList<>();
    private boolean clientAuthenticationNeeded;

    @PostConstruct
    public void load() {
        if (!SSL_PROPERTIES.toFile().exists()) {
            LOGGER.error("ssl properties does not exist");
            return;
        }

        try (InputStream is = Files.newInputStream(SSL_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(is);
            loadFromProperties(properties);
        } catch (IOException e) {
            LOGGER.error("Could not load ssl properties", e);
        }

        try {
            keyStore = loadKeyStore();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            LOGGER.error("Could not load or create keyStore", e);
        }
    }

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

    private void loadFromProperties(Properties properties) {
        this.keystorePassword = properties.getProperty("keystore.password");
        this.keystoreProtocol = Objects.requireNonNull(properties.getProperty("keystore.protocol"));
        Optional.ofNullable(properties.getProperty("client.authentication.needed")).ifPresent(clientAuthenticationNeeded1 -> this.clientAuthenticationNeeded = Boolean.parseBoolean(clientAuthenticationNeeded1));
        Optional.ofNullable(properties.getProperty("keystore.ciphers")).ifPresent(ciphers -> this.keystoreCiphers = Arrays.asList(ciphers.split(CIPHERS_COMMA_SEPARATOR)));
    }

    private KeyStore loadKeyStore() throws CertificateException, NoSuchAlgorithmException, IOException,
            KeyStoreException {
        KeyStore keyStoreLocal = KeyStore.getInstance("JKS");

        if (!keystorePath.toFile().exists()) {
            Files.createFile(keystorePath);
            keyStoreLocal.load(null, null);
            try (OutputStream os = Files.newOutputStream(keystorePath)) {
                keyStoreLocal.store(os, keystorePassword.toCharArray());
            }
        } else {
            try (InputStream is = Files.newInputStream(keystorePath)) {
                keyStoreLocal.load(is, keystorePassword.toCharArray());
            }
        }
        return keyStoreLocal;
    }

    public Optional<KeyStore> geKeyStore() {
        return Optional.ofNullable(keyStore);
    }
}
