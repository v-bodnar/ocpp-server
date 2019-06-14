package com.omb.ocpp.gui;

import com.omb.ocpp.groovy.ConfirmationSupplier;
import com.omb.ocpp.groovy.GroovyService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.glassfish.hk2.api.ServiceLocator;

import java.lang.reflect.ParameterizedType;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class ServerTab {
    private final GroovyService groovyService;
    private final TableView<ConfirmationSupplier> tableView = new TableView<>();

    ServerTab(ServiceLocator applicationContext) {
        this.groovyService = applicationContext.getService(GroovyService.class);
        this.groovyService.setGroovyCacheChangedListener(aVoid -> refreshTable());
    }

    Tab constructTab() {
        Tab tab = new Tab();
        tab.setText("Groovy");
        tab.setClosable(false);

        tableView.setItems(FXCollections.observableArrayList(groovyService.getConfirmationSuppliers()));

        TableColumn<ConfirmationSupplier, String> classNameColumn = new TableColumn<>("Class Name");
        classNameColumn.prefWidthProperty().bind(tableView.widthProperty().divide(3));
        classNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getClass().getSimpleName()));

        TableColumn<ConfirmationSupplier, String> confirmationName = new TableColumn<>("Confirmation class provided");
        confirmationName.prefWidthProperty().bind(tableView.widthProperty().divide(3));
        confirmationName.setCellValueFactory(param -> {
            String[] tok =
                    ((ParameterizedType) param.getValue().getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1].getTypeName().split("\\.");
            return new ReadOnlyStringWrapper(tok[tok.length - 1]);
        });

        TableColumn<ConfirmationSupplier, String> dateCol = new TableColumn<>("Date loaded");
        dateCol.prefWidthProperty().bind(tableView.widthProperty().divide(3).subtract(20));
        dateCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
                .format(param.getValue().getClassLoadDate()))
        );

        tableView.getColumns().addAll(classNameColumn, confirmationName, dateCol);

        Button reloadGroovyButton = new Button("Reload groovy scripts");
        reloadGroovyButton.setOnAction(event -> groovyService.reloadGroovyFiles());
        reloadGroovyButton.setMaxWidth(Double.MAX_VALUE);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(reloadGroovyButton);
        HBox.setHgrow(reloadGroovyButton, Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(tableView, hBox);
        tab.setContent(vBox);

        return tab;
    }

    private void refreshTable() {
        tableView.setItems(FXCollections.observableArrayList(groovyService.getConfirmationSuppliers()));
        tableView.refresh();
    }

}
