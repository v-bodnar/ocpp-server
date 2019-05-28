package com.omb.ocpp.gui;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleStream extends OutputStream {
    private TextFlow output;
    private OutputStream fdOut = new FileOutputStream(FileDescriptor.out);
    private PrintStream printStream = new PrintStream(new BufferedOutputStream(fdOut, 128), true);
    private StringBuilder stringBuilder = new StringBuilder();

    ConsoleStream(TextFlow ta) {
        this.output = ta;
    }

    @Override
    public void write(int i) {
        Platform.runLater(() -> {
            printStream.print((char) i);
            stringBuilder.append((char) i);
            if (i == 10) {
                String string = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                Text text = new Text(string);
                if (string.contains("INFO")) {
                    text.setFill(Color.GREEN);
                }else if (string.contains("DEBUG")) {
                    text.setFill(Color.BLACK);
                } else if (string.contains("WARN")) {
                    text.setFill(Color.ORANGE);
                } else if (string.contains("ERROR")) {
                    text.setStyle("-fx-font-weight: bold");
                    text.setFill(Color.RED);
                }
                output.getChildren().add(text);
            }
        });
    }
}
