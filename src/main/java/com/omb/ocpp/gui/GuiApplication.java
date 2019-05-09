package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.GroovyService;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;

public class GuiApplication extends javafx.application.Application {
    private final ServiceLocator applicationContext = Application.APPLICATION.getApplicationContext();
    private final GroovyService groovyService = Application.APPLICATION.getService(GroovyService.class);

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
        tabPane.getTabs().add(new ServerTab(applicationContext).constructTab(primaryStage));
        borderPane.setCenter(tabPane);

        primaryStage.setTitle("Ocpp Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
