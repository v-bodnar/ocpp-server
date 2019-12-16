package com.omb.ocpp.server;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HandshakeResolver implements eu.chargetime.ocpp.HandshakeResolver{
    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeResolver.class);
    private final String basicPassword;

    public HandshakeResolver(String basicPassword) {
        this.basicPassword = basicPassword;
    }

    public void onHandshake(ClientHandshake request) throws InvalidDataException {
        String authString = request.getFieldValue("Authorization");
        if (basicPassword != null && !basicPassword.isEmpty() && authString != null && !authString.isEmpty() && !decode(authString).equals(basicPassword)) {
            LOGGER.error("Handshake failed because passwords do not match provided: {} expected: {}",
                    decode(authString), basicPassword);
            throw new InvalidDataException(CloseFrame.REFUSE, "Authorization Failed!");
        }
    }

    private String decode(String basicPassword) {
        String base64Credentials = basicPassword.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        return new String(credDecoded, StandardCharsets.UTF_8).split(":", 2)[1];
    }

}
