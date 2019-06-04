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
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static final String LITHOS_HOME = Optional.ofNullable(System.getenv("LITHOS_HOME")).orElse("/home/bmterra/lithos");

    public static final Application APPLICATION = new Application();

    private final ServiceLocator applicationContext = ServiceLocatorUtilities.bind(new ApplicationBinder());

    private static final String HELP = "help";
    private static final String SHOW_KEYSTORE_CONFIG = "showKeystoreConfig";
    private static final String CREATE_KEYSTORE_CERTIFICATE = "createKeystoreCertificate";
    private static final String DELETE_KEYSTORE_CERTIFICATE = "deleteKeystoreCertificate";

    private static final String NO_GUI_ID = "nogui";
    private static final String IP_ID = "ip";
    private static final String OCPP_PORT_ID = "ocppPort";
    private static final String REST_PORT_ID = "restPort";
    private static final String OCPP_SSL_ID = "ocppSsl";

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

    public static void main(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            Options options = getOptions();
            CommandLine cmd = parser.parse(options, args);
            invokeCommand(options, cmd, args);
        } catch (Exception e) {
            LOGGER.error("Wrong arguments on the command line", e);
            printHelp(getOptions());
        }
    }

    private static void invokeCommand(Options options, CommandLine cmd, String[] args) throws Exception {
        Map<Predicate<CommandLine>, Action> COMMANDS = new LinkedHashMap<>();
        COMMANDS.put(line -> line.hasOption(HELP), () -> printHelp(options));
        COMMANDS.put(line -> line.hasOption(SHOW_KEYSTORE_CONFIG), APPLICATION::showKeystoreCertificatesConfig);
        COMMANDS.put(line -> line.hasOption(CREATE_KEYSTORE_CERTIFICATE), APPLICATION::createKeystoreCertificate);
        COMMANDS.put(line -> line.hasOption(DELETE_KEYSTORE_CERTIFICATE), () -> {
            UUID keystoreUUID = Optional.ofNullable(cmd.getOptionValue(DELETE_KEYSTORE_CERTIFICATE)).map(UUID::fromString).orElseThrow(() -> new IllegalArgumentException("Keystore UUID not found"));
            APPLICATION.deleteKeystoreCertificate(keystoreUUID);
        });
        COMMANDS.put(line -> line.hasOption(NO_GUI_ID), () -> {
            String host = cmd.hasOption(IP_ID) ? cmd.getOptionValue(IP_ID) : "127.0.0.1";
            int ocppPort = cmd.hasOption(OCPP_PORT_ID) ? Integer.parseInt(cmd.getOptionValue(OCPP_PORT_ID)) : 8887;
            int restPort = cmd.hasOption(REST_PORT_ID) ? Integer.parseInt(cmd.getOptionValue(REST_PORT_ID)) : 9090;
            boolean ocppSsl = cmd.hasOption(OCPP_SSL_ID) && Boolean.parseBoolean(cmd.getOptionValue(OCPP_SSL_ID));

            LOGGER.info("Starting server in no GUI mode, host:{}, ocppPort: {}, restPort: {}", host, ocppPort, restPort);
            APPLICATION.startNoGui(host, ocppPort, ocppSsl, restPort);
        });

        Action action = COMMANDS.
                entrySet().
                stream().
                filter(e -> e.getKey().test(cmd)).
                map(e -> e.getValue()).
                findFirst().
                orElseGet(() -> () -> GuiApplication.main(args));

        action.execute();
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
        keystoreApi.deleteKeystoreCertificate(keystoreUUID);
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", HELP, false, "print this message");

        options.addOption(SHOW_KEYSTORE_CONFIG, false, "Show keystore config file content");
        options.addOption(CREATE_KEYSTORE_CERTIFICATE, false, "Create new keystore certificate");
        options.addOption(DELETE_KEYSTORE_CERTIFICATE, true, "Delete keystore certificate");

        options.addOption(NO_GUI_ID, NO_GUI_ID, false, "indicates that application should be started without GUI.");
        options.addOption(IP_ID, IP_ID, true, "the ip on which server will accept OCPP connections, default:127.0.0.1, works in combination with -nogui");
        options.addOption(OCPP_PORT_ID, OCPP_PORT_ID, true, "port on which OCPP server will accept connections, default:8887, works in combination with -nogui");
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

    private void startNoGui(String host, int ocppPort, boolean ocppSsl, int restPort) {
        groovyService.loadGroovyScripts();
        ocppServerService.start(host, ocppPort, ocppSsl);
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

    @FunctionalInterface
    private interface Action {
        void execute() throws Exception;
    }
}
