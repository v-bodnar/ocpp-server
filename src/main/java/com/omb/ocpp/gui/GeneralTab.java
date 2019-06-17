package com.omb.ocpp.gui;

import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.SslContextConfig;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
    private static final String STOPPED_IMAGE = "/images/ev_stopped.png";
    private static final String PENDING_IMAGE = "/images/ev_pending.png";
    private static final String STARTED_IMAGE = "/images/ev_started.png";
    private static final String CERTIFICATE_ERROR = "Could not retrieve certificates data";

    private final Tab tab = new Tab();
    private final ComboBox<String> ipCombobox = new ComboBox<>();
    private final ComboBox<X509Certificate> certificateCombo = new ComboBox<>();
    private final CheckBox validateClientCertCheckBox = new CheckBox("Validate client certificate");
    private final TextField portTextField = new TextField();
    private final Button serverButton = new Button("Start");
    private final HBox hBox = new HBox();

    private final ImageView statusStoppedImage = new ImageView(new Image(getClass().getResourceAsStream(STOPPED_IMAGE)));
    private final ImageView statusPendingImage = new ImageView(new Image(getClass().getResourceAsStream(PENDING_IMAGE)));
    private final ImageView statusStartedImage = new ImageView(new Image(getClass().getResourceAsStream(STARTED_IMAGE)));

    private final OcppServerService ocppServerService;
    private final WebServer webServer;
    private final KeystoreApi keystoreApi;

    GeneralTab(ServiceLocator applicationContext) {
        this.ocppServerService = applicationContext.getService(OcppServerService.class);
        this.webServer = applicationContext.getService(WebServer.class);
        this.keystoreApi = applicationContext.getService(KeystoreApi.class);
    }

    Tab constructTab(SplitPane splitPane) {
        tab.setText("General");
        tab.setClosable(false);
        tab.setOnSelectionChanged(this::tabChangeEventHandler);

        validateClientCertCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (ocppServerService.getSslContextConfig() != null) {
                ocppServerService.getSslContextConfig().setClientAuthenticationNeeded(newValue);
            }
        });


        certificateCombo.setConverter(certificateConverter);

        certificateCombo.getSelectionModel().selectedItemProperty().addListener(this::certificatesComboValueChanged);

        serverButton.setPrefWidth(100);

        ipCombobox.setItems(FXCollections.observableArrayList(getIpAddresses()));
        ipCombobox.setValue("127.0.0.1");
        portTextField.setText("8887");
        portTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                LOGGER.error("Input from port field has to be integer, using default port: {}", DEFAULT_OCPP_PORT);
                portTextField.setText("" + DEFAULT_OCPP_PORT);
            }
        });

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(5));
        vBox.setAlignment(Pos.TOP_CENTER);

        statusStartedImage.setPreserveRatio(true);
        statusStoppedImage.setPreserveRatio(true);
        statusPendingImage.setPreserveRatio(true);
        statusStoppedImage.fitWidthProperty().bind(splitPane.widthProperty().divide(2).subtract(100));
        statusPendingImage.fitWidthProperty().bind(splitPane.widthProperty().divide(2).subtract(100));
        statusStartedImage.fitWidthProperty().bind(splitPane.widthProperty().divide(2).subtract(100));

        ipCombobox.prefWidthProperty().bind(splitPane.widthProperty());
        portTextField.prefWidthProperty().bind(splitPane.widthProperty());
        certificateCombo.prefWidthProperty().bind(splitPane.widthProperty());
        validateClientCertCheckBox.prefWidthProperty().bind(splitPane.widthProperty());
        serverButton.prefWidthProperty().bind(splitPane.widthProperty());

        vBox.getChildren().addAll(ipCombobox, portTextField, certificateCombo, validateClientCertCheckBox, serverButton);
        VBox.setVgrow(serverButton, Priority.ALWAYS);

        HBox.setHgrow(vBox, Priority.ALWAYS);
        hBox.getChildren().addAll(vBox, statusStoppedImage);
        tab.setContent(hBox);

        startStateChecker();
        return tab;
    }

    private void checkAndSetServerStateColor() {
        if (ocppServerService.isRunning() && webServer.isRunning()) {
            serverButton.setText("Stop");
            serverButton.setDisable(false);
            ipCombobox.setDisable(true);
            portTextField.setDisable(true);
            validateClientCertCheckBox.setDisable(true);
            certificateCombo.setDisable(true);
            changeStatusImage(Status.STARED);
            if (!hBox.getChildren().contains(statusStartedImage)) {
                hBox.getChildren().remove(statusPendingImage);
                hBox.getChildren().add(statusStartedImage);
            }
            serverButton.setOnAction(event -> {
                changeStatusImage(Status.PENDING);
                CompletableFuture.runAsync(ocppServerService::stop);
                CompletableFuture.runAsync(webServer::shutDown);
                serverButton.setDisable(true);
            });
        } else {
            serverButton.setText("Start");
            serverButton.setDisable(false);
            ipCombobox.setDisable(false);
            portTextField.setDisable(false);
            certificateCombo.setDisable(false);
            changeStatusImage(Status.STOPPED);
            validateClientCertCheckBox.setDisable(false);
            serverButton.setOnAction(event -> {
                changeStatusImage(Status.PENDING);
                CompletableFuture.runAsync(() -> ocppServerService.start(ipCombobox.getValue(), Integer.parseInt(portTextField.getText())));
                CompletableFuture.runAsync(() -> {
                    try {
                        webServer.startServer(9090);
                    } catch (Exception e) {
                        LOGGER.error("Can't start REST server", e);
                    }
                });
                serverButton.setDisable(true);
            });
        }
    }

    private void changeStatusImage(Status status) {
        Node node = hBox.getChildren().get(1);
        ImageView statusImage = determineImage(status);
        if (node != null && node != statusImage) {
            hBox.getChildren().remove(node);
            hBox.getChildren().add(statusImage);
        }

    }

    private ImageView determineImage(Status status) {
        switch (status) {
            case STARED:
                return statusStartedImage;
            case PENDING:
                return statusPendingImage;
            case STOPPED:
                return statusStoppedImage;
            default:
                return statusStoppedImage;
        }
    }

    enum Status {
        STARED,
        PENDING,
        STOPPED
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

    private StringConverter<X509Certificate> certificateConverter = new StringConverter<>() {
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
                    LOGGER.error(CERTIFICATE_ERROR);
                    return null;
                }
            } catch (Exception e) {
                LOGGER.error(CERTIFICATE_ERROR, e);
                return null;
            }
        }
    };


    private void tabChangeEventHandler(Event event) {
        if (event.getTarget().equals(tab)) {
            try {
                certificateCombo.setItems(FXCollections.observableArrayList(keystoreApi.getAllServerCertificates()));
                certificateCombo.getItems().add(null);
            } catch (Exception e) {
                LOGGER.error("Could not retrieve certificates data", e);
            }
        }
    }

    private void certificatesComboValueChanged(ObservableValue<? extends X509Certificate> observable,
                                               X509Certificate oldValue,
                                               X509Certificate newValue) {
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
    }
}
