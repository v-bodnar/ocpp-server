package com.omb.ocpp.gui;

import com.omb.ocpp.security.KeyChainGenerator;
import com.omb.ocpp.server.SslKeyStoreConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.Optional;

import static com.omb.ocpp.security.KeyChainGenerator.saveClientCertificateInKeyStore;
import static com.omb.ocpp.server.SslKeyStoreConfig.SSL_PROPERTIES;

public class SslTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslTab.class);
    private static final String NEW_LINE = System.getProperty("line.separator");
    private final ServiceLocator applicationContext;
    private final TextArea serverCertTextArea = new TextArea();
    private final TextArea clientCertTextArea = new TextArea();
    private final FileChooser serverCertificateDownloader = new FileChooser();
    private final Button serverCertificateDownloadButton = new Button("Download server certificate");
    private final FileChooser clientCertificateUploader = new FileChooser();
    private final Button clientCertificateUploadButton = new Button("Upload client certificate");
    private final SslKeyStoreConfig sslKeyStoreConfig;
    private double textAreasHeight = 595;
    private double textAreasWidth = 0;

    public SslTab(ServiceLocator applicationContext) {
        this.applicationContext = applicationContext;
        this.sslKeyStoreConfig = applicationContext.getService(SslKeyStoreConfig.class);
    }

    Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("SSL Cert");
        tab.setClosable(false);

        createServerTextArea();
        fitWidth(serverCertTextArea, primaryStage);
        createClientTextArea();
        fitWidth(clientCertTextArea, primaryStage);

        Optional<String> serverCertificate =
                KeyChainGenerator.getServerCertificatePem(sslKeyStoreConfig);

        serverCertificateDownloadButton.setPrefWidth(200);
        serverCertificateDownloadButton.setOnAction(event -> {
            serverCertificateDownloader.setTitle("Save server certificate");
            serverCertificateDownloader.setInitialFileName("ServerCertificate.pem");
            File file = serverCertificateDownloader.showSaveDialog(primaryStage);
            if (file != null && serverCertificate.isPresent()) {
                try (FileOutputStream os = new FileOutputStream(file)) {
                    os.write(serverCertificate.get().getBytes());
                } catch (IOException e) {
                    LOGGER.error("Could not write certificate to file", e);
                }
            } else {
                LOGGER.error("Certificate not found");
            }
        });

        clientCertificateUploadButton.setPrefWidth(200);
        clientCertificateUploadButton.setOnAction(event -> {
            clientCertificateUploader.setTitle("Save server certificate");
            File file = serverCertificateDownloader.showOpenDialog(primaryStage);
            saveClientCertificateInKeyStore(applicationContext.getService(SslKeyStoreConfig.class), file);
            createClientTextArea();
        });

        HBox textAreas = new HBox();
        textAreas.setSpacing(10);
        textAreas.setPadding(new Insets(5));
        textAreas.setAlignment(Pos.CENTER_LEFT);
        textAreas.setFillHeight(true);
        textAreas.setPrefHeight(Double.MAX_VALUE);
        textAreas.getChildren().addAll(serverCertTextArea, clientCertTextArea);

        textAreas.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (textAreasHeight != newValue.getHeight()) {
                textAreasHeight = newValue.getHeight();
                textAreas.setPrefHeight(primaryStage.getHeight() + 20); // +20 is for paddings
            }

        });

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().
                addAll(serverCertificateDownloadButton, clientCertificateUploadButton, textAreas);
        tab.setContent(vBox);

        return tab;
    }

    private void createServerTextArea() {
        Optional<Certificate> serverCertificate =
                KeyChainGenerator.getServerCertificate(applicationContext.getService(SslKeyStoreConfig.class));
        if (serverCertificate.isPresent()) {
            serverCertTextArea.setText(String.format("Server Certificate: %s %s", NEW_LINE,
                    serverCertificate.get().toString()));
        } else {
            serverCertTextArea.setText(String.format("Certificate is not generated, %s" +
                            "check if file %s exists and contains next information: %s" +
                            "\t keystore.password=<String>password %s" +
                            "\t keystore.protocol=<String>TLSv1.1|TLSv1.2 %s" +
                            "\t keystore.ciphers=<String>coma separated list %s",
                    NEW_LINE, SSL_PROPERTIES.toString(), NEW_LINE, NEW_LINE, NEW_LINE, NEW_LINE, NEW_LINE));
        }
    }

    private void fitWidth(TextArea textArea, Stage primaryStage) {
        textArea.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double halfScene = newValue.getWidth() / 2;
            if (textAreasWidth != halfScene) {
                textAreasWidth = halfScene;
                textArea.setPrefWidth(primaryStage.getWidth() / 2 + 20);
            }
        });
    }

    private void createClientTextArea() {
        Optional<Certificate> clientCertificate =
                KeyChainGenerator.getClientCertificate(applicationContext.getService(SslKeyStoreConfig.class));

        if (clientCertificate.isPresent()) {
            clientCertTextArea.setText(String.format("Client Certificate: %s %s", NEW_LINE,
                    clientCertificate.get().toString()));
        } else {
            String newLine = System.getProperty("line.separator");
            clientCertTextArea.setText(String.format("Certificate is not uploaded, %s" +
                    "if you are using self-signed certificate to client, upload it to the server", newLine));
        }
    }
}
