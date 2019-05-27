package com.omb.ocpp.security;

import com.omb.ocpp.server.SslKeyStoreConfig;
import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import org.java_websocket.WebSocketServerFactory;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

public class BaseWssFactoryBuilderWrapper implements WssFactoryBuilder {

    private final SslKeyStoreConfig sslKeystoreConfig;
    private List<String> ciphers = new ArrayList<>();
    private SSLContext sslContext;

    private BaseWssFactoryBuilderWrapper(SslKeyStoreConfig sslKeystoreConfig) {
        this.sslKeystoreConfig = sslKeystoreConfig;
    }

    public static BaseWssFactoryBuilderWrapper builder(SslKeyStoreConfig sslKeystoreConfig) {
        return new BaseWssFactoryBuilderWrapper(sslKeystoreConfig);
    }

    public BaseWssFactoryBuilderWrapper ciphers(List<String> ciphers) {
        this.ciphers = ciphers;
        return this;
    }

    public BaseWssFactoryBuilderWrapper sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    @Override
    public WebSocketServerFactory build() {
        verify();
        return ciphers.isEmpty()
                ? new DefaultSSLWebSocketServerFactoryWrapper(sslKeystoreConfig, sslContext)
                : new CustomSSLWebSocketServerFactoryWrapper(sslKeystoreConfig, sslContext, ciphers);
    }

    @Override
    public void verify() {
        if (sslContext == null) {
            throw new IllegalStateException("sslContext must be set");
        }
    }
}