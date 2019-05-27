package com.omb.ocpp.security;

import com.omb.ocpp.server.SslKeyStoreConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CustomSSLWebSocketServerFactoryWrapper extends DefaultSSLWebSocketServerFactoryWrapper {

    private List<String> ciphers;

    public CustomSSLWebSocketServerFactoryWrapper(SslKeyStoreConfig sslKeystoreConfig, SSLContext sslContext, List<String> ciphers) {
        super(sslKeystoreConfig, sslContext);
        this.ciphers = Objects.requireNonNull(ciphers);
    }

    @Override
    public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
        SSLEngine sslEngine = sslcontext.createSSLEngine();
        /*
         * See https://github.com/TooTallNate/Java-WebSocket/issues/466
         *
         * For TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256 you must patch your java installation directly.
         */
        List<String> enabledCiphers = new ArrayList<>(Arrays.asList(sslEngine.getEnabledCipherSuites()));
        enabledCiphers.retainAll(ciphers);

        return createSslSocket(channel, key, enabledCiphers);
    }
}
