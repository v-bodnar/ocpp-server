package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.GroovyService;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ServerTab {

    private GroovyService groovyService = ApplicationContext.INSTANCE.getGroovyService();

    public Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("G Server");
        tab.setClosable(false);

        ImageView imageView = new ImageView();
        imageView.setImage(new Image(getClass().getResourceAsStream("/images/under_construction.gif")));

        Button reloadGroovyButton = new Button("Reload groovy scripts");
        reloadGroovyButton.setOnAction(event -> groovyService.reloadGroovyFiles());

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageView);
        stackPane.getChildren().add(reloadGroovyButton);

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

        tab.setContent(stackPane);
        return tab;
    }
}
