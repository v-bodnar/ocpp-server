package com.omb.ocpp.gui;

import com.omb.ocpp.security.certificate.service.TrustStoreService;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.cert.X509Certificate;

public class SslClientTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslClientTab.class);

    private final TableView<X509Certificate> tableView = new TableView<>();
    private final FileChooser certificateDownloader = new FileChooser();
    private final Button uploadCertificateButton = new Button("Upload certificate");
    private final TrustStoreService trustStoreService;

    public SslClientTab(ServiceLocator applicationContext) {
        this.trustStoreService = applicationContext.getService(TrustStoreService.class);
    }

    Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("Client Certificates");
        tab.setClosable(false);

        uploadCertificateButton.setMaxWidth(Double.MAX_VALUE);
        uploadCertificateButton.setOnAction(event -> {
            try {
                certificateDownloader.setTitle("Upload client certificate");
                File file = certificateDownloader.showOpenDialog(primaryStage);
                if (file != null) {
                    try (InputStream is = new FileInputStream(file)) {
                        trustStoreService.addClientCertificate(is);
                    }
                }
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
                    trustStoreService.deleteClientCertificate(getTableRow().getItem());
                    populateTable();

                };
            }
        });

        TableColumn<X509Certificate, Hyperlink> downloadColumn = new TableColumn<>("Link");
        downloadColumn.prefWidthProperty().bind(tableView.widthProperty().divide(8).subtract(10));
        downloadColumn.setCellFactory(param -> new HyperlinkCell<>("download") {
            @Override
            public EventHandler<ActionEvent> setOnAction() {
                return event -> {
                    certificateDownloader.setTitle("Save client certificate");
                    certificateDownloader.setInitialFileName("ClientCertificate.pem");
                    File file = certificateDownloader.showSaveDialog(primaryStage);
                    if (file == null) {
                        return;
                    }
                    try (FileOutputStream os = new FileOutputStream(file)) {
                        String serverCertificate = trustStoreService.getCertificateAsPem(getTableRow().getItem());
                        os.write(serverCertificate.getBytes());
                    } catch (Exception e) {
                        LOGGER.error("Could not find certificate", e);
                    }
                };
            }
        });

        tableView.getColumns().addAll(issuerColumn, validFromColumn, validToColumn, actionColumn, downloadColumn);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(uploadCertificateButton);
        HBox.setHgrow(uploadCertificateButton, Priority.ALWAYS);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(tableView, hBox);
        tab.setContent(vBox);

        return tab;
    }

    private void populateTable() {
        try {
            tableView.setItems(FXCollections.observableArrayList(trustStoreService.getClientCertificates()));
        } catch (Exception e) {
            LOGGER.error("Could not load certificates");
        }
    }
}
