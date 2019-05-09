package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.GroovyService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

class ServerTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicatorTab.class);
    private final GroovyService groovyService;

    ServerTab(ServiceLocator applicationContext) {
        this.groovyService = applicationContext.getService(GroovyService.class);
    }

    Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("G Server");
        tab.setClosable(false);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("groovy", "*.groovy"));

        Button uploadConfirmationSupplierButton = new Button("Upload Confirmation Supplier");
        uploadConfirmationSupplierButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null && file.exists()) {
                try {
                    groovyService.uploadGroovyScript(new FileInputStream(file), file.getName());
                    Toast.info(primaryStage, "Upload Finished");
                } catch (Exception e) {
                    Toast.error(primaryStage, "Error occurred during upload");
                    LOGGER.error("Error occurred during upload", e);
                }
            }
        });

        uploadConfirmationSupplierButton.setMinWidth(200);
        uploadConfirmationSupplierButton.setMaxWidth(200);

        Button reloadGroovyButton = new Button("Reload groovy scripts");
        reloadGroovyButton.setOnAction(event -> {
            groovyService.reloadGroovyFiles();
            Toast.info(primaryStage, "Reload Finished");
        });
        reloadGroovyButton.setMinWidth(200);
        reloadGroovyButton.setMaxWidth(200);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(reloadGroovyButton);
        tab.setContent(vBox);

        return tab;
    }

}
