/*
 * $Id: PKCS10.java,v 1.1 2006/09/21 12:42:57 vtschopp Exp $
 * 
 * Created on May 30, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.pki.bouncycastle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

/**
 * 
 * PKCS10 wrapper class for the BouncyCastle PKCS10CertificationRequest object.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class PKCS10 {

    /** Logging */
    static private Log LOG= LogFactory.getLog(PKCS10.class);

    static {
        // add only once
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            LOG.info("add BouncyCastle security provider");
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /** Signature algorithm for the PKCS#10 request */
    static public String SIGNATURE_ALGORITHM= "SHA1WithRSA";

    /** BouncyCastle PKCS#10 */
    private PKCS10CertificationRequest bcPKCS10_= null;

    /**
     * 
     * @param subject
     * @param publicKey
     * @param privateKey
     * @throws GeneralSecurityException
     */
    public PKCS10(String subject, PublicKey publicKey, PrivateKey privateKey)
            throws GeneralSecurityException {
        this(subject, publicKey, privateKey, null);
    }

    /**
     * 
     * @param subject
     * @param publicKey
     * @param privateKey
     * @param x509Extensions
     * @throws GeneralSecurityException
     */
    public PKCS10(String subject, PublicKey publicKey, PrivateKey privateKey,
            X509Extensions x509Extensions) throws GeneralSecurityException {
        // subject DN 
        X509Principal principal= new X509Principal(subject);
        // extensions
        ASN1Set attributes= new DERSet();
        if (x509Extensions != null) {
            // PKCS9 extensions
            DERSet extensions= new DERSet(x509Extensions);
            Attribute attribute= new Attribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
                                               extensions);
            attributes= new DERSet(attribute);
        }
        // create CSR
        bcPKCS10_= new PKCS10CertificationRequest(SIGNATURE_ALGORITHM,
                                                  principal,
                                                  publicKey,
                                                  attributes,
                                                  privateKey);
        // verify
        if (!bcPKCS10_.verify()) {
            LOG.error("Failed to verify the PKCS#10");
            throw new GeneralSecurityException("PKCS#10 verification failed");
        }

    }

    /**
     * 
     * @param pkcs10
     * @throws GeneralSecurityException
     */
    private PKCS10(PKCS10CertificationRequest pkcs10)
            throws GeneralSecurityException {
        this.bcPKCS10_= pkcs10;
        if (!bcPKCS10_.verify()) {
            LOG.error("Failed to verify the PKCS#10");
            throw new GeneralSecurityException("PKCS#10 verification failed");
        }

    }

    /**
     * @return The DER encoded byte array.
     */
    public byte[] getDEREncoded() {
        return this.bcPKCS10_.getEncoded();
    }

    /**
     * @return The PEM encoded string representation.
     */
    public String getPEMEncoded() {
        StringWriter sw= new StringWriter();
        PEMWriter pem= new PEMWriter(sw);
        try {
            pem.writeObject(this.bcPKCS10_);
        } catch (IOException e) {
            LOG.warn("Failed to write PKCS10 in PEM format", e);
            return null;
        } finally {
            try {
                pem.close();
                sw.close();
            } catch (IOException e) {
                // ignored
            }
        }
        return sw.toString();
    }

    /**
     * Stores the PCKS10 in PEM format. This is OpenSSL compatible.
     * 
     * @param file
     *            The file to store into.
     * @throws IOException
     */
    public void storePEMEncoded(File file) throws IOException {
        FileWriter fw= new FileWriter(file);
        PEMWriter pem= new PEMWriter(fw);
        pem.writeObject(this.bcPKCS10_);
        try {
            pem.close();
            fw.close();
        } catch (IOException e) {
            // ignored
            LOG.warn(e);
        }
    }

    /**
     * Stores the DER encoded PKCS#10 in a file.
     * 
     * @param file
     *            The file to store into.
     * @throws IOException
     */
    public void storeDEREncoded(File file) throws IOException {
        FileOutputStream fos= new FileOutputStream(file);
        byte derBytes[]= bcPKCS10_.getEncoded();
        fos.write(derBytes);
        try {
            fos.close();
        } catch (IOException e) {
            // ignored
            LOG.warn(e);
        }
    }

    /**
     * Reads the PKCS10 from a reader. This is OpenSSL compatible.
     * 
     * @param reader
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     * @see java.io.Reader
     */
    static public PKCS10 readPEMEncoded(Reader reader) throws IOException,
            GeneralSecurityException {
        PEMReader pem= new PEMReader(reader);
        PKCS10CertificationRequest pkcs10csr= (PKCS10CertificationRequest) pem.readObject();
        try {
            pem.close();
        } catch (IOException e) {
            // ignored
            LOG.warn(e);
        }
        PKCS10 pkcs10= new PKCS10(pkcs10csr);
        return pkcs10;
    }

    /**
     * @return The subject DN as string.
     */
    public String getSubject() {
        Principal principal= getPrincipal();
        return principal.getName();
    }

    /**
     * @return The subject DN as Principal
     */
    public Principal getPrincipal() {
        X509Name subject= this.bcPKCS10_.getCertificationRequestInfo().getSubject();
        X509Principal principal= new X509Principal(subject);
        return principal;
    }

    /**
     * Gets the X509Extensions included in the PKCS10.
     * 
     * @return The X509Extensions or <code>null</code> if there is no
     *         X509Extensions.
     */
    public X509Extensions getX509Extensions() {
        X509Extensions x509Extensions= null;
        ASN1Set attributes= this.bcPKCS10_.getCertificationRequestInfo().getAttributes();
        if (attributes.size() > 0) {
            ASN1Sequence attributeSequence= (ASN1Sequence) attributes.getObjectAt(0);
            Attribute attribute= new Attribute(attributeSequence);
            DERObjectIdentifier oid= attribute.getAttrType();
            if (oid.equals(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest)) {
                ASN1Set attributeValues= attribute.getAttrValues();
                if (attributeValues.size() > 0) {
                    ASN1Sequence x509extensionsSequence= (ASN1Sequence) attributeValues.getObjectAt(0);
                    x509Extensions= new X509Extensions(x509extensionsSequence);

                }
            }
        }
        return x509Extensions;
    }

}
