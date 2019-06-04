package com.omb.ocpp.certificate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omb.ocpp.certificate.KeystoreConstants;
import com.omb.ocpp.certificate.api.KeystoreApi;
import com.omb.ocpp.certificate.config.KeystoreCertificateConfig;
import com.omb.ocpp.certificate.config.KeystoreCertificatesConfig;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.*;

public class CreateKeystoreCertificateService {

    private static final String OCPP_SERVER_PRIVATE_KEY = "OCPP_SERVER_PRIVATE_KEY";
    private static final String OCPP_SERVER_CERT = "OCPP_SERVER_CERT";

    private KeystoreApi keystoreApi;

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
        KeystoreCertificatesConfig keystoreCertificatesConfig = keystoreApi.getKeystoreCertificatesConfig();
        keystoreCertificatesConfig.addKeystoreCertificateConfig(keystoreCertificateConfig);
        writeConfigToFile(keystoreCertificatesConfig);
    }

    private KeystoreCertificateConfig randomDataForConfig() {
        UUID randomName = UUID.randomUUID();
        String keystorePassword = UUID.randomUUID().toString();
        Path keystorePath = Paths.get(KeystoreConstants.KEYSTORE_ROOT_FOLDER_PATH.toString(), randomName + ".jks");

        return new KeystoreCertificateConfig.Builder().
                setUuid(randomName).
                setKeystorePassword(keystorePassword).
                setKeystorePath(keystorePath.toString()).
                build();
    }

    private void writeConfigToFile(KeystoreCertificatesConfig keystoreCertificatesConfig) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String configAsJson = gson.toJson(keystoreCertificatesConfig);
        Files.writeString(KeystoreConstants.KEYSTORE_CERTIFICATE_CONFIG_PATH, configAsJson);
    }

    private void createJavaKeyStoreWithCertificate(KeystoreCertificateConfig keystoreCertificateConfig) throws Exception {
        KeyStore keyStoreLocal = createJavaKeyStore(keystoreCertificateConfig);
        generateCertificate(keyStoreLocal, keystoreCertificateConfig.getKeystorePassword().toCharArray(), keystoreCertificateConfig.getKeystorePath());
    }

    private KeyStore createJavaKeyStore(KeystoreCertificateConfig keystoreCertificateConfig) throws Exception {
        Files.createFile(keystoreCertificateConfig.getKeystorePath());
        KeyStore keyStoreLocal = KeyStore.getInstance("JKS");
        keyStoreLocal.load(null, null);
        try (OutputStream os = Files.newOutputStream(keystoreCertificateConfig.getKeystorePath())) {
            keyStoreLocal.store(os, keystoreCertificateConfig.getKeystorePassword().toCharArray());
        }
        return keyStoreLocal;
    }

    private Optional<Certificate> generateCertificate(KeyStore keyStore, char[] password, Path keyStorePath) throws Exception {

        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());

        String domainName = InetAddress.getLocalHost().getHostName();
        X500Name issuerName = new X500Name("CN=" + domainName);
        X500Name subjectName = new X500Name("CN=" + domainName);
        BigInteger certSerial = BigInteger.valueOf(new Random().nextInt());
        Date validFrom = new Date();
        Date validTo = Date.from(ZonedDateTime.now().plusYears(10).toInstant());

        KeyPair keyPair = generateKeyPair();

        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

        AsymmetricKeyParameter privateKey = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
        SubjectPublicKeyInfo publicKey = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

        PKCS10CertificationRequestBuilder p10Builder = new PKCS10CertificationRequestBuilder(subjectName,
                publicKey);
        ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKey);
        PKCS10CertificationRequest csr = p10Builder.build(signer);

        PKCS10CertificationRequest pk10Holder = new PKCS10CertificationRequest(csr.getEncoded());

        X509v3CertificateBuilder certificateBuilder =
                new X509v3CertificateBuilder(issuerName, certSerial, validFrom, validTo, pk10Holder.getSubject(), publicKey);

        DERSequence subjectAlternativeNames = new DERSequence(new ASN1Encodable[]{
                new GeneralName(GeneralName.dNSName, "localhost"),
                new GeneralName(GeneralName.dNSName, "127.0.0.1")
        });

        certificateBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAlternativeNames);

        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);

        org.bouncycastle.asn1.x509.Certificate bcCertificate = certificateHolder.toASN1Structure();

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");

        X509Certificate certificate =
                (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(bcCertificate.getEncoded()));

        //Save keys and certificate in the keyStore
        keyStore.setKeyEntry(OCPP_SERVER_PRIVATE_KEY, keyPair.getPrivate(), password,
                Collections.singletonList(certificate).toArray(new X509Certificate[1]));
        keyStore.setCertificateEntry(OCPP_SERVER_CERT, certificate);

        try (OutputStream out = Files.newOutputStream(keyStorePath)) {
            keyStore.store(out, password);
        }

        return Optional.ofNullable(certificate);
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
        rsa.initialize(2048, new SecureRandom());
        return rsa.genKeyPair();
    }
}
