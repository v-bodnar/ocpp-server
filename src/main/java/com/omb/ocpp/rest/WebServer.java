package com.omb.ocpp.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private Server restApiServer;

    public void startServer(int restApiPortNumber) throws Exception {
        logger.info("Starting REST API server");

        System.setProperty("org.eclipse.jetty.util.log.class",
                "org.eclipse.jetty.util.log.JavaUtilLog");
        System.setProperty("org.eclipse.jetty.util.log.class.LEVEL", "INFO");

        restApiServer = new Server(restApiPortNumber);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        restApiServer.setHandler(context);

        //Add logging for all requests
        Slf4jRequestLog requestLog = new Slf4jRequestLog();
        requestLog.setLoggerName("REST API");
        restApiServer.setRequestLog(requestLog);

        //Creating jersey servlet
        final ResourceConfig resourceConfig = new ResourceConfig()
                .registerClasses(RestAPI.class)
                .register(JacksonFeature.class);

        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(resourceConfig));
        jerseyServlet.setInitOrder(0);
        context.addServlet(jerseyServlet, "/*");
        restApiServer.start();
    }

    public boolean isRunning(){
        return restApiServer.isRunning();
    }

    public void shutDown() {
        logger.info("Shutting down REST API server");
        if (restApiServer != null) {
            try {
                restApiServer.stop();
            } catch (Exception e) {
                logger.error("Error while stopping Jetty Web Server", e);
            }
        }
    }
}
