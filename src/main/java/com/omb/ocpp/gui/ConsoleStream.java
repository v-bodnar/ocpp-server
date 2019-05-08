package com.omb.ocpp.gui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleStream extends OutputStream {
    private TextArea output;
    private OutputStream fdOut = new FileOutputStream(FileDescriptor.out);
    private PrintStream printStream = new PrintStream(new BufferedOutputStream(fdOut, 128), true);

    public ConsoleStream(TextArea ta) {
        this.output = ta;
    }

    @Override
    public void write(int i) throws IOException {
        Platform.runLater(() -> {
            printStream.print((char) i);
            output.appendText(String.valueOf((char) i));
        });
    }
}
