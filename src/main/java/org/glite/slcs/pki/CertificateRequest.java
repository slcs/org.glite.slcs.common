/*
 * $Id: CertificateRequest.java,v 1.2 2006/10/24 08:54:01 vtschopp Exp $
 * 
 * Created on May 3, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.glite.slcs.pki.bouncycastle.PKCS10;
import org.glite.slcs.util.Utils;

/**
 * CertificateRequest is a wrapper class for a PKCS10 object and the methods to
 * read and store as PEM format.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.2 $
 */
public class CertificateRequest {

    /** Logging */
    private static Log LOG= LogFactory.getLog(CertificateRequest.class);

    /** Default unix file permission for the certificate request file */
    private static final int CSR_FILE_PERMISSION= 640;

    /**
     * BouncyCastle PKCS10 wrapper
     */
    private PKCS10 pkcs10_= null;

    /**
     * Creates a certificate request for the given keys and principal (DN).
     * 
     * @param keys
     *            The CertificateKeys
     * @param principal
     *            The certificate request principal (DN).
     * @throws GeneralSecurityException
     *             If an error occurs while creating the object.
     */
    public CertificateRequest(CertificateKeys keys, Principal principal)
            throws GeneralSecurityException {
        this(keys, principal.getName());
    }

    /**
     * Creates a certificate request for the given keys and subject (DN).
     * 
     * @param keys
     *            The CertificateKeys
     * @param subject
     *            The certificate request subject (DN).
     * @throws GeneralSecurityException
     *             If an error occurs while creating the object.
     */
    public CertificateRequest(CertificateKeys keys, String subject)
            throws GeneralSecurityException {
        this(keys, subject, null);
    }

    /**
     * Creates a certificate request for the given keys, subject and extensions.
     * 
     * @param keys
     *            The CertificateKeys
     * @param subject
     *            The certificate request subject (DN).
     * @param extensions
     *            An List of certificate extensions.
     * @throws GeneralSecurityException
     *             If an error occurs while creating the object.
     * @see org.glite.slcs.pki.CertificateExtension
     */
    public CertificateRequest(CertificateKeys keys, String subject,
            List<CertificateExtension> certificateExtensions) throws GeneralSecurityException {

        X509Extensions x509extensions= null;
        if (certificateExtensions != null && !certificateExtensions.isEmpty()) {
            Hashtable<DERObjectIdentifier,X509Extension> extensionsMap= new Hashtable<DERObjectIdentifier, X509Extension>();
            for (CertificateExtension extension : certificateExtensions) {
                extensionsMap.put(extension.getOID(), extension.getExtension());
            }
            x509extensions= new X509Extensions(extensionsMap);
        }
        this.pkcs10_= new PKCS10(subject,
                                 keys.getPublic(),
                                 keys.getPrivate(),
                                 x509extensions);
    }

    /**
     * Creates a certificate request with the given PKCS10 object.
     * 
     * @param pkcs10
     *            The PKCS10 object.
     */
    public CertificateRequest(PKCS10 pkcs10) {
        this.pkcs10_= pkcs10;
    }

    /**
     * Returns a List of certificate extensions contained in the certificate
     * request.
     * 
     * @return The List of CertificateExtension
     */
    public List<CertificateExtension> getCertificateExtensions() {
        List<CertificateExtension> certificateExtensions= new ArrayList<CertificateExtension>();
        X509Extensions x509Extensions= pkcs10_.getX509Extensions();
        if (x509Extensions != null) {
            @SuppressWarnings("unchecked")
			Enumeration<DERObjectIdentifier> oids= x509Extensions.oids();
            while (oids.hasMoreElements()) {
                DERObjectIdentifier oid= (DERObjectIdentifier) oids.nextElement();
                @SuppressWarnings("deprecation")
				X509Extension x509Extension= x509Extensions.getExtension(oid);
                boolean critical= x509Extension.isCritical();
                CertificateExtension extension= new CertificateExtension(oid,
                                                                         x509Extension,
                                                                         critical);
                certificateExtensions.add(extension);
            }
        }
        return certificateExtensions;
    }

    /**
     * @return the certificate request principal (subject)
     */
    public Principal getPrincipal() {
        return this.pkcs10_.getPrincipal();
    }

    /**
     * @return The PKCS#10 PEM encoded string.
     */
    public String getPEMEncoded() {
        return pkcs10_.getPEMEncoded();
    }

    /**
     * @return PKCS#10 DER encoded byte array
     */
    public byte[] getDEREncoded() {
        return pkcs10_.getDEREncoded();
    }

    /**
     * 
     * @param filename
     * @throws IOException
     */
    public void storePEM(String filename) throws IOException {
        File file= new File(filename);
        storePEM(file);
    }

    /**
     * 
     * @param file
     * @throws IOException
     */
    public void storePEM(File file) throws IOException {
        boolean permOk= Utils.setFilePermissions(file, CSR_FILE_PERMISSION);
        if (!permOk) {
            LOG.warn("Failed to set permission: " + CSR_FILE_PERMISSION
                    + " for file: " + file);
        }
        pkcs10_.storePEMEncoded(file);

    }

    /**
     * 
     * @param file
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    static public CertificateRequest loadPEM(File file) throws IOException,
            GeneralSecurityException {
        FileReader reader= new FileReader(file);
        CertificateRequest csr= readPEM(reader);
        try {
            reader.close();
        } catch (IOException e) {
            LOG.warn(e);
        }
        return csr;
    }

    /**
     * 
     * @param reader
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    static public CertificateRequest readPEM(Reader reader) throws IOException,
            GeneralSecurityException {
        PKCS10 pkcs10= PKCS10.readPEMEncoded(reader);
        CertificateRequest csr= new CertificateRequest(pkcs10);
        return csr;
    }

}
