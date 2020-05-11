package com.omb.ocpp.security.certificate.api;

import com.omb.ocpp.config.Config;
import com.omb.ocpp.config.ConfigKey;
import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.security.certificate.config.KeystoreConfigRegistry;
import com.omb.ocpp.security.certificate.service.CreateKeystoreCertificateService;
import com.omb.ocpp.security.certificate.service.CreateOrGetKeystoreCertificatesConfigService;
import com.omb.ocpp.security.certificate.service.DeleteKeystoreCertificateConfigService;
import com.omb.ocpp.security.certificate.service.GetKeyStoreDetailsService;
import com.omb.ocpp.security.certificate.service.InitializeSslContextService;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.omb.ocpp.security.certificate.KeystoreConstants.OCPP_SERVER_CERT;
import static com.omb.ocpp.security.certificate.KeystoreConstants.OCPP_SERVER_PRIVATE_KEY;

public class KeystoreApiImpl implements KeystoreApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeystoreApiImpl.class);

    private Consumer<Void> certChangeListener = aVoid -> LOGGER.debug("No listeners attached");

    @Inject
    private Config config;

    @Override
    public synchronized KeystoreCertificateConfig createKeystoreCertificate() throws Exception {
        CreateKeystoreCertificateService service = new CreateKeystoreCertificateService(this);
        KeystoreCertificateConfig keystoreCertificateConfig = service.execute();
        certChangeListener.accept(null);
        return keystoreCertificateConfig;
    }

    @Override
    public synchronized KeystoreConfigRegistry getKeystoreConfigRegistry() throws Exception {
        CreateOrGetKeystoreCertificatesConfigService service = new CreateOrGetKeystoreCertificatesConfigService();
        return service.execute();
    }

    @Override
    public synchronized KeystoreCertificateConfig getKeystoreCertificateConfig(UUID keystoreUUID) throws Exception {
        return getKeystoreConfigRegistry().getKeystoreCertificateConfig(keystoreUUID);
    }

    @Override
    public synchronized KeystoreCertificateConfig getTrustStoreCertificateConfig() throws Exception {
        return getKeystoreConfigRegistry().getTrustStoreConfig();
    }

    @Override
    public synchronized void deleteKeystoreCertificate(UUID keystoreUUID) throws Exception {
        DeleteKeystoreCertificateConfigService service = new DeleteKeystoreCertificateConfigService(this, keystoreUUID);
        service.execute();
        certChangeListener.accept(null);
    }

    @Override
    public List<KeyStore> getKeyStores() throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getKeyStores();
    }

    @Override
    public synchronized KeyStore getKeyStores(UUID keystoreUUID) throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getKeyStores(keystoreUUID);
    }

    @Override
    public synchronized List<KeyStore> getKeyStores(List<UUID> keystoreUUIDs) throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getKeyStores(keystoreUUIDs);
    }

    @Override
    public synchronized KeyStore getTrustStore() throws Exception {
        GetKeyStoreDetailsService service = new GetKeyStoreDetailsService(this);
        return service.getTrustStore();
    }

    @Override
    public SSLContext initializeSslContext(UUID keystoreUUID) throws Exception {
        InitializeSslContextService service = new InitializeSslContextService(this, keystoreUUID);
        return service.execute();
    }

    @Override
    public Set<X509Certificate> getAllServerCertificates() throws Exception {
        return getKeyStores().stream().map(keyStore -> {
            try {
                Certificate certificate = keyStore.getCertificate(OCPP_SERVER_CERT);
                if (certificate == null) {
                    throw new CertificateParsingException("Certificate with alias ocpp_server_cert was not found, " +
                            "check alias");
                }
                if (certificate instanceof X509Certificate) {
                    return (X509Certificate) certificate;
                } else {
                    throw new CertificateParsingException("Unsupported certificate type, must be X509Certificate");
                }
            } catch (KeyStoreException | CertificateParsingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public synchronized X509Certificate getServerCertificate(UUID keystoreUUID) throws Exception {
        Certificate certificate = getKeyStores(keystoreUUID).getCertificate(OCPP_SERVER_CERT);
        if (certificate instanceof X509Certificate) {
            return (X509Certificate) certificate;
        } else {
            throw new CertificateParsingException("Unsupported certificate type, must be X509Certificate");
        }
    }

    @Override
    public String getServerCertificatePem(UUID keystoreUUID) throws Exception {
        X509Certificate certificate = getServerCertificate(keystoreUUID);
        try (StringWriter writer = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(certificate);
            pemWriter.flush();
            return writer.toString();
        }

    }

    @Override
    public synchronized UUID getKeyStoreUUIDByCertificate(Certificate certificate) throws Exception {
        for (KeystoreCertificateConfig keyStoreConfig : getKeystoreConfigRegistry().getKeystoreCertificatesConfig()) {
            KeyStore keyStore = getKeyStores(keyStoreConfig.getUuid());
            if (keyStore.getCertificateAlias(certificate) != null) {
                return keyStoreConfig.getUuid();
            }
        }
        throw new KeyStoreException("Certificate not found");
    }

    @Override
    public void setKeystoreListener(Consumer<Void> listener) {
        this.certChangeListener = listener;
    }

    @Override
    public String signCertificate(String csrString) {
        try {
            Security.setProperty("crypto.policy", "unlimited");
            Security.addProvider(new BouncyCastleProvider());
            String pkAlgorithm = "ECDSA";
            AlgorithmParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
            String signAlgorithm = "SHA256withECDSA";
            Instant validFrom = Instant.now();
            Instant validTo = Instant.now().plus(config.getInt(ConfigKey.CERTIFICATE_EXPIRATION_IN_MINUTES), ChronoUnit.MINUTES);
            PKCS10CertificationRequest csr = parseStringToPKCS10(csrString);

            //Get CA key pair
            List<KeystoreCertificateConfig> keystoreCertificateConfigs = getKeystoreConfigRegistry().getKeystoreCertificatesConfig().stream()
                    .filter(keystoreCertificateConfig -> !keystoreCertificateConfig.getKeystorePath().toString().endsWith("trust-store.jks"))
                    .collect(Collectors.toList());
            if (keystoreCertificateConfigs.isEmpty()) {
                LOGGER.error("Can't sign certificate, please generate server certificate to use it as CA");
                return "";
            }
            KeystoreCertificateConfig keyStoreConfig = keystoreCertificateConfigs.get(0);
            KeyStore keyStore = getKeyStores(keyStoreConfig.getUuid());
            PrivateKey caPrivateKey = (PrivateKey) keyStore.getKey(OCPP_SERVER_PRIVATE_KEY, keyStoreConfig.getKeystorePassword().toCharArray());
            Certificate caCertificate = keyStore.getCertificate(OCPP_SERVER_CERT);

            //Add cert information
            BigInteger certSerial = BigInteger.valueOf(new Random().nextInt());
            String domainName = InetAddress.getLocalHost().getHostName();
            X500Name issuerName = new X500Name("CN=" + domainName);
            X509v3CertificateBuilder certificateBuilder =
                    new X509v3CertificateBuilder(issuerName, certSerial, Date.from(validFrom), Date.from(validTo), csr.getSubject(), csr.getSubjectPublicKeyInfo());

            //Add cert signature
            ContentSigner signer = new JcaContentSignerBuilder(signAlgorithm).build(caPrivateKey);
            X509CertificateHolder certificateHolder = certificateBuilder.build(signer);

            //Create certificate in BC format
            org.bouncycastle.asn1.x509.Certificate bcCertificate = certificateHolder.toASN1Structure();

            //Convert BC format to regular Java certificate
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(bcCertificate.getEncoded()));
            return pemEncode(caCertificate, certificate).orElse("");
        } catch (Exception e) {
            LOGGER.error("Could not create certificate", e);
            return "";
        }
    }

    private Optional<String> pemEncode(Certificate caCertificate, Certificate signedCertificate) {
        try (StringWriter writer = new StringWriter();
             JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(signedCertificate);
            if (config.getBoolean(ConfigKey.CERTIFICATE_CHAIN_ADD_ROOT_CA_TO)) {
                pemWriter.writeObject(caCertificate);
            }
            pemWriter.flush();
            return Optional.of(writer.toString());
        } catch (IOException e) {
            LOGGER.error("Can't pem encrypt", e);
            return Optional.empty();
        }
    }

    private PKCS10CertificationRequest parseStringToPKCS10(String pemEncodedCsr) throws IOException {
        Reader csrReader = new StringReader(pemEncodedCsr);
        try (PEMParser pemParser = new PEMParser(csrReader)) {
            Object pemObj = pemParser.readObject();
            return (PKCS10CertificationRequest) pemObj;
        }
    }
}
