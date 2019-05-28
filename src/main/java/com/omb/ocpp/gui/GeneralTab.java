package com.omb.ocpp.gui;

import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.server.OcppServerService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

class GeneralTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralTab.class);
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?:\\.|$)){4}");
    private static final int DEFAULT_OCPP_PORT = 8887;

    private final Label serverState = new Label("Stopped");
    private final ComboBox<String> ipCombobox = new ComboBox<>();
    private final TextField portTextField = new TextField();
    private final Button serverButton = new Button("Start");
    private final CheckBox sslEnabledCheckbox = new CheckBox("ssl");

    private final OcppServerService ocppServerService;
    private final WebServer webServer;
    private TextFlow textFlow;

    GeneralTab(ServiceLocator applicationContext, TextFlow textFlow) {
        this.ocppServerService = applicationContext.getService(OcppServerService.class);
        this.webServer = applicationContext.getService(WebServer.class);
        this.textFlow = textFlow;
    }

    Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("General");
        tab.setClosable(false);


        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> textFlow.getChildren().clear());
        clearButton.setPrefWidth(100);
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
        hBox.getChildren().addAll(serverState, ipCombobox, portTextField, sslEnabledCheckbox, serverButton,
                clearButton);

        final ImageView imageFill = new ImageView(new Image(getClass().getResourceAsStream("/images/ev.png")));
        imageFill.setPreserveRatio(true);
        imageFill.fitHeightProperty().bind(primaryStage.heightProperty().subtract(400));

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
            sslEnabledCheckbox.setDisable(true);
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
            sslEnabledCheckbox.setDisable(false);
            serverButton.setOnAction(event -> {
                CompletableFuture.runAsync(() -> ocppServerService.start(ipCombobox.getValue(),
                        portTextField.getText(), sslEnabledCheckbox.isSelected()));
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
