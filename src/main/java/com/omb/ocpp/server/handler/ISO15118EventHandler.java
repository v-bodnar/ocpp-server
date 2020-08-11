package com.omb.ocpp.server.handler;

import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.server.iso15118.dto.AuthorizeRequest;
import com.omb.ocpp.server.iso15118.dto.AuthorizeResponse;
import com.omb.ocpp.server.iso15118.dto.Get15118EVCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.Get15118EVCertificateResponse;
import com.omb.ocpp.server.iso15118.dto.certificate.signing.SignCertificateRequestSupport;
import com.omb.ocpp.server.iso15118.dto.SignCertificateResponse;
import com.omb.ocpp.server.iso15118.dto.SignedUpdateFirmwareRequest;
import com.omb.ocpp.server.iso15118.dto.SignedUpdateFirmwareResponse;
import eu.chargetime.ocpp.JSONCommunicator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static com.omb.ocpp.server.handler.CoreEventHandler.RECEIVED_REQUEST;

@Service
public class ISO15118EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ISO15118EventHandler.class);
    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);
    private final GroovyService groovyService;

    @Inject
    public ISO15118EventHandler(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    public AuthorizeResponse handleAuthorizeRequest(UUID uuid, AuthorizeRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(uuid, request);
    }

    public Get15118EVCertificateResponse handleGetCertificateRequest(UUID uuid, Get15118EVCertificateRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(uuid, request);
    }

    public SignCertificateResponse handleSignCertificateRequest(UUID uuid, SignCertificateRequestSupport request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(uuid, request);
    }

    public SignedUpdateFirmwareResponse handleSignedUpdateFirmwareRequest(UUID uuid, SignedUpdateFirmwareRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(uuid, request);
    }
}
