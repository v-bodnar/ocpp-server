package com.omb.ocpp.rest;

import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.gui.Application;
import com.omb.ocpp.security.KeyChainGenerator;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.SslKeyStoreConfig;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Optional;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class);
    private final GroovyService groovyService = Application.APPLICATION.getService(GroovyService.class);
    private final SslKeyStoreConfig sslKeyStoreConfig = Application.APPLICATION.getService(SslKeyStoreConfig.class);

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

    @POST
    @Path("send-clear-cache-request")
    public Response sendClearCacheRequest(ClearCacheRequest clearCacheRequest) {
        return sendRequest(clearCacheRequest);
    }

    @POST
    @Path("send-data-transfer-request")
    public Response sendDataTransferRequest(DataTransferRequest dataTransferRequest) {
        return sendRequest(dataTransferRequest);
    }

    @POST
    @Path("send-get-configuration-request")
    public Response sendGetConfigurationRequest(GetConfigurationRequest getConfigurationRequest) {
        return sendRequest(getConfigurationRequest);
    }

    @POST
    @Path("send-remote-start-transaction-request")
    public Response sendRemoteStartTransactionRequest(RemoteStartTransactionRequest remoteStartTransactionRequest) {
        return sendRequest(remoteStartTransactionRequest);
    }

    @POST
    @Path("send-remote-stop-transaction-request")
    public Response sendRemoteStopTransactionRequest(RemoteStopTransactionRequest remoteStopTransactionRequest) {
        return sendRequest(remoteStopTransactionRequest);
    }


    @POST
    @Path("send-unlock-connector-request")
    public Response sendUnlockConnectorRequest(UnlockConnectorRequest unlockConnectorRequest) {
        return sendRequest(unlockConnectorRequest);
    }

    @POST
    @Path("send-diagnostics-status-notification-request")
    public Response sendDiagnosticsStatusNotificationRequest(DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest) {
        return sendRequest(diagnosticsStatusNotificationRequest);
    }

    @POST
    @Path("send-firmware-status-notification-request")
    public Response sendFirmwareStatusNotificationRequest(FirmwareStatusNotificationRequest firmwareStatusNotificationRequest) {
        return sendRequest(firmwareStatusNotificationRequest);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("upload-confirmation-supplier")
    public Response uploadConfirmationSupplier(@FormDataParam("file") InputStream uploadedInputStream,
                                               @FormDataParam("file") FormDataContentDisposition fileDetail) {
        if (uploadedInputStream == null || fileDetail == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            groovyService.uploadGroovyScript(uploadedInputStream, fileDetail.getFileName());
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.error("Could not upload confirmation supplier", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("upload-client-cert")
    public Response uploadClientCertificate(@FormDataParam("file") InputStream uploadedInputStream,
                                            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        if (uploadedInputStream == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            KeyChainGenerator.saveClientCertificateInKeyStore(sslKeyStoreConfig, uploadedInputStream);
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.error("Could not upload client certificate", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("download-server-cert")
    public Response downloadServerCertificate() {
        Optional<Certificate> certificate = KeyChainGenerator.getCertificate(sslKeyStoreConfig);
        if (certificate.isPresent()) {
            StreamingOutput fileStream = output -> {
                try {
                    output.write(Base64.getEncoder().encode(certificate.get().getEncoded()));
                    output.flush();
                } catch (CertificateEncodingException e) {
                    LOGGER.error("Could not write server certificate", e);
                }
            };
            return Response
                    .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = server.cer")
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Server certificate does not exist").build();
        }

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
