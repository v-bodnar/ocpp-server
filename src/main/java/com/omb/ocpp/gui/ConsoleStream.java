package com.omb.ocpp.gui;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ConsoleStream extends OutputStream {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleStream.class);
    private static final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();
    private static final int MAX_CONSOLE_SIZE = 1000;
    private final TextFlow textFlow;
    private final TextField textField;
    private OutputStream fdOut = new FileOutputStream(FileDescriptor.out);
    private PrintStream printStream = new PrintStream(new BufferedOutputStream(fdOut, 128), true);
    private StringBuilder stringBuilder = new StringBuilder();
    private String highlightString;
    private Pattern highlightRegex;
    private Color highlightColor = Color.MAGENTA;

    ConsoleStream(TextFlow textFlow, TextField textField) {
        this.textFlow = textFlow;
        this.textField = textField;
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
                text.setFill(getColor(string));
                if (string.contains("ERROR")) {
                    text.setStyle("-fx-font-weight: bold");
                }
                text.setOnMouseClicked(event -> {
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString(((Text) event.getSource()).getText());
                    CLIPBOARD.setContent(clipboardContent);
                    textField.setText(((Text) event.getSource()).getText());
                });
                textFlow.getChildren().add(text);
                if (textFlow.getChildren().size() > MAX_CONSOLE_SIZE) {
                    textFlow.getChildren().remove(0, MAX_CONSOLE_SIZE / 10);
                }
            }
        });
    }

    private Color getColor(String string) {
        if (highlightRegex != null && highlightRegex.matcher(string).find()) {
            return highlightColor;
        } else if (highlightString != null && !highlightString.isEmpty() && string.contains(highlightString)) {
            return highlightColor;
        } else if (string.contains("INFO")) {
            return Color.GREEN;
        } else if (string.contains("DEBUG")) {
            return Color.BLACK;
        } else if (string.contains("WARN")) {
            return Color.ORANGE;
        } else if (string.contains("ERROR")) {
            return Color.RED;
        } else {
            return Color.GRAY;
        }
    }

    void markLines(String key, boolean regex) {
        if (key == null || key.isEmpty()) {
            clearMarkup();
        } else if (regex) {
            markLinesRegex(key);
        } else {
            markLines(key);
        }
    }

    private void markLines(String key) {
        highlightString = key;
        textFlow.getChildren()
                .stream()
                .filter(node -> node instanceof Text)
                .forEach(node -> {
                    if (((Text) node).getText().contains(key)) {
                        ((Text) node).setFill(highlightColor);
                    } else {
                        ((Text) node).setFill(getColor(((Text) node).getText()));
                    }
                });
    }

    private void markLinesRegex(String key) {
        try {
            highlightRegex = Pattern.compile(key);
            textFlow.getChildren()
                    .stream()
                    .filter(node -> node instanceof Text)
                    .forEach(node -> {
                        if (highlightRegex.matcher(((Text) node).getText()).find()) {
                            ((Text) node).setFill(highlightColor);
                        } else {
                            ((Text) node).setFill(getColor(((Text) node).getText()));
                        }
                    });
        } catch (PatternSyntaxException e) {
            LOGGER.error("Wrong regex", e);
            highlightRegex = null;
        }
    }

    private void clearMarkup() {
        highlightString = null;
        highlightRegex = null;
        textFlow.getChildren()
                .stream()
                .filter(node -> node instanceof Text && ((Text) node).getFill().equals(highlightColor))
                .forEach(node -> ((Text) node).setFill(getColor(((Text) node).getText())));
    }

    public void copySelected(){
        String copiedText = textFlow.getChildren()
                .stream()
                .filter(node -> node instanceof Text && ((Text) node).getFill().equals(highlightColor))
                .map(node -> ((Text) node).getText())
                .collect(Collectors.joining());

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(copiedText);
        clipboard.setContent(content);
    }
}
