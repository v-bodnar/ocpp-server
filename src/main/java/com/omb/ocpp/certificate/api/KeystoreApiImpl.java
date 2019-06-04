package com.omb.ocpp.certificate.api;

import com.omb.ocpp.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;
import com.omb.ocpp.certificate.service.CreateKeystoreCertificateService;
import com.omb.ocpp.certificate.service.CreateOrGetKeystoreCertificatesConfigService;
import com.omb.ocpp.certificate.service.DeleteKeystoreCertificateConfigService;
import org.jvnet.hk2.annotations.Service;

import java.util.UUID;

@Service
public class KeystoreApiImpl implements KeystoreApi {

    @Override
    public synchronized KeystoreCertificateConfig createKeystoreCertificate() throws Exception {
        CreateKeystoreCertificateService service = new CreateKeystoreCertificateService(this);
        return service.execute();
    }

    @Override
    public synchronized KeystoreCertificatesConfig getKeystoreCertificatesConfig() throws Exception {
        CreateOrGetKeystoreCertificatesConfigService service = new CreateOrGetKeystoreCertificatesConfigService();
        return service.execute();
    }

    @Override
    public synchronized Boolean deleteKeystoreCertificate(UUID keystoreUUID) throws Exception {
        DeleteKeystoreCertificateConfigService service = new DeleteKeystoreCertificateConfigService(this, keystoreUUID);
        return service.execute();
    }
}
