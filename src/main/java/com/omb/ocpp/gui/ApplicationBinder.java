package com.omb.ocpp.gui;

import com.omb.ocpp.certificate.api.KeystoreApi;
import com.omb.ocpp.certificate.api.KeystoreApiImpl;
import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.SslKeyStoreConfig;
import com.omb.ocpp.server.handler.CoreEventHandler;
import com.omb.ocpp.server.handler.FirmwareManagementEventHandler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(GroovyService.class).to(GroovyService.class).in(Singleton.class);
        bind(OcppServerService.class).to(OcppServerService.class).in(Singleton.class);
        bind(WebServer.class).to(WebServer.class).in(Singleton.class);
        bind(CoreEventHandler.class).to(CoreEventHandler.class).in(Singleton.class);
        bind(FirmwareManagementEventHandler.class).to(FirmwareManagementEventHandler.class).in(Singleton.class);
        bind(SslKeyStoreConfig.class).to(SslKeyStoreConfig.class).in(Singleton.class);
        bind(KeystoreApiImpl.class).to(KeystoreApiImpl.class).in(Singleton.class);
    }
}
