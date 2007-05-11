/*
 * $Id: Certificate.java,v 1.3 2007/05/11 11:34:17 vtschopp Exp $
 * 
 * Created on Jun 14, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.pki.bouncycastle.Codec;
import org.glite.slcs.util.Utils;

/**
 * Certificate is a wrapper class for the X509Certificate. Used to store a
 * certificate with its chain and read/write it from/to file.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public class Certificate {

    /** Logging */
    static private Log LOG= LogFactory.getLog(Certificate.class);

    /** Default unix file permissions for the certificate file */
    public static final int CRT_FILE_PERMISSION= 640;

    /** The X.509 certificate */
    private X509Certificate cert_;

    /** the certificate chain if any */
    private X509Certificate[] chain_;

    /**
     * Read a PEM source to extract the certificate and its chain. The
     * certificate must be the first in the source, all others are considered as
     * chain members.
     * 
     * @param reader
     *            The Reader to read the source.
     * @return The Certificate (with its chain)
     * @throws IOException
     *             If an error occurs while reading the source.
     * @throws GeneralSecurityException
     *             If an error occurs while creating the Certificate.
     */
    static public Certificate readPEM(Reader reader) throws IOException,
            GeneralSecurityException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("read cert and chain...");
        }
        X509Certificate certificates[]= Codec.readPEMEncodedCertificates(reader);

        // check array size
        int length= certificates.length;
        if (length < 1) {
            LOG.error("No X509 certificate found in source");
            throw new GeneralSecurityException("No valid X509 certificates found");
        }

        // The first is the main cert
        X509Certificate cert= certificates[0];
        // all others go in the chain
        X509Certificate chain[]= null;
        int chainLength= length - 1;
        if (chainLength > 0) {
            chain= new X509Certificate[chainLength];
            System.arraycopy(certificates, 1, chain, 0, chainLength);
        }
        return new Certificate(cert, chain);
    }

    /**
     * Creates a certificate with its chain.
     * 
     * @param cert
     *            The X509Certificate
     * @param chain
     *            The chain as an array of X509Ceritificate.
     * @throws GeneralSecurityException
     *             If the certificate is null.
     */
    public Certificate(X509Certificate cert, X509Certificate[] chain)
            throws GeneralSecurityException {
        if (cert == null) {
            throw new GeneralSecurityException("X509Certificate is null");
        }
        this.cert_= cert;
        this.chain_= chain;
    }

    /**
     * Creates a certificate without chain.
     * 
     * @param cert
     *            The X509Certificate.
     * @throws GeneralSecurityException
     *             If the X509Certificate is null.
     */
    public Certificate(X509Certificate cert) throws GeneralSecurityException {
        this(cert, null);
    }

    /**
     * Stores the certificate and its chain in PEM format in the given file
     * name. OpenSSL compatible.
     * 
     * @param filename
     *            The file name to store into.
     * @throws IOException
     *             If an error occurs while writing the cert.
     */
    public void storePEM(String filename) throws IOException {
        File file= new File(filename);
        storePEM(file);
    }

    /**
     * Stores the ceritifcate and its chain in PEM format in the given file. The
     * file permission is set as CRT_FILE_PERMISSION (octal: 640). OpenSSL
     * compatible.
     * 
     * @param file
     *            The file to write into.
     * @throws IOException
     */
    public void storePEM(File file) throws IOException {
        boolean permOk= Utils.setFilePermissions(file, CRT_FILE_PERMISSION);
        if (!permOk) {
            LOG.warn("Failed to set permission: " + CRT_FILE_PERMISSION
                    + " for file: " + file);
        }
        Codec.storePEMEncoded(this.cert_, this.chain_, file);

    }

    /**
     * Returns the PEM encoded certificate
     * 
     * @return The PEM encoded string
     */
    public String getPEM() {
        StringBuffer sb= new StringBuffer();
        sb.append(Codec.getPEMEncoded(this.cert_));
        if (chain_ != null) {
            for (int i= 0; i < chain_.length; i++) {
                X509Certificate chainCert= chain_[i];
                sb.append(Codec.getPEMEncoded(chainCert));
            }
        }
        return sb.toString();
    }

    /**
     * @return the X509 certificate
     */
    public X509Certificate[] getCertificateChain() {
        return chain_;
    }
    
    /**
     * @return the X509 certificate chain
     */
    public X509Certificate getCertificate() {
        return cert_;
    }
}
