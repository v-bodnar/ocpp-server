package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.server.OcppServerService;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class GuiApplication extends javafx.application.Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiApplication.class);
    private final ServiceLocator applicationContext = Application.APPLICATION.getApplicationContext();
    private final GroovyService groovyService = Application.APPLICATION.getService(GroovyService.class);
    private final OcppServerService ocppServerService = applicationContext.getService(OcppServerService.class);
    private final WebServer webServer = applicationContext.getService(WebServer.class);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        groovyService.loadGroovyScripts();
        createMainScene(primaryStage);
    }

    private void createMainScene(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/ocpp-logo.png")));

        // bind to take available space
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        root.getChildren().add(borderPane);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(new GeneralTab(applicationContext).constructTab());
        tabPane.getTabs().add(new CommunicatorTab(applicationContext).constructTab());
        tabPane.getTabs().add(new ServerTab(applicationContext).constructTab());
        borderPane.setCenter(tabPane);

        primaryStage.setOnCloseRequest(event -> {
            LOGGER.info("Shutting down");
            CompletableFuture.runAsync(ocppServerService::stop);
            CompletableFuture.runAsync(webServer::shutDown);
            System.exit(0);
        });

        primaryStage.setTitle("Ocpp Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
