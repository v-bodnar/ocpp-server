package com.omb.ocpp.server.handler;

import com.omb.ocpp.groovy.GroovyService;
import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.feature.profile.ClientRemoteTriggerHandler;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Service
public class RemoteTriggerEventHandler implements ClientRemoteTriggerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteTriggerEventHandler.class);
    private final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);
    private final GroovyService groovyService;

    @Inject
    public RemoteTriggerEventHandler(GroovyService groovyService) {
        this.groovyService = groovyService;
    }

    @Override
    public TriggerMessageConfirmation handleTriggerMessageRequest(TriggerMessageRequest request) {
        LOGGER.debug("{} - {}", request.getClass().getSimpleName(), jsonCommunicator.packPayload(request));
        return groovyService.getConfirmation(null, request);
    }


}