package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.rest.WebServer;
import com.omb.ocpp.server.OcppServerService;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
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
        SplitPane splitPane = new SplitPane();
        Scene scene = new Scene(splitPane, 1000, 600);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/ocpp-logo.png")));

        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.prefHeightProperty().bind(scene.heightProperty());
        splitPane.prefWidthProperty().bind(scene.widthProperty());

        TextField selectedLine = new TextField();
        TextFlow textFlow = new TextFlow();
        ConsoleStream console = new ConsoleStream(textFlow, selectedLine);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
        System.setErr(ps);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(textFlow);
        scrollPane.prefViewportHeightProperty().bind(scene.heightProperty());
        scrollPane.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color : black;");

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(new GeneralTab(applicationContext).constructTab(splitPane));
        tabPane.getTabs().add(new CommunicatorTab(applicationContext).constructTab(primaryStage));
        tabPane.getTabs().add(new ServerTab(applicationContext).constructTab());
        tabPane.getTabs().add(new SslServerTab(applicationContext).constructTab(primaryStage));
        tabPane.getTabs().add(new SslClientTab(applicationContext).constructTab(primaryStage));

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> textFlow.getChildren().clear());
        clearButton.setPrefWidth(100);
        clearButton.setMinWidth(100);

        TextField markerWord = new TextField();
        markerWord.setPromptText("string");
        markerWord.textProperty().addListener((observable, oldValue, newValue) -> console.markLines(markerWord.getText(), false));
        markerWord.setPrefWidth(100);

        TextField markerRegex = new TextField();
        markerRegex.setPromptText("regex");
        markerRegex.setPrefWidth(100);

        Button highlightButton = new Button("Highlight");
        highlightButton.setOnAction(event -> console.markLines(markerRegex.getText(), true));
        highlightButton.setPrefWidth(100);

        Button copyButton = new Button("Copy highlight");
        copyButton.setOnAction(event -> console.copySelected());
        copyButton.setPrefWidth(100);

        VBox rightVBox = new VBox();
        VBox leftVBox = new VBox();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftVBox, Priority.ALWAYS);
        hBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));

        leftVBox.getChildren().addAll(selectedLine, scrollPane);
        rightVBox.getChildren().addAll(clearButton, markerWord, markerRegex, highlightButton, copyButton);
        hBox.getChildren().addAll(leftVBox, rightVBox);

        splitPane.getItems().addAll(tabPane, hBox);

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
