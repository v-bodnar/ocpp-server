package com.omb.ocpp.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.groovy.GroovyService;
import com.omb.ocpp.gui.Application;
import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.security.certificate.service.TrustStoreService;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.iso15118.dto.InstallCertificateRequest;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
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
import eu.chargetime.ocpp.model.smartcharging.ClearChargingProfileRequest;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);
    private final OcppServerService ocppServerService = Application.APPLICATION.getService(OcppServerService.class);
    private final GroovyService groovyService = Application.APPLICATION.getService(GroovyService.class);
    private final KeystoreApi keystoreApi = Application.APPLICATION.getService(KeystoreApi.class);
    private final TrustStoreService trustStoreService = Application.APPLICATION.getService(TrustStoreService.class);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @POST
    @Path("send-reset-request")
    public Response sendResetRequest(@QueryParam("userName") String userName, ResetRequest resetRequest) {
        return sendRequest(resetRequest, userName);
    }

    @POST
    @Path("send-get-diagnostics")
    public Response sendGetDiagnostics(@QueryParam("userName") String userName, GetDiagnosticsRequest getDiagnosticsRequest) {
        return sendRequest(getDiagnosticsRequest, userName);
    }

    @POST
    @Path("send-change-availability-request")
    public Response sendChangeAvailabilityRequest(@QueryParam("userName") String userName, ChangeAvailabilityRequest changeAvailabilityRequest) {
        return sendRequest(changeAvailabilityRequest, userName);
    }

    @POST
    @Path("send-change-configuration-request")
    public Response sendChangeConfigurationRequest(@QueryParam("userName") String userName, ChangeConfigurationRequest changeConfigurationRequest) {
        return sendRequest(changeConfigurationRequest, userName);
    }

    @POST
    @Path("send-clear-cache-request")
    public Response sendClearCacheRequest(@QueryParam("userName") String userName, ClearCacheRequest clearCacheRequest) {
        return sendRequest(clearCacheRequest, userName);
    }

    @POST
    @Path("send-data-transfer-request")
    public Response sendDataTransferRequest(@QueryParam("userName") String userName, DataTransferRequest dataTransferRequest) {
        return sendRequest(dataTransferRequest, userName);
    }

    @POST
    @Path("send-get-configuration-request")
    public Response sendGetConfigurationRequest(@QueryParam("userName") String userName, GetConfigurationRequest getConfigurationRequest) {
        return sendRequest(getConfigurationRequest, userName);
    }

    @POST
    @Path("send-remote-start-transaction-request")
    public Response sendRemoteStartTransactionRequest(@QueryParam("userName") String userName, 
                                                      RemoteStartTransactionRequest remoteStartTransactionRequest) {
        return sendRequest(remoteStartTransactionRequest, userName);
    }

    @POST
    @Path("send-remote-stop-transaction-request")
    public Response sendRemoteStopTransactionRequest(@QueryParam("userName") String userName, 
                                                     RemoteStopTransactionRequest remoteStopTransactionRequest) {
        return sendRequest(remoteStopTransactionRequest, userName);
    }

    @POST
    @Path("send-unlock-connector-request")
    public Response sendUnlockConnectorRequest(@QueryParam("userName") String userName, UnlockConnectorRequest unlockConnectorRequest) {
        return sendRequest(unlockConnectorRequest, userName);
    }

    @POST
    @Path("send-diagnostics-status-notification-request")
    public Response sendDiagnosticsStatusNotificationRequest(@QueryParam("userName") String userName, 
                                                             DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest) {
        return sendRequest(diagnosticsStatusNotificationRequest, userName);
    }

    @POST
    @Path("send-firmware-status-notification-request")
    public Response sendFirmwareStatusNotificationRequest(@QueryParam("userName") String userName, 
                                                          FirmwareStatusNotificationRequest firmwareStatusNotificationRequest) {
        return sendRequest(firmwareStatusNotificationRequest, userName);
    }

    @POST
    @Path("send-set-charging-profile-request")
    public Response sendSetChargingProfileRequest(@QueryParam("userName") String userName, 
                                                  SetChargingProfileRequest setChargingProfileRequest) {
        return sendRequest(setChargingProfileRequest, userName);
    }

    @POST
    @Path("send-clear-charging-profile-request")
    public Response sendClearChargingProfileRequest(@QueryParam("userName") String userName, 
                                                    ClearChargingProfileRequest clearChargingProfileRequest) {
        return sendRequest(clearChargingProfileRequest, userName);
    }

    @POST
    @Path("send-install-certificate-request")
    public Response sendInstallCertificateRequest(@QueryParam("userName") String userName, InstallCertificateRequest installCertificateRequest) {
        return sendRequest(installCertificateRequest, userName);
    }

    @POST
    @Path("send-trigger-message-request")
    public Response sendTriggerMessageRequest(@QueryParam("userName") String userName, 
                                              TriggerMessageRequest triggerMessageRequest) {
        return sendRequest(triggerMessageRequest, userName);
    }

    @POST
    @Path("send-all-reset-request")
    public Response sendAllResetRequest(ResetRequest resetRequest) {
        return sendRequestToAll(resetRequest);
    }

    @POST
    @Path("send-all-get-diagnostics")
    public Response sendToAllGetDiagnostics(GetDiagnosticsRequest getDiagnosticsRequest) {
        return sendRequestToAll(getDiagnosticsRequest);
    }

    @POST
    @Path("send-all-change-availability-request")
    public Response sendToAllChangeAvailabilityRequest(ChangeAvailabilityRequest changeAvailabilityRequest) {
        return sendRequestToAll(changeAvailabilityRequest);
    }

    @POST
    @Path("send-all-change-configuration-request")
    public Response sendToAllChangeConfigurationRequest(ChangeConfigurationRequest changeConfigurationRequest) {
        return sendRequestToAll(changeConfigurationRequest);
    }

    @POST
    @Path("send-all-clear-cache-request")
    public Response sendToAllClearCacheRequest(ClearCacheRequest clearCacheRequest) {
        return sendRequestToAll(clearCacheRequest);
    }

    @POST
    @Path("send-all-data-transfer-request")
    public Response sendToAllDataTransferRequest(DataTransferRequest dataTransferRequest) {
        return sendRequestToAll(dataTransferRequest);
    }

    @POST
    @Path("send-all-get-configuration-request")
    public Response sendToAllGetConfigurationRequest(GetConfigurationRequest getConfigurationRequest) {
        return sendRequestToAll(getConfigurationRequest);
    }

    @POST
    @Path("send-all-remote-start-transaction-request")
    public Response sendToAllRemoteStartTransactionRequest(RemoteStartTransactionRequest remoteStartTransactionRequest) {
        return sendRequestToAll(remoteStartTransactionRequest);
    }

    @POST
    @Path("send-all-remote-stop-transaction-request")
    public Response sendToAllRemoteStopTransactionRequest(RemoteStopTransactionRequest remoteStopTransactionRequest) {
        return sendRequestToAll(remoteStopTransactionRequest);
    }


    @POST
    @Path("send-all-unlock-connector-request")
    public Response sendToAllUnlockConnectorRequest(UnlockConnectorRequest unlockConnectorRequest) {
        return sendRequestToAll(unlockConnectorRequest);
    }

    @POST
    @Path("send-all-diagnostics-status-notification-request")
    public Response sendToAllDiagnosticsStatusNotificationRequest(DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest) {
        return sendRequestToAll(diagnosticsStatusNotificationRequest);
    }

    @POST
    @Path("send-all-firmware-status-notification-request")
    public Response sendToAllFirmwareStatusNotificationRequest(FirmwareStatusNotificationRequest firmwareStatusNotificationRequest) {
        return sendRequestToAll(firmwareStatusNotificationRequest);
    }

    @POST
    @Path("send-all-set-charging-profile-request")
    public Response sendToAllSetChargingProfileRequest(SetChargingProfileRequest setChargingProfileRequest) {
        return sendRequestToAll(setChargingProfileRequest);
    }

    @POST
    @Path("send-all-clear-charging-profile-request")
    public Response sendToAllClearChargingProfileRequest(ClearChargingProfileRequest clearChargingProfileRequest) {
        return sendRequestToAll(clearChargingProfileRequest);
    }

    @POST
    @Path("send-all-trigger-message-request")
    public Response sendToAllTriggerMessageRequest(TriggerMessageRequest triggerMessageRequest) {
        return sendRequestToAll(triggerMessageRequest);
    }

    @POST
    @Path("send-all-install-certificate-request")
    public Response sendToAllInstallCertificateRequest(InstallCertificateRequest installCertificateRequest) {
        return sendRequestToAll(installCertificateRequest);
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
            trustStoreService.addClientCertificate(uploadedInputStream);
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.error("Could not upload client certificate", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @DELETE
    @Path("delete-client-cert")
    public Response deleteClientCertificate(@QueryParam("alias") String alias) {
        if (alias == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            trustStoreService.deleteClientCertificate(alias);
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.error("Could not delete client certificate", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("list-trust-store-aliases")
    public Response listClientCertificate() {
        Optional<List<String>> aliases = trustStoreService.listAliases();
        if (aliases.isPresent()) {
            return Response.ok(aliases.get()).build();
        } else {
            LOGGER.error("Could not list trust store aliases");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Could not list trust store aliases").build();
        }
    }

    @GET
    @Path("download-server-cert")
    public Response downloadServerCertificate(@QueryParam("uuid") String uuid) {
        try {
            String certificate = keystoreApi.getServerCertificatePem(UUID.fromString(uuid));
            StreamingOutput fileStream = output -> {
                output.write(certificate.getBytes());
                output.flush();
            };
            return Response
                    .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = server.pem")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    String.format("Server certificate does not exist, error: %s", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("delete-server-cert")
    public Response deleteServerCertificate(@QueryParam("uuid") String uuid) {
        try {
            keystoreApi.deleteKeystoreCertificate(UUID.fromString(uuid));
            return Response
                    .ok()
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    String.format("Could not delete certificate, error: %s", e.getMessage())).build();
        }
    }

    @POST
    @Path("generate-server-cert")
    public Response generateServerCertificate() {
        try {
            KeystoreCertificateConfig keystoreCertificateConfig = keystoreApi.createKeystoreCertificate();
            return Response
                    .ok(gson.toJson(keystoreCertificateConfig))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    String.format("Could not delete certificate, error: %s", e.getMessage())).build();
        }
    }

    @GET
    @Path("get-keystore-config")
    public Response getKeyStoreConfig() {
        try {
            List<KeystoreCertificateConfig> configList =
                    keystoreApi.getKeystoreConfigRegistry().getKeystoreCertificatesConfig();
            return Response
                    .ok(gson.toJson(configList))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    String.format("Server certificate does not exist, error: %s", e.getMessage())).build();
        }
    }

    private Response sendRequestToAll(Request request) {
        try {
            List<MultiClientResponse> responses = new LinkedList<>();
            Map<UUID, CompletionStage<Confirmation>> responsePromises = ocppServerService.sendToAll(request);
            for (Map.Entry<UUID, CompletionStage<Confirmation>> entry : responsePromises.entrySet()) {
                responses.add(new MultiClientResponse(entry.getKey(), entry.getValue().toCompletableFuture().get()));
            }

            return Response.ok().entity(responses).build();
        } catch (NotConnectedException | OccurenceConstraintException | UnsupportedFeatureException | InterruptedException | ExecutionException e) {
            LOGGER.error("Could not send request", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    private Response sendRequest(Request request) {
        try {
            return Response.ok().entity(ocppServerService.send(request).toCompletableFuture().get()).build();
        } catch (NotConnectedException | OccurenceConstraintException | UnsupportedFeatureException | InterruptedException | ExecutionException e) {
            LOGGER.error("Could not send request", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    private Response sendRequest(Request request, String username) {
        try {
            if (username == null) {
                return sendRequest(request);
            } else {
                return Response.ok().entity(ocppServerService.sendToClient(request, username).toCompletableFuture().get()).build();
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Could not send request", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }

    static class MultiClientResponse {
        private final UUID uuid;
        private final Confirmation confirmation;

        public MultiClientResponse(UUID uuid, Confirmation confirmation) {
            this.uuid = uuid;
            this.confirmation = confirmation;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Confirmation getConfirmation() {
            return confirmation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MultiClientResponse that = (MultiClientResponse) o;
            return Objects.equals(uuid, that.uuid) &&
                    Objects.equals(confirmation, that.confirmation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid, confirmation);
        }
    }
}
