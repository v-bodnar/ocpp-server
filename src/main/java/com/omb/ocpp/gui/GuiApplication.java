package com.omb.ocpp.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GuiApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationContext.INSTANCE.initialize();
        createMainScene(primaryStage);
    }

    public void createMainScene(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/ocpp-logo.png")));

        // bind to take available space
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        root.getChildren().add(borderPane);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(new GeneralTab().constructTab());
        tabPane.getTabs().add(new CommunicatorTab().constructTab());
        tabPane.getTabs().add(new ServerTab().constructTab(primaryStage));
        borderPane.setCenter(tabPane);

        primaryStage.setTitle("Ocpp Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
