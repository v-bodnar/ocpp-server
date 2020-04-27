package com.omb.ocpp.security.certificate.service;

import com.omb.ocpp.security.certificate.KeystoreConstants;
import com.omb.ocpp.security.certificate.api.KeystoreApi;
import com.omb.ocpp.security.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.security.certificate.config.KeystoreConfigRegistry;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static com.omb.ocpp.security.certificate.KeystoreConstants.OCPP_SERVER_CERT;
import static com.omb.ocpp.security.certificate.KeystoreConstants.OCPP_SERVER_PRIVATE_KEY;

public class CreateKeystoreCertificateService {

    private final KeystoreApi keystoreApi;

    public CreateKeystoreCertificateService(KeystoreApi keystoreApi) {
        this.keystoreApi = Objects.requireNonNull(keystoreApi);
    }

    public KeystoreCertificateConfig execute() throws Exception {
        KeystoreCertificateConfig keystoreCertificateConfig = randomDataForConfig();
        createJavaKeyStoreWithCertificate(keystoreCertificateConfig);
        persistConfigurationToFile(keystoreCertificateConfig);
        return keystoreCertificateConfig;
    }

    private void persistConfigurationToFile(KeystoreCertificateConfig keystoreCertificateConfig) throws Exception {
        KeystoreConfigRegistry keystoreConfigRegistry = keystoreApi.getKeystoreConfigRegistry();
        keystoreConfigRegistry.addKeystoreCertificateConfig(keystoreCertificateConfig);
        keystoreConfigRegistry.persist();
    }

    private KeystoreCertificateConfig randomDataForConfig() {
        UUID randomName = UUID.randomUUID();
        String keystorePassword = UUID.randomUUID().toString();
        Path keystorePath = Paths.get(KeystoreConstants.KEYSTORE_ROOT_FOLDER_PATH.toString(), randomName + ".jks");

        return new KeystoreCertificateConfig.Builder().
                setUuid(randomName).
                setKeystorePassword(keystorePassword).
                setKeystorePath(keystorePath.toString()).
                setKeystoreProtocol("TLSv1.2").
                build();
    }

    private void createJavaKeyStoreWithCertificate(KeystoreCertificateConfig keystoreCertificateConfig) throws CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException, OperatorCreationException, InvalidAlgorithmParameterException {
        KeyStore keyStoreLocal = createJavaKeyStore(keystoreCertificateConfig);
        generateCertificate(keyStoreLocal, keystoreCertificateConfig.getKeystorePassword().toCharArray(), keystoreCertificateConfig.getKeystorePath());
    }

    private KeyStore createJavaKeyStore(KeystoreCertificateConfig keystoreCertificateConfig) throws IOException,
            KeyStoreException, CertificateException, NoSuchAlgorithmException {
        Files.createFile(keystoreCertificateConfig.getKeystorePath());
        KeyStore keyStoreLocal = KeyStore.getInstance("JKS");
        keyStoreLocal.load(null, null);
        try (OutputStream os = Files.newOutputStream(keystoreCertificateConfig.getKeystorePath())) {
            keyStoreLocal.store(os, keystoreCertificateConfig.getKeystorePassword().toCharArray());
        }
        return keyStoreLocal;
    }

    private void generateCertificate(KeyStore keyStore, char[] password, Path keyStorePath) throws IOException,
            NoSuchAlgorithmException, OperatorCreationException, CertificateException, NoSuchProviderException, KeyStoreException, InvalidAlgorithmParameterException {

        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());

        String domainName = InetAddress.getLocalHost().getHostName();
        X500Name issuerName = new X500Name("CN=" + domainName);
        X500Name subjectName = new X500Name("CN=" + domainName);
        BigInteger certSerial = BigInteger.valueOf(new Random().nextInt());
        String pkAlgorithm = "ECDSA";
        AlgorithmParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
        String signAlgorithm = "SHA256withECDSA";
        Date validFrom = new Date();
        Date validTo = Date.from(ZonedDateTime.now().plusYears(10).toInstant());

        //Create CA key pair
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(pkAlgorithm, "BC");
        keyPairGen.initialize(spec);
        KeyPair caKeyPair = keyPairGen.generateKeyPair();

        //Create CSR
        ContentSigner signGen = new JcaContentSignerBuilder(signAlgorithm).build(caKeyPair.getPrivate());
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subjectName, caKeyPair.getPublic());
        PKCS10CertificationRequest csr = builder.build(signGen);

        //Add cert information
        X509v3CertificateBuilder certificateBuilder =
                new X509v3CertificateBuilder(issuerName, certSerial, validFrom, validTo, subjectName, csr.getSubjectPublicKeyInfo());

        //Add cert signature
        ContentSigner signer = new JcaContentSignerBuilder(signAlgorithm).build(caKeyPair.getPrivate());
        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);

        //Create certificate in BC format
        org.bouncycastle.asn1.x509.Certificate bcCertificate = certificateHolder.toASN1Structure();

        //Convert BC format to regular Java certificate
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
        Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(bcCertificate.getEncoded()));

        //Save keys and certificate in the keyStore
        keyStore.setKeyEntry(OCPP_SERVER_PRIVATE_KEY, caKeyPair.getPrivate(), password, Collections.singletonList(certificate).toArray(new X509Certificate[0]));
        keyStore.setCertificateEntry(OCPP_SERVER_CERT, certificate);

        try (OutputStream out = Files.newOutputStream(keyStorePath)) {
            keyStore.store(out, password);
        }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("ECDSA", "BC");
        AlgorithmParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
        keyPairGen.initialize(spec);
        return keyPairGen.generateKeyPair();
    }
}
