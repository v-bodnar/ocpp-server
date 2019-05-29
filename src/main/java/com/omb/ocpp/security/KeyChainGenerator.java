package com.omb.ocpp.security;

import com.omb.ocpp.server.SslKeyStoreConfig;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
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
    private static final String KEY_STORE_NOT_FOUND = "KeyStore not found";
    private static final String OCPP_SERVER_PRIVATE_KEY = "OCPP_SERVER_PRIVATE_KEY";
    private static final String OCPP_SERVER_PUBLIC_KEY = "OCPP_SERVER_PUBLIC_KEY";
    private static final String OCPP_SERVER_CERT = "OCPP_SERVER_CERT";
    private static final String OCPP_CLIENT_CERT = "OCPP_CLIENT_CERT";

    private KeyChainGenerator() {
    }

    public static Optional<String> getServerCertificatePem(SslKeyStoreConfig keyStoreConfig) {
        Optional<java.security.cert.Certificate> certificate = getServerCertificate(keyStoreConfig);
        if (certificate.isPresent()) {
            try {
                final StringWriter writer = new StringWriter();
                final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
                pemWriter.writeObject(certificate.get());
                pemWriter.flush();
                pemWriter.close();
                return Optional.ofNullable(writer.toString());
            } catch (IOException e) {
                LOGGER.debug("Could not get or generate certificate {}", e.getMessage());
                return Optional.empty();
            }

        } else {
            return Optional.empty();
        }
    }

    public static Optional<java.security.cert.Certificate> getServerCertificate(SslKeyStoreConfig keyStoreConfig) {
        try {
            KeyStore keyStore = keyStoreConfig.geKeyStore().orElseThrow(() -> new KeyStoreException(KEY_STORE_NOT_FOUND));
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

    public static Optional<java.security.cert.Certificate> getClientCertificate(SslKeyStoreConfig keyStoreConfig) {
        try {
            KeyStore keyStore = keyStoreConfig.geKeyStore().orElseThrow(() -> new KeyStoreException(KEY_STORE_NOT_FOUND));
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
            KeyStore keyStore = keyStoreConfig.geKeyStore().orElseThrow(() -> new KeyStoreException(KEY_STORE_NOT_FOUND));
            keyStore.setCertificateEntry(OCPP_CLIENT_CERT, cer);
            char[] password = keyStoreConfig.getKeystorePassword().toCharArray();
            try (OutputStream out = Files.newOutputStream(keyStoreConfig.getKeystorePath())) {
                keyStore.store(out, password);
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            LOGGER.debug("Could not save client certificate", e);
        }
    }

    private static Optional<java.security.cert.Certificate> generateCertificate(KeyStore keyStore, char[] password,
                                                                                Path keyStorePath) {
        try {
            Security.setProperty("crypto.policy", "unlimited");
            Security.addProvider(new BouncyCastleProvider());

            final String domainName = InetAddress.getLocalHost().getHostName();
            final X500Name issuerName = new X500Name("CN=" + domainName);
            final X500Name subjectName = new X500Name("CN=" + domainName);
            final BigInteger certSerial = BigInteger.valueOf(new Random().nextInt());
            final Date validFrom = new Date();
            final Date validTo = Date.from(ZonedDateTime.now().plusYears(10).toInstant());

            final KeyPair keyPair = getKeyPair(keyStore, password);

            final AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA");
            final AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

            final AsymmetricKeyParameter privateKey = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
            final SubjectPublicKeyInfo publicKey = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

            final PKCS10CertificationRequestBuilder p10Builder = new PKCS10CertificationRequestBuilder(subjectName,
                    publicKey);
            final ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKey);
            final PKCS10CertificationRequest csr = p10Builder.build(signer);

            final PKCS10CertificationRequest pk10Holder = new PKCS10CertificationRequest(csr.getEncoded());

            final X509v3CertificateBuilder certificateBuilder =
                    new X509v3CertificateBuilder(issuerName, certSerial, validFrom, validTo, pk10Holder.getSubject(), publicKey);

            final DERSequence subjectAlternativeNames = new DERSequence(new ASN1Encodable[]{
                    new GeneralName(GeneralName.dNSName, "localhost"),
                    new GeneralName(GeneralName.dNSName, "127.0.0.1")
            });

            certificateBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAlternativeNames);

            X509CertificateHolder certificateHolder = certificateBuilder.build(signer);

            Certificate bcCertificate = certificateHolder.toASN1Structure();

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
        } catch (CertificateException | NoSuchAlgorithmException | OperatorCreationException | KeyStoreException | IOException | UnrecoverableKeyException | NoSuchProviderException e) {
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
            rsa.initialize(2048, new SecureRandom());
            return rsa.genKeyPair();
        }
    }
}
