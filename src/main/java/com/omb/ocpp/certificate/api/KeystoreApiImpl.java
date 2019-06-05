package com.omb.ocpp.certificate.api;

import com.omb.ocpp.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;
import com.omb.ocpp.certificate.service.CreateKeystoreCertificateService;
import com.omb.ocpp.certificate.service.CreateOrGetKeystoreCertificatesConfigService;
import com.omb.ocpp.certificate.service.DeleteKeystoreCertificateConfigService;
import com.omb.ocpp.certificate.service.GetKeyStoreDetailsService;

import java.security.KeyStore;
import java.util.List;
import java.util.UUID;

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
    public synchronized KeystoreCertificateConfig getKeystoreCertificateConfig(UUID keystoreUUID) throws Exception {
        return getKeystoreCertificatesConfig().getKeystoreCertificateConfig(keystoreUUID);
    }

    @Override
    public synchronized void deleteKeystoreCertificate(UUID keystoreUUID) throws Exception {
        DeleteKeystoreCertificateConfigService service = new DeleteKeystoreCertificateConfigService(this, keystoreUUID);
        service.execute();
    }

    @Override
    public List<KeyStore> getKeyStores() throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.execute();
    }

    @Override
    public synchronized KeyStore getKeyStores(UUID keystoreUUID) throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.execute(keystoreUUID);
    }

    @Override
    public synchronized List<KeyStore> getKeyStores(List<UUID> keystoreUUIDs) throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.execute(keystoreUUIDs);
    }
}
