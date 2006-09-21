/*
 * $Id: PKCS10Test.java,v 1.1 2006/09/21 12:42:59 vtschopp Exp $
 * 
 * Created on Aug 22, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.pki.bouncycastle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;

public class PKCS10Test extends TestCase {

    private static String KEY_ALGORITHM= "RSA";

    private static int KEY_SIZE= 1024;

    private String subject= null;

    private String email= null;

    private KeyPair keys= null;

    protected void setUp() throws Exception {
        super.setUp();
        this.subject= "C=CH, O=Switch - Teleinformatikdienste fuer Lehre und Forschung, CN=Valery Tschopp 9FEE5EE3";
        this.email= "tschopp@switch.ch";
        KeyPairGenerator generator= KeyPairGenerator.getInstance(KEY_ALGORITHM);
        generator.initialize(KEY_SIZE);
        this.keys= generator.generateKeyPair();

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.glite.slcs.pki.bouncycastle.PKCS10.PKCS10(String,
     * PublicKey, PrivateKey, X509Extensions)'
     */
    public void testPKCS10StringPublicKeyPrivateKeyX509Extensions()
            throws GeneralSecurityException, IOException {
        X509Extensions x509Extensions= createX509Extensions();
        PKCS10 pkcs10= new PKCS10(subject,
                                  keys.getPublic(),
                                  keys.getPrivate(),
                                  x509Extensions);

        // store csr
        String basename= "testPKCS10StringPublicKeyPrivateKeyX509Extensions";
        File pemFile= new File(basename + ".csr");
        pemFile.delete();
        pemFile.deleteOnExit();
        pkcs10.storePEMEncoded(pemFile);
        File derFile= new File(basename + ".der");
        derFile.delete();
        derFile.deleteOnExit();
        pkcs10.storeDEREncoded(derFile);

    }

    public void testGetX509Extensions() throws GeneralSecurityException,
            IOException {
        X509Extensions x509Extensions= createX509Extensions();
        PKCS10 pkcs10= new PKCS10(subject,
                                  keys.getPublic(),
                                  keys.getPrivate(),
                                  x509Extensions);
        String basename= "testGetX509Extensions";
        File pemFile= new File(basename + ".csr");
        pemFile.delete();
        pemFile.deleteOnExit();
        pkcs10.storePEMEncoded(pemFile);

        // reload the PEM file
        FileReader reader= new FileReader(pemFile);
        PKCS10 pkcs10_reloaded= PKCS10.readPEMEncoded(reader);
        X509Extensions x509Extensions_reloaded= pkcs10_reloaded.getX509Extensions();
        if (x509Extensions_reloaded != null) {
            // dump the X509extensions
            Enumeration oids= x509Extensions_reloaded.oids();
            while (oids.hasMoreElements()) {
                DERObjectIdentifier oid= (DERObjectIdentifier) oids.nextElement();
                System.out.println("X509Extension OID: " + oid);
                X509Extension x509Extension= x509Extensions_reloaded.getExtension(oid);
                System.out.println("X509Extension: " + x509Extension.getValue());

            }
        }
    }

    private X509Extensions createX509Extensions() {
        // extensions
        Hashtable extensions= new Hashtable();

        // Key Usage: Digital Signature + Key Encipherment
        KeyUsage keyUsage= new KeyUsage(KeyUsage.digitalSignature
                + KeyUsage.keyEncipherment);
        X509Extension keyUsageExtension= new X509Extension(true,
                                                           new DEROctetString(keyUsage));
        extensions.put(X509Extensions.KeyUsage, keyUsageExtension);

        // Extended Key Usage: TLS Web Client Authentication
        ExtendedKeyUsage extendedKeyUsage= new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth);
        X509Extension extendedKeyUsageExtension= new X509Extension(false,
                                                                   new DEROctetString(extendedKeyUsage));
        extensions.put(X509Extensions.ExtendedKeyUsage,
                       extendedKeyUsageExtension);

        // Certificate Policies: 2.16.756.1.2.6.3
        DERObjectIdentifier policyOID= new DERObjectIdentifier("2.16.756.1.2.6.3");
        PolicyInformation policyInformation= new PolicyInformation(policyOID);
        DERSequence certificatePolicies= new DERSequence(policyInformation);
        X509Extension certificatePoliciesExtension= new X509Extension(false,
                                                                      new DEROctetString(certificatePolicies));
        extensions.put(X509Extensions.CertificatePolicies,
                       certificatePoliciesExtension);

        // SubjectAltName: <email>
        GeneralName subjectAltName= new GeneralName(GeneralName.rfc822Name,
                                                    this.email);
        GeneralNames subjectAltNames= new GeneralNames(subjectAltName);
        X509Extension subjectAltNameExtension= new X509Extension(false,
                                                                 new DEROctetString(subjectAltNames));
        extensions.put(X509Extensions.SubjectAlternativeName,
                       subjectAltNameExtension);

        // create the X509Extensions object
        X509Extensions x509Extensions= new X509Extensions(extensions);
        return x509Extensions;
    }

}
