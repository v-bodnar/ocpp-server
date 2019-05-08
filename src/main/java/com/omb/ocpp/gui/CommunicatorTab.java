package com.omb.ocpp.gui;

import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.SessionsListener;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionRequest;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.omb.ocpp.gui.StubRequestsFactory.toRequest;

@Service
public class CommunicatorTab {
    private static final Logger logger = LoggerFactory.getLogger(CommunicatorTab.class);

    private final ListView<String> sessionsList = new ListView<>();
    private final TextField selectedClientField = new TextField();
    private final ComboBox<Class<? extends Request>> messageTypeCombo = new ComboBox<>();
    private final TextArea messageTextArea = new TextArea();
    private final Button sendButton = new Button("Send Message");
    private final Set<Class<? extends Request>> messagesAvailableForSend = getMessagesAvailableForSend();

    private final OcppServerService ocppServerService;

    @Inject
    public CommunicatorTab(ServiceLocator applicationContext) {
        this.ocppServerService = applicationContext.getService(OcppServerService.class);
    }

    public Tab constructTab() {
        Tab tab = new Tab();
        tab.setText("Communicator");
        tab.setClosable(false);

        ObservableList<String> items = FXCollections.observableArrayList(
                getSessionsListFormatted(ocppServerService.getSessionList()));
        sessionsList.setItems(items);
        sessionsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectedClientField.setText(newValue));
        ocppServerService.setSessionsListener(new GuiSessionsListener());

        Label label = new Label();
        label.setText("Selected client:");

        selectedClientField.setText("NONE");
        selectedClientField.setPrefWidth(500);
        selectedClientField.setEditable(false);

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(5));
        hBox1.setAlignment(Pos.CENTER_LEFT);
        hBox1.getChildren().addAll(label, selectedClientField);

        messageTypeCombo.setItems(FXCollections.observableArrayList(messagesAvailableForSend));
        messageTypeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Class<? extends Request> object) {
                if(object == null) return "";
                return object.getSimpleName();
            }

            @Override
            public Class<? extends Request> fromString(String string) {
                try {
                    return Class.forName(string).asSubclass(Request.class);
                } catch (ClassNotFoundException e) {
                    return SendLocalListRequest.class;
                }
            }
        });
        messageTypeCombo.setOnAction(event -> {
            if (messageTypeCombo.getValue() != null) {
                messageTextArea.setText(StubRequestsFactory.getStubRequest(messageTypeCombo.getValue()));
            }
        });

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(5));
        vBox.getChildren().addAll(hBox1, messageTypeCombo, messageTextArea, sendButton);
        vBox.setFillWidth(true);

        sendButton.setOnAction(event -> {
            if (selectedClientField.getText() != null
                    && !selectedClientField.getText().isEmpty()
                    && !selectedClientField.getText().equals("NONE")) {
                Optional<? extends Request> request = toRequest(messageTextArea.getText(), messageTypeCombo.getValue());
                if (request.isPresent()) {
                    ocppServerService.send(request.get(), selectedClientField.getText());
                } else {
                    logger.error("Request parsing error, request: {}", messageTextArea.getText());
                }
            } else {
                logger.error("Client to send for is not selected");
            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(sessionsList, vBox);

        tab.setContent(hBox);
        return tab;
    }

    private static Set<Class<? extends Request>> getMessagesAvailableForSend() {
        Set<Class<? extends Request>> messages = new LinkedHashSet<>();

        //Core profile messages
        messages.add(ChangeAvailabilityRequest.class);
        messages.add(ChangeConfigurationRequest.class);
        messages.add(ClearCacheRequest.class);
        messages.add(DataTransferRequest.class);
        messages.add(GetConfigurationRequest.class);
        messages.add(MeterValuesRequest.class);
        messages.add(RemoteStartTransactionRequest.class);
        messages.add(RemoteStopTransactionRequest.class);
        messages.add(ResetRequest.class);
        messages.add(UnlockConnectorRequest.class);

        //Firmware profile messages
        messages.add(DiagnosticsStatusNotificationRequest.class);
        messages.add(FirmwareStatusNotificationRequest.class);
        messages.add(GetDiagnosticsRequest.class);
        messages.add(UpdateFirmwareRequest.class);

        //LocalAuthList profile messages
        messages.add(GetLocalListVersionRequest.class);
        messages.add(SendLocalListRequest.class);

        //Remote trigger profile messages
        messages.add(TriggerMessageRequest.class);

        return messages;
    }


    private static List<String> getSessionsListFormatted(Map<UUID, SessionInformation> sessions) {
        return sessions.values().stream()
                .map(sessionInformation ->
                        String.format("%s (%s)", sessionInformation.getIdentifier(), sessionInformation.getAddress()))
                .collect(Collectors.toList());
    }

    private class GuiSessionsListener implements SessionsListener {
        @Override
        public void onSessionsCountChange(Map<UUID, SessionInformation> sessions) {
            Platform.runLater(() -> {
                ObservableList<String> items = FXCollections.observableArrayList(
                        getSessionsListFormatted(sessions));
                sessionsList.setItems(items);
            });
        }
    }
}


