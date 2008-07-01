/*
 * $Id: Codec.java,v 1.5 2008/07/01 11:30:54 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki.bouncycastle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

/**
 * Codec utility to read and write PEM object using the BouncyCastle functions.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.5 $
 */
public class Codec {

    /** Logging */
    static private Log LOG = LogFactory.getLog(Codec.class);

    /** Static initialisation */
    static {
        // add only once
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            LOG.info("add BouncyCastle security provider");
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * PEM encode a Key. OpenSSL compatible.
     * 
     * @param key
     *            The Key to PEM encode
     * @return The PEM encode String representation of the key
     */
    static public String getPEMEncoded(Key key) {
        StringWriter sw = new StringWriter();
        PEMWriter pem = new PEMWriter(sw);
        try {
            pem.writeObject(key);
        } catch (IOException e) {
            LOG.warn("Failed to write PEM key", e);
            return null;
        }
        finally {
            try {
                pem.close();
            } catch (IOException e) {
                // ignored
                LOG.warn(e);
            }
        }
        return sw.toString();
    }

    /**
     * PEM encode the encrypted Key. OpenSSL compatible.
     * 
     * @param key
     *            The Key to encoded
     * @param password
     *            The Key encryption password
     * @return The PEM encode String representation of the key
     */
    static public String getPEMEncoded(Key key, char[] password) {
        StringWriter sw = new StringWriter();
        PEMWriter pem = new PEMWriter(sw);
        try {
            String algorithm = "DESEDE";
            SecureRandom random = new SecureRandom();
            pem.writeObject(key, algorithm, password, random);
        } catch (IOException e) {
            LOG.warn("Failed to write encoded PEM key", e);
            return null;
        }
        finally {
            try {
                pem.close();
            } catch (IOException e) {
                // ignored
                LOG.warn(e);
            }
        }
        return sw.toString();
    }

    /**
     * Store the Key PEM encoded in a File. OpenSSL compatible.
     * 
     * @param key
     *            The Key to store PEM encoded.
     * @param file
     *            The File to store into.
     * @throws IOException
     *             If an error occurs.
     */
    static public void storePEMEncoded(Key key, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        PEMWriter pem = new PEMWriter(fw);
        pem.writeObject(key);
        try {
            pem.close();
            fw.close();
        } catch (IOException e) {
            // ignored
            LOG.warn(e);
        }
    }

    /**
     * Store the encrypted Key PEM encoded in a File. OpenSSL compatible.
     * 
     * @param key
     *            The Key to store PEM encoded.
     * @param password
     *            The Key encryption password.
     * @param file
     *            The File to store into.
     * @throws IOException
     *             If an error occurs.
     */
    static public void storePEMEncoded(Key key, char[] password, File file)
            throws IOException {
        FileWriter fw = new FileWriter(file);
        PEMWriter pem = new PEMWriter(fw);
        String algorithm = "DESEDE";
        SecureRandom random = new SecureRandom();
        pem.writeObject(key, algorithm, password, random);
        try {
            pem.close();
            fw.close();
        } catch (IOException e) {
            // ignored
            LOG.warn(e);
        }
    }

    /**
     * Stores a X509 certificate in PEM format. OpenSSL compatible.
     * 
     * @param cert
     *            The X509 certificate to store PEM encoded.
     * @param file
     *            The File to store into.
     * @throws IOException
     *             If an error occurs while storing.
     */
    static public void storePEMEncoded(X509Certificate cert, File file)
            throws IOException {
        FileWriter fw = new FileWriter(file);
        PEMWriter pem = new PEMWriter(fw);
        pem.writeObject(cert);
        try {
            pem.close();
            fw.close();
        } catch (IOException e) {
            // ignored
            LOG.warn(e);
        }
    }

    /**
     * Stores a X509 certificate and its chain of certificate PEM encoded.
     * OpenSSL compatible.
     * 
     * @param cert
     *            The X509 certificate to store PEM encoded.
     * @param chain
     *            The X509 certificates chain array
     * @param file
     *            The File to store into.
     * @throws IOException
     *             If an IO error occurs while saving.
     */
    static public void storePEMEncoded(X509Certificate cert,
            X509Certificate[] chain, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        PEMWriter pem = new PEMWriter(fw);
        pem.writeObject(cert);
        if (chain != null) {
            for (int i = 0; i < chain.length; i++) {
                pem.writeObject(chain[i]);
            }
        }
        try {
            pem.close();
            fw.close();
        } catch (IOException e) {
            // ignored
            LOG.warn(e);
        }
    }

    /**
     * Returns the PEM encoded String of the X509 certificate. OpenSSL
     * compatible.
     * 
     * @param cert
     *            The X509 certificate.
     * @return The PEM encoded String.
     */
    static public String getPEMEncoded(X509Certificate cert) {
        StringWriter sw = new StringWriter();
        PEMWriter pem = new PEMWriter(sw);
        try {
            pem.writeObject(cert);
        } catch (IOException e) {
            LOG.warn("Failed to write PKCS7 in PEM format", e);
            return null;
        }
        finally {
            try {
                pem.close();
                sw.close();
            } catch (IOException e) {
                // ignored
                LOG.warn(e);
            }
        }
        return sw.toString();
    }

    /**
     * Return an array of all X509Certificates stored in a PEM encoded source.
     * The certificate order of the source is respected.
     * 
     * @param reader
     *            The Reader used to read the source.
     * @return The array of all X509 certificates found in the PEM source.
     * @throws IOException
     *             If an error occurs while reading the source.
     */
    static public X509Certificate[] readPEMEncodedCertificates(Reader reader)
            throws IOException {
        Vector<X509Certificate> certificates = new Vector<X509Certificate>();
        LOG.debug("read all certificates");
        PEMReader pr = new PEMReader(reader);
        boolean haveNext = true;
        while (haveNext) {
            X509Certificate certificate = (X509Certificate) pr.readObject();
            if (certificate == null) {
                haveNext = false; // stop loop
            }
            else {
                certificates.add(certificate);
            }
        }
        int length = certificates.size();
        LOG.debug(length + " certificates found");
        X509Certificate certificatesArray[] = (X509Certificate[]) certificates.toArray(new X509Certificate[length]);
        return certificatesArray;
    }

    /**
     * Stores the private key and certificate in a PKCS12 file. The certificate
     * Subject CN is used as key alias in the PKCS12 store.
     * 
     * @param privateKey
     *            The private key.
     * @param certificate
     *            The X509 certificate.
     * @param chain
     *            The X509 certificate chain.
     * @param file
     *            The file object.
     * @param password
     *            The password for the PKCS12 file.
     * @throws GeneralSecurityException
     *             If a crypto error occurs.
     * @throws IOException
     *             If an IO error occurs.
     */
    static public void storePKCS12(PrivateKey privateKey,
            X509Certificate certificate, X509Certificate chain[], File file,
            char[] password) throws GeneralSecurityException, IOException {
        // set the bag information for the PKCS12 keystore
        PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) privateKey;
        PublicKey publicKey = certificate.getPublicKey();
        bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, new SubjectKeyIdentifierStructure(publicKey));

        // the PKCS12 keystore key alias is the CN
        String alias = getPrincipalValue(certificate, X509Principal.CN);

        // build full cert chain
        int nCerts = chain.length + 1;
        Certificate certs[] = new Certificate[nCerts];
        certs[0] = certificate;
        for (int i = 0; i < chain.length; i++) {
            certs[i + 1] = chain[i];
        }
        // create a PKCS12 keystore
        KeyStore p12Store = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME);
        p12Store.load(null, null);
        // set the key entry
        p12Store.setKeyEntry(alias, privateKey, null, certs);
        // store the file
        FileOutputStream fos = new FileOutputStream(file);
        p12Store.store(fos, password);
        fos.close();
    }

    /**
     * Gets the first value of the {@link X509Principal} corresponding to the
     * given oid.
     * 
     * @param certificate
     *            The X509 certificate, containing the X509Principal.
     * @param oid
     *            The OID of the desired value.
     * @return The value or <code>null</code> if the principal doesn't contain
     *         the oid.
     * @throws GeneralSecurityException
     *             If a crypto error occurs.
     */
    static public String getPrincipalValue(X509Certificate certificate,
            DERObjectIdentifier oid) throws GeneralSecurityException {
        X509Principal subject = PrincipalUtil.getSubjectX509Principal(certificate);
        Vector oids = subject.getOIDs();
        int valueIndex = oids.indexOf(oid);
        if (valueIndex < 0) {
            // oid not found
            return null;
        }
        Vector values = subject.getValues();
        String value = values.get(valueIndex).toString();
        return value;
    }

    /**
     * Prevents instantiation of the utility class.
     */
    private Codec() {
    }
}
