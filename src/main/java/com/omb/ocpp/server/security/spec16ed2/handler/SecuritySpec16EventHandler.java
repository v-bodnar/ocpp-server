package com.omb.ocpp.server.security.spec16ed2.handler;

import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto.SignCertificateRequest;
import com.omb.ocpp.server.security.spec16ed2.sign.certificate.dto.SignCertificateResponse;
import eu.chargetime.ocpp.JSONCommunicator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

import javax.inject.Inject;

import static com.omb.ocpp.server.handler.CoreEventHandler.RECEIVED_REQUEST;

@Service
public class SecuritySpec16EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecuritySpec16EventHandler.class);

    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);
    private final GroovyService groovyService;

    @Inject
    public SecuritySpec16EventHandler(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    public SignCertificateResponse handleSignCertificateRequest(UUID uuid, SignCertificateRequest request) {
        LOGGER.debug(RECEIVED_REQUEST, request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(uuid, request);
    }
}
