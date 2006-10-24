/*
 * $Id: Codec.java,v 1.3 2006/10/24 08:55:04 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki.bouncycastle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

/**
 * Codec utility to read and write PEM object using the BouncyCastle functions.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public class Codec {

    /** Logging */
    static private Log LOG= LogFactory.getLog(Codec.class);

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
        StringWriter sw= new StringWriter();
        PEMWriter pem= new PEMWriter(sw);
        try {
            pem.writeObject(key);
        } catch (IOException e) {
            LOG.warn("Failed to write PEM key", e);
            return null;
        } finally {
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
        StringWriter sw= new StringWriter();
        PEMWriter pem= new PEMWriter(sw);
        try {
            String algorithm= "DESEDE";
            SecureRandom random= new SecureRandom();
            pem.writeObject(key, algorithm, password, random);
        } catch (IOException e) {
            LOG.warn("Failed to write encoded PEM key", e);
            return null;
        } finally {
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
        FileWriter fw= new FileWriter(file);
        PEMWriter pem= new PEMWriter(fw);
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
        FileWriter fw= new FileWriter(file);
        PEMWriter pem= new PEMWriter(fw);
        String algorithm= "DESEDE";
        SecureRandom random= new SecureRandom();
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
        FileWriter fw= new FileWriter(file);
        PEMWriter pem= new PEMWriter(fw);
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
        FileWriter fw= new FileWriter(file);
        PEMWriter pem= new PEMWriter(fw);
        pem.writeObject(cert);
        if (chain != null) {
            for (int i= 0; i < chain.length; i++) {
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
        StringWriter sw= new StringWriter();
        PEMWriter pem= new PEMWriter(sw);
        try {
            pem.writeObject(cert);
        } catch (IOException e) {
            LOG.warn("Failed to write PKCS7 in PEM format", e);
            return null;
        } finally {
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
        Vector certificates= new Vector();
        LOG.debug("read all certificates");
        PEMReader pr= new PEMReader(reader);
        boolean haveNext= true;
        while (haveNext) {
            X509Certificate certificate= (X509Certificate) pr.readObject();
            if (certificate == null) {
                haveNext= false; // stop loop
            }
            else {
                certificates.add(certificate);
            }
        }
        int length= certificates.size();
        LOG.debug(length + " certificates found");
        X509Certificate certificatesArray[]= (X509Certificate[]) certificates.toArray(new X509Certificate[length]);
        return certificatesArray;
    }

    /**
     * Prevents instantiation of the utility class.
     */
    private Codec() {
    }
}
