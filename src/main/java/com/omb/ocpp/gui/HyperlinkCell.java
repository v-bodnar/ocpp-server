package com.omb.ocpp.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;

public abstract class HyperlinkCell<S, T> extends TableCell<S, T> {

    private final Hyperlink hyperlink;

    public HyperlinkCell(String label) {
        this.hyperlink = new Hyperlink(label);
        this.hyperlink.setUserData(getIndex());
        this.hyperlink.setOnAction(setOnAction());
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(hyperlink);
        }
    }

    public abstract EventHandler<ActionEvent> setOnAction();
}