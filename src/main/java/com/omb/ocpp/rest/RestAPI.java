package com.omb.ocpp.rest;

import com.omb.ocpp.server.OcppServerService;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);
    private final OcppServerService ocppServerService;

    @Inject
    public RestAPI(OcppServerService ocppServerService) {
        this.ocppServerService = ocppServerService;
    }

    @POST
    @Path("send-reset-request")
    public Response sendResetRequest(ResetRequest resetRequest) {
        return sendRequest(resetRequest);
    }

    @POST
    @Path("send-get-diagnostics")
    public Response sendGetDiagnostics(GetDiagnosticsRequest getDiagnosticsRequest) {
        return sendRequest(getDiagnosticsRequest);
    }

    @POST
    @Path("send-change-availability-request")
    public Response sendChangeAvailabilityRequest(ChangeAvailabilityRequest changeAvailabilityRequest) {
        return sendRequest(changeAvailabilityRequest);
    }

    @POST
    @Path("send-change-configuration-request")
    public Response sendChangeConfigurationRequest(ChangeConfigurationRequest changeConfigurationRequest) {
        return sendRequest(changeConfigurationRequest);
    }

    private Response sendRequest(Request request) {
        try {
            ocppServerService.sendToAll(request);
            return Response.ok().build();
        } catch (NotConnectedException | OccurenceConstraintException | UnsupportedFeatureException e) {
            LOGGER.error("Could not send request", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }
}
