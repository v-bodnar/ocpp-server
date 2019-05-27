package com.omb.ocpp.gui;

import com.omb.ocpp.security.KeyChainGenerator;
import com.omb.ocpp.server.SslKeyStoreConfig;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
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
import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Optional;

import static com.omb.ocpp.security.KeyChainGenerator.saveClientCertificateInKeyStore;
import static com.omb.ocpp.server.SslKeyStoreConfig.SSL_PROPERTIES;

public class SslTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslTab.class);
    private final ServiceLocator applicationContext;
    private final TextArea textArea = new TextArea();
    private final FileChooser serverCertificateDownloader = new FileChooser();
    private final Button serverCertificateDownloadButton = new Button("Download server certificate");
    private final FileChooser clientCertificateUploader = new FileChooser();
    private final Button clientCertificateUploadButton = new Button("Upload client certificate");
    private double textAreaHeight = 595;

    public SslTab(ServiceLocator applicationContext) {
        this.applicationContext = applicationContext;
    }

    Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("SSL Cert");
        tab.setClosable(false);


        textArea.setPrefWidth(995);
        textArea.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (textAreaHeight != newValue.getHeight()) {
                textAreaHeight = newValue.getHeight();
                textArea.setPrefHeight(primaryStage.getHeight() + 20); // +20 is for paddings
            }
        });

        Optional<Certificate> certificate = KeyChainGenerator.getCertificate(applicationContext.getService(SslKeyStoreConfig.class));
        if (certificate.isPresent()) {
            textArea.setText(certificate.get().toString());
        } else {
            String newLine = System.getProperty("line.separator");
            textArea.setText(String.format("Certificate is not generated, %s" +
                            "check if file %s exists and contains next information: %s" +
                            "\t keystore.password=<String>password %s" +
                            "\t keystore.protocol=<String>TLSv1.1|TLSv1.2 %s" +
                            "\t keystore.ciphers=<String>coma separated list %s",
                    newLine, SSL_PROPERTIES.toString(), newLine, newLine, newLine, newLine, newLine));
        }

        serverCertificateDownloadButton.setPrefWidth(200);
        serverCertificateDownloadButton.setOnAction(event -> {
            serverCertificateDownloader.setTitle("Save server certificate");
            serverCertificateDownloader.setInitialFileName("ServerCertificate.cer");
            File file = serverCertificateDownloader.showSaveDialog(primaryStage);
            if (file != null && certificate.isPresent()) {
                try (FileOutputStream os = new FileOutputStream(file)) {
                    byte[] buf = certificate.get().getEncoded();
                    os.write(Base64.getEncoder().encode(buf));
                } catch (IOException | CertificateEncodingException e) {
                    LOGGER.error("Could not write certificate to file", e);
                }
            } else {
                LOGGER.error("Certificate not found");
                return;
            }
        });

        clientCertificateUploadButton.setPrefWidth(200);
        clientCertificateUploadButton.setOnAction(event -> {
            clientCertificateUploader.setTitle("Save server certificate");
            File file = serverCertificateDownloader.showOpenDialog(primaryStage);
            saveClientCertificateInKeyStore(applicationContext.getService(SslKeyStoreConfig.class), file);
        });

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().

                addAll(serverCertificateDownloadButton, clientCertificateUploadButton, textArea);
        tab.setContent(vBox);

        return tab;
    }
}
