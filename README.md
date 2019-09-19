# Java FX 12 GUI OCPP server

**Features**
* Application has two modes GUI/command line
* Allows communication with clients using OCPP protocol (Supports SSL/TLS)
* Allows dynamic change of OCPP server behavior 
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

**Download**
```
git clone https://github.com/v-bodnar/ocpp-server.git
git clone https://github.com/v-bodnar/GroovyOcppSupplier.git $GROOVY_PATH //@see section "Changing server behavior 
using Groovy"
```

**Build:** 
```
cd ocpp-server
gradle build
```

**Configuration:**  
You can use **OCPP_SERVER_HOME/ocpp-server.properties** to configure server

Supported keys:
 - application.gui.mode - Indicates that application should be started with/without GUI, 
 possible values:  'true/false', default: true 
 - ocpp.features.profile.list - List of features supported by server,
 default: Core,FirmwareManagement,RemoteTrigger,LocalAuthList
 - ocpp.server.ip - Ip on which server will accept OCPP connections, 
 default:0.0.0.0, works in combination with 'application.gui.mode:false'
 - ocpp.server.port - Port on which OCPP server will accept connections, 
 default:8887, works in combination with 'application.gui.mode:false'
 - rest.api.port - port on which REST server will accept connections, 
 default:9090, works in combination with 'application.gui.mode:false'
 - ssl.enabled - Run ssl server with ssl context
 works in combination with 'application.gui.mode:false'
 - ssl.keystore.uuid - if ssl enabled server will use keystore with given keystore uuid, 
 works in combination with 'application.gui.mode:false'
 - ssl.client.auth - Indicates if server needs to validate client certificate, 
 works in combination with 'application.gui.mode:false'

**Usage:**  
Before using set **OCPP_SERVER_HOME** - environment variable 
``` 
cd ocpp-server/build/libs/
java -jar ocpp-server-0.1.jar  
```
   
## Changing server behavior using Groovy
**$GROOVY_PATH = $OCPP_SERVER_HOME/groovy/**  
After the first start of ocpp-server app you will get exception:
```
xx:xx:xx.xxx [JavaFX Application Thread] ERROR com.omb.ocpp.groovy.GroovyService - Could not load groovy scripts
java.io.FileNotFoundException: Please execute "git clone https://github.com/v-bodnar/GroovyOcppSupplier.git 
$GROOVY_PATH"
```
So just execute the command above. It will clone new gradle project into the **$GROOVY_PATH** folder.
After it is done restart the application or click "Reload groovy scripts" on the "Groovy" tab in GUI.

From this point in time you can make changes to the cloned gradle project and dynamically reload scripts using 
"Reload groovy scripts" button on the "Groovy" tab in GUI or using REST api.

By implementing or changing classes that implement:
```
ConfirmationSupplier<REQUEST extends Request, RESPONSE extends Confirmation> 
```
you can change responses that ocpp server sends to clients dynamically on runtime.

## Secure connection using ssl
**SSL_PATH = $OCPP_SERVER_HOME/ssl**  
During startup Ocpp-server will create trust store SSL_PATH/trust-store.jks which is used for storing clients certificates
During startup Ocpp-server will create file SSL_PATH/keystore-certificates.config which is used for storing information 
about key-stores that contain server certificate, each server certificate will be stored in the separate key-store. 
Information about trust-store is also stored in this file.

If you want to use manually generated server certificate just add key-store that contains it to SSL_PATH and add 
information to keystore-certificates.config file. Example:
```
{
  "keystoreCertificatesConfig": [
    {
      "uuid": "b1ca37d9-3ee9-4b22-95d3-e976f02ff3fd",
      "keystorePassword": "f9a42ef7-7ce5-4dc3-8b15-d139f4dcd737",
      "keystorePath": "C:\\Work\\Shared\\OCPP_SERVER_HOME\\ocpp\\ssl\\b1ca37d9-3ee9-4b22-95d3-e976f02ff3fd.jks",
      "keystoreProtocol": "TLSv1.2"
    }
  ]
}    
```

**GUI mode:**
1. Go to tab "Server Certificates" click button "Generate certificate", this will generate new server certificate, 
table above will show information about new certificate, there you also can download it or delete.
2. If  client validation is needed go to tab "Client Certificates", click button "Upload certificate" and specify 
clients certificate. It will be added to the trust-store and table will be refreshed showing clients certificates in 
the trust store
3. Go to tab "General", select server certificate that you want to use, check "Validate client certificate" if needed.

**NO GUI mode:**
REST API exposes CRUD methods for managing server and client certificates. In NO GUI mode client certificates can 
only be uploaded using REST API or by manually adding certificate to SSL_PATH/trust-store.jks.

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
    
    @GET
    @Path("list-trust-store-aliases")
    public Response listClientCertificate()    
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("upload-client-cert")
    public Response uploadClientCertificate(@FormDataParam("file") InputStream uploadedInputStream,
                                            @FormDataParam("file") FormDataContentDisposition fileDetail)
                                                
    @POST
    @Path("generate-server-cert")
    public Response generateServerCertificate()
    
    @GET
    @Path("get-keystore-config")
    public Response getKeyStoreConfig()
    
    @DELETE
    @Path("delete-server-cert")
    public Response deleteServerCertificate(@QueryParam("uuid") String uuid)    

```
