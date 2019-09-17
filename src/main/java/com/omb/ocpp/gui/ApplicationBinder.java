package com.omb.ocpp.gui;

import com.omb.ocpp.config.Config;
import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.security.certificate.api.KeystoreApiImpl;
import com.omb.ocpp.security.certificate.service.TrustStoreService;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.handler.CoreEventHandler;
import com.omb.ocpp.server.handler.FirmwareManagementEventHandler;
import com.omb.ocpp.server.handler.ISO15118EventHandler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(Config.class).to(Config.class).in(Singleton.class);
        bind(GroovyService.class).to(GroovyService.class).in(Singleton.class);
        bind(OcppServerService.class).to(OcppServerService.class).in(Singleton.class);
        bind(WebServer.class).to(WebServer.class).in(Singleton.class);
        bind(CoreEventHandler.class).to(CoreEventHandler.class).in(Singleton.class);
        bind(FirmwareManagementEventHandler.class).to(FirmwareManagementEventHandler.class).in(Singleton.class);
        bind(ISO15118EventHandler.class).to(ISO15118EventHandler.class).in(Singleton.class);
        bind(KeystoreApiImpl.class).to(KeystoreApi.class).in(Singleton.class);
        bind(TrustStoreService.class).to(TrustStoreService.class).in(Singleton.class);
    }
}
