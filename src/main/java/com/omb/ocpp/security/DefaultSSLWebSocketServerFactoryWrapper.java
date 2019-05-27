package com.omb.ocpp.security;

import com.omb.ocpp.server.SslKeyStoreConfig;
import org.java_websocket.SSLSocketChannel2;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultSSLWebSocketServerFactoryWrapper extends DefaultSSLWebSocketServerFactory {

    private final SslKeyStoreConfig sslKeystoreConfig;

    public DefaultSSLWebSocketServerFactoryWrapper(SslKeyStoreConfig sslKeystoreConfig, SSLContext sslContext) {
        super(sslContext);
        this.sslKeystoreConfig = sslKeystoreConfig;
    }

    @Override
    public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
        SSLEngine sslEngine = sslcontext.createSSLEngine();
        List<String> ciphers = new ArrayList<>(Arrays.asList(sslEngine.getEnabledCipherSuites()));
        ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        return createSslSocket(channel, key, ciphers);
    }

    protected ByteChannel createSslSocket(SocketChannel channel, SelectionKey key, List<String> enabledCiphers) throws IOException {
        SSLEngine e = sslcontext.createSSLEngine();
        e.setEnabledCipherSuites(enabledCiphers.toArray(new String[enabledCiphers.size()]));
        e.setUseClientMode(false);
        e.setNeedClientAuth(sslKeystoreConfig.isClientAuthenticationNeeded());
        return new SSLSocketChannel2(channel, e, exec, key);
    }
}
