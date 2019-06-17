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

**Download**
```
git clone https://github.com/v-bodnar/ocpp-server.git
```

**Build:** 
```
cd ocpp-server
gradle build
```

**Usage:**  
Before using set **LITHOS_HOME** - environment variable 
``` 
cd ocpp-server/build/libs/
java -jar ocpp-server-0.1.jar <arguments>  
```
**Arguments:**  
 - -h,--help  print this message  
 - -nogui,--nogui  indicates that application should be started dsfdsthout GUI.  
 - -ip,--ip <arg>  the ip on which server will accept OCPP and REST connections, default:127.0.0.1, works in 
 combination with -nogui  
 - -ocppPort,--ocppPort <arg>  port on which OCPP server will accept connections, default:8887, works in combination 
 with -nogui  
 - --restPort <arg>  port on which REST server will accept connections, default:9090, works in combination with -nogui
 - -keystoreUUID,--keystoreUUID <arg>                               run ssl server with keystore for defined keystore
   uuid  
 - -clientAuthenticationNeeded,--clientAuthenticationNeeded <arg>   should server needed for client certificate
 - -keystoreCiphers,--keystoreCiphers <arg>                         list of keystore ciphers separated by comma
 - -createKeystoreCertificate                                       Create new keystore certificate
 - -deleteKeystoreCertificate <arg>                                 Delete keystore certificate
 - -showKeystoreConfig                                              Show keystore config file content

## Changing server behavior using Groovy
**$GROOVY_PATH = $LITHOS_HOME/ocpp/groovy/**  
Groovy files will be created under path: **$GROOVY_PATH**  
By creating groovy classes inside **$GROOVY_PATH** that implement:
```
ConfirmationSupplier<REQUEST extends Request, RESPONSE extends Confirmation> 
```
you can change responses that ocpp server sends to clients. Using GUI reload those classes on runtime. 
 
Also you can upload .groovy files using REST API, it will replace files with the same names and automatically load 
classes to classloader

## Secure connection using ssl
**SSL_PATH = $LITHOS_HOME/ocpp/ssl**  
During startup Lithos will create trust store SSL_PATH/trust-store.jks which is used for storing clients certificates
During startup Lithos will create file SSL_PATH/keystore-certificates.config which is used for storing information 
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
      "keystorePath": "C:\\Work\\Shared\\LITHOS_HOME\\ocpp\\ssl\\b1ca37d9-3ee9-4b22-95d3-e976f02ff3fd.jks",
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
* To create server certificate 
```
java -jar ocpp-server-0.1.jar -createKeystoreCertificate
```
* To list server certificates ids
```
java -jar ocpp-server-0.1.jar -showKeystoreConfig
```
* To run using one of certificates
``` 
-jar ocpp-server-0.1.jar -nogui -keystoreUUID uuid-shown-by-previous-command -clientAuthenticationNeeded 
```

REST API also exposes CRUD methods for managing server and client certificates. In NO GUI mode client certificates can 
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
