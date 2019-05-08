package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.server.OcppServerService;

public class ApplicationContext {
    public static final ApplicationContext INSTANCE = new ApplicationContext();

    private GroovyService groovyService;
    private OcppServerService ocppServerService;
    private WebServer webServer;

    public void initialize() {
        this.groovyService = new GroovyService();
        groovyService.loadGroovyScripts();

        this.ocppServerService = new OcppServerService();
        this.webServer = new WebServer(9099);
    }

    public OcppServerService getOcppServerService() {
        return ocppServerService;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public GroovyService getGroovyService() {
        return groovyService;
    }
}
