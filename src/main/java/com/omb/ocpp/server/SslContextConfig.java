package com.omb.ocpp.server;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

public class SslContextConfig {

    private List<String> ciphers = new ArrayList<>();
    private boolean clientAuthenticationNeeded;
    private SSLContext sslContext;

    public List<String> getCiphers() {
        return ciphers;
    }

    public SslContextConfig setCiphers(List<String> ciphers) {
        this.ciphers = ciphers;
        return this;
    }

    public boolean isClientAuthenticationNeeded() {
        return clientAuthenticationNeeded;
    }

    public SslContextConfig setClientAuthenticationNeeded(boolean clientAuthenticationNeeded) {
        this.clientAuthenticationNeeded = clientAuthenticationNeeded;
        return this;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public SslContextConfig setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }
}
