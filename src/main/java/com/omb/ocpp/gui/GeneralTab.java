package com.omb.ocpp.gui;

import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.SslContextConfig;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

class GeneralTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralTab.class);
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?:\\.|$)){4}");
    private static final int DEFAULT_OCPP_PORT = 8887;

    private final Label serverState = new Label("Stopped");
    private final ComboBox<String> ipCombobox = new ComboBox<>();
    private final ComboBox<X509Certificate> certificateCombo = new ComboBox<>();
    private final CheckBox validateClientCertCheckBox = new CheckBox("validate client cert");
    private final TextField portTextField = new TextField();
    private final Button serverButton = new Button("Start");

    private final OcppServerService ocppServerService;
    private final WebServer webServer;
    private final KeystoreApi keystoreApi;

    GeneralTab(ServiceLocator applicationContext) {
        this.ocppServerService = applicationContext.getService(OcppServerService.class);
        this.webServer = applicationContext.getService(WebServer.class);
        this.keystoreApi = applicationContext.getService(KeystoreApi.class);
    }

    Tab constructTab(SplitPane splitPane) {
        Tab tab = new Tab();
        tab.setText("General");
        tab.setClosable(false);
        tab.setOnSelectionChanged(event -> {
            if (event.getTarget().equals(tab)) {
                try {
                    certificateCombo.setItems(FXCollections.observableArrayList(keystoreApi.getAllServerCertificates()));
                    certificateCombo.getItems().add(null);
                } catch (Exception e) {
                    LOGGER.error("Could not retrieve certificates data", e);
                }
            }
        });

        validateClientCertCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (ocppServerService.getSslContextConfig() != null) {
                ocppServerService.getSslContextConfig().setClientAuthenticationNeeded(newValue);
            }
        });


        certificateCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(X509Certificate certificate) {
                if (certificate == null) {
                    return "Select Server Certificate";
                } else {
                    return String.format("%s - %s", certificate.getIssuerDN(), certificate.getNotBefore());
                }
            }

            @Override
            public X509Certificate fromString(String string) {
                try {
                    Optional<X509Certificate> certificate = keystoreApi.getAllServerCertificates().stream()
                            .filter(x509Certificate -> x509Certificate.getIssuerDN().toString().equals(string.split(" ")[0]))
                            .findFirst();
                    if (certificate.isPresent()) {
                        return certificate.get();
                    } else {
                        LOGGER.error("Could not retrieve certificates data");
                        return null;
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not retrieve certificates data", e);
                    return null;
                }
            }
        });

        certificateCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                ocppServerService.setSslContextConfig(null);
            } else {
                try {
                    UUID keystoreUUID = keystoreApi.getKeyStoreUUIDByCertificate(newValue);
                    SslContextConfig sslContextConfig = new SslContextConfig().
                            setSslContext(keystoreApi.initializeSslContext(keystoreUUID)).
                            setClientAuthenticationNeeded(validateClientCertCheckBox.isSelected()).
                            setClientAuthenticationNeeded(false);
                    ocppServerService.setSslContextConfig(sslContextConfig);
                } catch (Exception e) {
                    LOGGER.error("Could not retrieve certificates data");
                }

            }
        });

        serverButton.setPrefWidth(100);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(serverState, Priority.ALWAYS);
        ipCombobox.setItems(FXCollections.observableArrayList(getIpAddresses()));
        ipCombobox.setValue("127.0.0.1");
        portTextField.setText("8887");
        portTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                LOGGER.error("Input from port field has to be integer, using default port: {}", DEFAULT_OCPP_PORT);
                portTextField.setText("" + DEFAULT_OCPP_PORT);
            }
        });

        hBox.getChildren().addAll(serverState, ipCombobox, portTextField, certificateCombo, validateClientCertCheckBox, serverButton);

        final ImageView imageFill = new ImageView(new Image(getClass().getResourceAsStream("/images/ev.png")));
        imageFill.setPreserveRatio(true);
        imageFill.fitHeightProperty().bind(splitPane.heightProperty().divide(2).subtract(75));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox, imageFill);
        tab.setContent(vBox);

        startStateChecker();
        return tab;
    }

    private void checkAndSetServerStateColor() {
        if (ocppServerService.isRunning() && webServer.isRunning()) {
            serverState.setStyle("-fx-text-fill: #0aa000;");
            serverState.setText("Started");
            serverButton.setText("Stop");
            serverButton.setDisable(false);
            ipCombobox.setDisable(true);
            portTextField.setDisable(true);
            validateClientCertCheckBox.setDisable(true);
            certificateCombo.setDisable(true);
            serverButton.setOnAction(event -> {
                CompletableFuture.runAsync(ocppServerService::stop);
                CompletableFuture.runAsync(webServer::shutDown);
                serverState.setText("Stopping...");
                serverButton.setDisable(true);
            });
        } else {
            serverState.setStyle("-fx-text-fill: #be0000;");
            serverState.setText("Stopped");
            serverButton.setText("Start");
            serverButton.setDisable(false);
            ipCombobox.setDisable(false);
            portTextField.setDisable(false);
            certificateCombo.setDisable(false);
            validateClientCertCheckBox.setDisable(false);
            serverButton.setOnAction(event -> {
                CompletableFuture.runAsync(() -> ocppServerService.start(ipCombobox.getValue(), Integer.parseInt(portTextField.getText())));
                CompletableFuture.runAsync(() -> {
                    try {
                        webServer.startServer(9090);
                    } catch (Exception e) {
                        LOGGER.error("Can't start REST server", e);
                    }
                });
                serverState.setText("Starting...");
                serverButton.setDisable(true);
            });
        }
    }

    private void startStateChecker() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(GeneralTab.this::checkAndSetServerStateColor);
            }
        }, 0, 3000);
    }

    private Set<String> getIpAddresses() {
        Set<String> availableIpAddresses = new HashSet<>();
        Enumeration e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            LOGGER.error("Could not retrieve ip addresses from network interfaces", e1);
        }
        while (e != null && e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (IP_ADDRESS_PATTERN.matcher(i.getHostAddress()).matches()) {
                    availableIpAddresses.add(i.getHostAddress());
                }
            }
        }
        return availableIpAddresses;
    }
}
