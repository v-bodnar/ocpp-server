package com.omb.ocpp.security;

import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import org.java_websocket.WebSocketServerFactory;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

public class BaseWssFactoryBuilderWrapper implements WssFactoryBuilder {

    private List<String> ciphers = new ArrayList<>();
    private boolean clientAuthenticationNeeded;
    private SSLContext sslContext;

    public BaseWssFactoryBuilderWrapper setCiphers(List<String> ciphers) {
        this.ciphers = ciphers;
        return this;
    }

    public BaseWssFactoryBuilderWrapper setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public BaseWssFactoryBuilderWrapper setClientAuthenticationNeeded(boolean clientAuthenticationNeeded) {
        this.clientAuthenticationNeeded = clientAuthenticationNeeded;
        return this;
    }

    @Override
    public WebSocketServerFactory build() {
        verify();
        return ciphers.isEmpty()
                ? new DefaultSSLWebSocketServerFactoryWrapper(clientAuthenticationNeeded, sslContext)
                : new CustomSSLWebSocketServerFactoryWrapper(clientAuthenticationNeeded, ciphers, sslContext);
    }

    @Override
    public void verify() {
        if (sslContext == null) {
            throw new IllegalStateException("sslContext must be set");
        }
    }
}