package com.omb.ocpp.security;

import com.omb.ocpp.server.SslKeyStoreConfig;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

public class KeyChainGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyChainGenerator.class);
    private static final String OCPP_SERVER_PRIVATE_KEY = "OCPP_SERVER_PRIVATE_KEY";
    private static final String OCPP_SERVER_PUBLIC_KEY = "OCPP_SERVER_PUBLIC_KEY";
    private static final String OCPP_SERVER_CERT = "OCPP_SERVER_CERT";
    private static final String OCPP_CLIENT_CERT = "OCPP_CLIENT_CERT";

    private KeyChainGenerator() {
    }

    public static Optional<Certificate> getServerCertificate(SslKeyStoreConfig keyStoreConfig) {
        try {
            KeyStore keyStore = keyStoreConfig.geKeyStore().orElseThrow(() -> new KeyStoreException("KeyStore not found"));
            char[] password = keyStoreConfig.getKeystorePassword().toCharArray();
            Path keyStorePath = keyStoreConfig.getKeystorePath();

            if (keyStore.containsAlias(OCPP_SERVER_CERT)) {
                return Optional.ofNullable(keyStore.getCertificate(OCPP_SERVER_CERT));
            } else {
                return generateCertificate(keyStore, password, keyStorePath);
            }
        } catch (KeyStoreException e) {
            LOGGER.debug("Could not get or generate certificate {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Certificate> getClientCertificate(SslKeyStoreConfig keyStoreConfig) {
        try {
            KeyStore keyStore = keyStoreConfig.geKeyStore().orElseThrow(() -> new KeyStoreException("KeyStore not found"));
            return Optional.ofNullable(keyStore.getCertificate(OCPP_CLIENT_CERT));
        } catch (KeyStoreException e) {
            LOGGER.debug("Could not get or generate certificate {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static void saveClientCertificateInKeyStore(SslKeyStoreConfig keyStoreConfig, File file) {
        if (file != null && file.exists()) {
            try (InputStream is = new FileInputStream(file)) {
                saveClientCertificateInKeyStore(keyStoreConfig, is);
            } catch (IOException e) {
                LOGGER.error("Could not find certificate", e);
            }
        } else {
            LOGGER.error("Certificate not found");
        }
    }

    public static void saveClientCertificateInKeyStore(SslKeyStoreConfig keyStoreConfig, InputStream inputStream) {
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(inputStream);
            KeyStore keyStore = keyStoreConfig.geKeyStore().orElseThrow(() -> new KeyStoreException("KeyStore not found"));
            keyStore.setCertificateEntry(OCPP_CLIENT_CERT, cer);
            char[] password = keyStoreConfig.getKeystorePassword().toCharArray();
            try (OutputStream out = Files.newOutputStream(keyStoreConfig.getKeystorePath())) {
                keyStore.store(out, password);
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            LOGGER.debug("Could not save client certificate", e);
        }
    }

    public static Optional<Certificate> generateCertificate(KeyStore keyStore, char[] password, Path keyStorePath) {
        try {
            Security.setProperty("crypto.policy", "unlimited");
            Security.addProvider(new BouncyCastleProvider());

            final KeyPair keyPair = getKeyPair(keyStore, password);
            final String dn = InetAddress.getLocalHost().getHostName();

            X500Name issuerName = new X500Name("CN=" + dn);
            X500Name subjectName = new X500Name("CN=" + dn);
            BigInteger serial = BigInteger.valueOf(new Random().nextInt());

            X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuerName, serial, new Date(),
                    Date.from(ZonedDateTime.now().plusYears(10).toInstant()), subjectName,
                    keyPair.getPublic());


            DERSequence subjectAlternativeNames = new DERSequence(new ASN1Encodable[]{
                    new GeneralName(GeneralName.dNSName, "localhost"),
                    new GeneralName(GeneralName.dNSName, "127.0.0.1")
            });

            certificateBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAlternativeNames);
            Certificate certificate = signCertificate(certificateBuilder, keyPair.getPrivate());

            //Save keys and certificate in the keyStore
            keyStore.setKeyEntry(OCPP_SERVER_PRIVATE_KEY, keyPair.getPrivate(), password,
                    Collections.singletonList(certificate).toArray(new Certificate[1]));
            keyStore.setCertificateEntry(OCPP_SERVER_CERT, certificate);

            try (OutputStream out = Files.newOutputStream(keyStorePath)) {
                keyStore.store(out, password);
            }

            return Optional.ofNullable(certificate);
        } catch (CertificateException | NoSuchAlgorithmException | OperatorCreationException | KeyStoreException | IOException | UnrecoverableKeyException e) {
            LOGGER.error("Could not create keys pair", e);
            return Optional.empty();
        }
    }

    private static KeyPair getKeyPair(KeyStore keyStore, char[] password) throws KeyStoreException,
            UnrecoverableKeyException, NoSuchAlgorithmException {
        if (keyStore.containsAlias(OCPP_SERVER_PRIVATE_KEY) && keyStore.containsAlias(OCPP_SERVER_PUBLIC_KEY)) {
            return new KeyPair((PublicKey) keyStore.getKey(OCPP_SERVER_PUBLIC_KEY, password),
                    (PrivateKey) keyStore.getKey(OCPP_SERVER_PRIVATE_KEY, password));
        } else {
            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(2048, null);
            return rsa.genKeyPair();
        }
    }

    private static X509Certificate signCertificate(X509v3CertificateBuilder certificateBuilder,
                                                   PrivateKey caPrivateKey) throws OperatorCreationException, CertificateException {
        ContentSigner signer = new JcaContentSignerBuilder("SHA1WithRSA")
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(caPrivateKey);
        return new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certificateBuilder.build(signer));
    }


}
