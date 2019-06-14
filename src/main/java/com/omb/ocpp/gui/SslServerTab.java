package com.omb.ocpp.gui;

import com.omb.ocpp.security.certificate.api.KeystoreApi;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.X509Certificate;

public class SslServerTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslServerTab.class);

    private final TableView<X509Certificate> tableView = new TableView<>();
    private final FileChooser certificateDownloader = new FileChooser();
    private final Button generateCertificateButton = new Button("Generate certificate");
    private final KeystoreApi keystoreApi;

    public SslServerTab(ServiceLocator applicationContext) {
        this.keystoreApi = applicationContext.getService(KeystoreApi.class);
    }

    Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("Server Certificates");
        tab.setClosable(false);

        keystoreApi.setKeystoreListener(aVoid -> populateTable());

        generateCertificateButton.setMaxWidth(Double.MAX_VALUE);
        generateCertificateButton.setOnAction(event -> {
            try {
                keystoreApi.createKeystoreCertificate();
                populateTable();
            } catch (Exception e) {
                LOGGER.error("Could not create certificate", e);
            }
        });

        tableView.setSelectionModel(null);
        populateTable();

        TableColumn<X509Certificate, String> issuerColumn = new TableColumn<>("Issuer");
        issuerColumn.prefWidthProperty().bind(tableView.widthProperty().divide(4));
        issuerColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getIssuerDN().toString()));

        TableColumn<X509Certificate, String> validFromColumn = new TableColumn<>("Valid from");
        validFromColumn.prefWidthProperty().bind(tableView.widthProperty().divide(4));
        validFromColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getNotBefore().toString()));

        TableColumn<X509Certificate, String> validToColumn = new TableColumn<>("Valid to");
        validToColumn.prefWidthProperty().bind(tableView.widthProperty().divide(4));
        validToColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getNotAfter().toString()));

        TableColumn<X509Certificate, Hyperlink> actionColumn = new TableColumn<>("Action");
        actionColumn.prefWidthProperty().bind(tableView.widthProperty().divide(8));
        actionColumn.setCellFactory(param -> new HyperlinkCell<>("delete") {
            @Override
            public EventHandler<ActionEvent> setOnAction() {
                return event -> {
                    try {
                        keystoreApi.deleteKeystoreCertificate(keystoreApi.getKeyStoreUUIDByCertificate(getTableRow().getItem()));
                        populateTable();
                    } catch (Exception e) {
                        LOGGER.error("Could not find certificate", e);
                    }
                };
            }
        });

        TableColumn<X509Certificate, Hyperlink> downloadColumn = new TableColumn<>("Link");
        downloadColumn.prefWidthProperty().bind(tableView.widthProperty().divide(8).subtract(10));
        downloadColumn.setCellFactory(param -> new HyperlinkCell<>("download") {
            @Override
            public EventHandler<ActionEvent> setOnAction() {
                return event -> {
                    certificateDownloader.setTitle("Save server certificate");
                    certificateDownloader.setInitialFileName("ServerCertificate.pem");
                    File file = certificateDownloader.showSaveDialog(primaryStage);
                    if (file == null) {
                        return;
                    }
                    try (FileOutputStream os = new FileOutputStream(file)) {
                        String serverCertificate =
                                keystoreApi.getServerCertificatePem(keystoreApi.getKeyStoreUUIDByCertificate(getTableRow().getItem()));
                        os.write(serverCertificate.getBytes());
                    } catch (Exception e) {
                        LOGGER.error("Could not find certificate", e);
                    }
                };
            }
        });

        tableView.getColumns().addAll(issuerColumn, validFromColumn, validToColumn, actionColumn, downloadColumn);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(generateCertificateButton);
        HBox.setHgrow(generateCertificateButton, Priority.ALWAYS);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(tableView, hBox);
        tab.setContent(vBox);

        return tab;
    }

    private void populateTable() {
        try {
            tableView.setItems(FXCollections.observableArrayList(keystoreApi.getAllServerCertificates()));
        } catch (Exception e) {
            LOGGER.error("Could not load certificates");
        }
    }
}
