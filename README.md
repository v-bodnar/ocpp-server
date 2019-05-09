Java FX 12 GUI OCPP server

Requirements:
* Java 11 +
* Gradle 4.2 +

Main dependencies:
* Java FX 12
* OCPP library (https://github.com/v-bodnar/Java-OCA-OCPP)
* Jetty
* Jersey
* Jackson

Groovy files will be created under path: LITHOS_HOME/ocpp/groovy.   
LITHOS_HOME - environment variable  

usage:   
gradle clean build  
java build/libs/ocpp-server-0.1.jar <args>  
 -h,--help  print this message  
 -nogui,--nogui  indicates that application should be started dsfdsthout GUI.  
 -ip,--ip <arg>  the ip on which server will accept OCPP and REST connections, default:127.0.0.1, works in combination with -nogui  
 -ocppPort,--ocppPort <arg>  port on which OCPP server will acceptconnections, default:8887, works incombination with -nogui  
 --restPort <arg>  port on which REST server will acceptconnections, default:9090, works incombination with -nogui  
  
Rest api:  
	@Produces(MediaType.APPLICATION_JSON)  
	@Consumes(MediaType.APPLICATION_JSON)  
	@Path("/")  
	
	  @Path("send-reset-request")  
    public Response sendResetRequest(ResetRequest resetRequest)  
  
    @POST  
    @Path("send-get-diagnostics")  
    public Response sendGetDiagnostics(GetDiagnosticsRequest getDiagnosticsRequest)  
  
    @POST  
    @Path("send-change-availability-request")  
    public Response sendChangeAvailabilityRequest(ChangeAvailabilityRequest changeAvailabilityRequest)   
  
    @POST  
    @Path("send-change-configuration-request")  
    public Response sendChangeConfigurationRequest(ChangeConfigurationRequest changeConfigurationRequest)   
  
    @POST  
    @Path("send-clear-cache-request")
    public Response sendClearCacheRequest(ClearCacheRequest clearCacheRequest) 

    @POST
    @Path("send-data-transfer-request")
    public Response sendDataTransferRequest(DataTransferRequest dataTransferRequest)

    @POST
    @Path("send-get-configuration-request")
    public Response sendGetConfigurationRequest(GetConfigurationRequest getConfigurationRequest)

    @POST
    @Path("send-remote-start-transaction-request")
    public Response sendRemoteStartTransactionRequest(RemoteStartTransactionRequest remoteStartTransactionRequest)

    @POST
    @Path("send-remote-stop-transaction-request")
    public Response sendRemoteStopTransactionRequest(RemoteStopTransactionRequest remoteStopTransactionRequest) 


    @POST
    @Path("send-unlock-connector-request")
    public Response sendUnlockConnectorRequest(UnlockConnectorRequest unlockConnectorRequest)

    @POST
    @Path("send-diagnostics-status-notification-request")
    public Response sendDiagnosticsStatusNotificationRequest(DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest)

    @POST
    @Path("send-firmware-status-notification-request")
    public Response sendFirmwareStatusNotificationRequest(FirmwareStatusNotificationRequest firmwareStatusNotificationRequest)

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("upload-confirmation-supplier")
    public Response uploadConfirmationSupplier(@FormDataParam("file") InputStream uploadedInputStream,
                                               @FormDataParam("file") FormDataContentDisposition fileDetail)




OCPP Server can work with SSL Context.
If we want to have SSL on we should add file to ${LITHOS_HOME}\ocpp\ssl\ssl.properties with content:

keystore.password=OCPPCaPass
keystore.protocol=TLSv1.2
keystore.path=C:\\Users\\plmazeb\\Tools\\OCPP-PKI_20190213\\keystores\\ocppCsSrvKeystore.jks

If file ssl.properties not exists Server run without any ssl context
