package eu.chargetime.ocpp.wss;

import com.omb.ocpp.server.SslKeystoreConfig;
import org.java_websocket.WebSocketServerFactory;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

public class BaseWssFactoryBuilderWrapper implements WssFactoryBuilder {

    private final SslKeystoreConfig sslKeystoreConfig;
    private List<String> ciphers = new ArrayList<>();
    private SSLContext sslContext;

    private BaseWssFactoryBuilderWrapper(SslKeystoreConfig sslKeystoreConfig) {
        this.sslKeystoreConfig = sslKeystoreConfig;
    }

    public static BaseWssFactoryBuilderWrapper builder(SslKeystoreConfig sslKeystoreConfig) {
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