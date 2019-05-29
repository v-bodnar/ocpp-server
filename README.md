# Java FX 12 GUI OCPP server

**Features**
* Application has two modes GUI/command line
* Allows communication with clients using OCPP protocol (Supports SSL/TLS)
* Exposes REST API to control OCPP communication

**Requirements:**
* Java 11 +
* Gradle 4.2 +

**Main dependencies:**
* Java FX 12
* OCPP library (https://github.com/v-bodnar/Java-OCA-OCPP)
* Jetty
* Jersey
* Jackson
* Bouncy Castle

Build: 
gradle clean build

Usage:  
set LITHOS_HOME - environment variable  
java -jar build/libs/ocpp-server-0.1.jar <args>  
 - -h,--help  print this message  
 - -nogui,--nogui  indicates that application should be started dsfdsthout GUI.  
 - -ip,--ip <arg>  the ip on which server will accept OCPP and REST connections, default:127.0.0.1, works in 
 combination with -nogui  
 - -ocppPort,--ocppPort <arg>  port on which OCPP server will acceptconnections, default:8887, works incombination 
 with -nogui  
 - --restPort <arg>  port on which REST server will acceptconnections, default:9090, works incombination with -nogui  
  
## Changing server behavior using Groovy
$GROOVY_PATH = $LITHOS_HOME/ocpp/groovy/  
Groovy files will be created under path: $GROOVY_PATH  
By creating groovy classes inside $GROOVY_PATH that implement:
```
ConfirmationSupplier<REQUEST extends Request, RESPONSE extends Confirmation> 
```
you can change responses that ocpp server sends to clients. Using GUI reload those classes on runtime. 
 
Also you can upload .groovy files using REST api, it will replace files with the same names and automatically load 
classes to classloader


## Setting up ssl configuration
Create file $LITHOS_HOME\ocpp\ssl\ssl.properties with content:
```
keystore.password=yourKeystorePassword  
keystore.protocol=TLSv1.1|TLSv1.2  
client.authentication.needed=false|true  
keystore.ciphers=define encryption  
```

If file ssl.properties not exists Server run without any ssl context

Server will generate self-signed certificate during startup, you can download it using GUI or REST API. Client have to
 import this certificate to truststore or allow untrusted certificates.  
If client.authentication.needed was set to true, clients certificate has to be added to truststore. This can be done 
using JDK keytool. GUI/REST API also support clients certificate upload with the limit that only single certificate can 
be uploaded.
 
## REST API 
```
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

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("upload-client-cert")
    public Response uploadClientCertificate(@FormDataParam("file") InputStream uploadedInputStream,
                                            @FormDataParam("file") FormDataContentDisposition fileDetail)
                                            
    @GET
    @Path("download-server-cert")
    public Response downloadServerCertificate()

```
