package com.omb.ocpp.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.certificate.api.KeystoreApiImpl;
import com.omb.ocpp.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;
import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.server.OcppServerService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static final String LITHOS_HOME = Optional.ofNullable(System.getenv("LITHOS_HOME")).orElse("/home/bmterra/lithos");

    private static final String SHOW_KEYSTORE_CONFIG = "showKeystoreConfig";
    private static final String CREATE_KEYSTORE_CERTIFICATE = "createKeystoreCertificate";
    private static final String DELETE_KEYSTORE_CERTIFICATE = "deleteKeystoreCertificate";

    private static final String NO_GUI_ID = "nogui";
    private static final String IP_ID = "ip";
    private static final String OCPP_PORT_ID = "ocppPort";
    private static final String REST_PORT_ID = "restPort";
    private static final String OCPP_SSL_ID = "ocppSsl";

    public static final Application APPLICATION = new Application();

    private static String serverIp;
    private static String ocppPort;
    private static boolean ocppSsl;
    private static int restPort;

    private final ServiceLocator applicationContext = ServiceLocatorUtilities.bind(new ApplicationBinder());

    @Inject
    private GroovyService groovyService;

    @Inject
    private OcppServerService ocppServerService;

    @Inject
    private WebServer webServer;

    @Inject
    private KeystoreApiImpl keystoreApi;

    private Application() {
        applicationContext.inject(this);
    }

    public static void main(String[] args) throws Exception {
        try {
            CommandLineParser parser = new DefaultParser();
            Options options = getOptions();
            CommandLine cmd = parser.parse(options, args);

            serverIp = cmd.hasOption(IP_ID) ? cmd.getOptionValue(IP_ID) : "127.0.0.1";
            ocppPort = cmd.hasOption(OCPP_PORT_ID) ? cmd.getOptionValue(OCPP_PORT_ID) : "8887";
            restPort = cmd.hasOption(REST_PORT_ID) ? Integer.parseInt(cmd.getOptionValue(REST_PORT_ID)) : 9090;
            ocppSsl = cmd.hasOption(OCPP_SSL_ID) && Boolean.parseBoolean(cmd.getOptionValue(OCPP_SSL_ID));
            if (cmd.hasOption("help")) {
                printHelp(options);
            } else if (cmd.hasOption(SHOW_KEYSTORE_CONFIG)) {
                APPLICATION.showKeystoreCertificatesConfig();
            } else if (cmd.hasOption(CREATE_KEYSTORE_CERTIFICATE)) {
                APPLICATION.createKeystoreCertificate();
            } else if (cmd.hasOption(DELETE_KEYSTORE_CERTIFICATE)) {
                Optional<UUID> keystoreUUID = Optional.ofNullable(cmd.getOptionValue(DELETE_KEYSTORE_CERTIFICATE)).map(e -> UUID.fromString(e));
                if (keystoreUUID.isPresent()) {
                    APPLICATION.deleteKeystoreCertificate(keystoreUUID.get());
                } else {
                    throw new IllegalArgumentException("Invalid UUID");
                }
            } else if (cmd.hasOption(NO_GUI_ID)) {
                LOGGER.info("Starting server in no GUI mode, ip:{}, ocppPort: {}, restPort: {}", serverIp, ocppPort,
                        restPort);
                APPLICATION.startNoGui();
            } else {
                GuiApplication.main(args);
            }

        } catch (ParseException | NumberFormatException e) {
            LOGGER.error("Wrong arguments on the command line", e);
            printHelp(getOptions());
        }
    }

    private void showKeystoreCertificatesConfig() throws Exception {
        KeystoreCertificatesConfig keystoreCertificatesConfig = keystoreApi.getKeystoreCertificatesConfig();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LOGGER.info(gson.toJson(keystoreCertificatesConfig));
    }

    private void createKeystoreCertificate() throws Exception {
        KeystoreCertificateConfig keystoreCertificateConfig = keystoreApi.createKeystoreCertificate();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LOGGER.info(gson.toJson(keystoreCertificateConfig));
    }

    private void deleteKeystoreCertificate(UUID keystoreUUID) throws Exception {
        Boolean result = keystoreApi.deleteKeystoreCertificate(keystoreUUID);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LOGGER.info(gson.toJson(result));
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");

        options.addOption(SHOW_KEYSTORE_CONFIG, false, "Show keystore config file content");
        options.addOption(CREATE_KEYSTORE_CERTIFICATE, false, "Create new keystore certificate");
        options.addOption(DELETE_KEYSTORE_CERTIFICATE, true, "Delete keystore certificate");

        options.addOption(NO_GUI_ID, NO_GUI_ID, false, "indicates that application should be " +
                "started without GUI.");
        options.addOption(IP_ID, IP_ID, true, "the ip on which server will accept OCPP connections, default:127" +
                ".0.0.1, works in combination with -nogui");
        options.addOption(OCPP_PORT_ID, OCPP_PORT_ID, true, "port on which OCPP server will accept connections, " +
                "default:8887, works in combination with -nogui");
        options.addOption(OCPP_SSL_ID, OCPP_SSL_ID, false, "indicates that ocpp server should work over ssl");
        options.addOption(Option.builder(REST_PORT_ID)
                .longOpt(REST_PORT_ID)
                .hasArg()
                .desc("port on which REST server will accept connections, default:9090, works in combination with -nogui")
                .type(Integer.class)
                .build()

        );
        return options;
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ocpp-server", options);
    }

    private void startNoGui() {
        groovyService.loadGroovyScripts();
        ocppServerService.start(serverIp, ocppPort, ocppSsl);
        try {
            webServer.startServer(restPort);
        } catch (Exception e) {
            LOGGER.error("Can't start REST server", e);
        }
    }

    public ServiceLocator getApplicationContext() {
        return applicationContext;
    }

    public <T> T getService(Class<T> clazz) {
        return getApplicationContext().getService(clazz);
    }
}
