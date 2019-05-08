package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.GroovyService;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

public class GuiApplication extends Application {
    public static GuiApplication APPLICATION;
    private final ServiceLocator applicationContext = ServiceLocatorUtilities.bind(new ApplicationBinder());
    private final GroovyService groovyService = applicationContext.getService(GroovyService.class);

    public GuiApplication() {
        super();
        synchronized (GuiApplication.class) {
            APPLICATION = this;
        }
    }

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

    private ServiceLocator getApplicationContext() {
        return applicationContext;
    }

    public <T> T getService(Class<T> clazz) {
        return getApplicationContext().getService(clazz);
    }
}
