/*
 * $Id: CertificateRequestTest.java,v 1.2 2006/10/20 14:20:21 vtschopp Exp $
 * 
 * Created on Jun 14, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.pki;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class CertificateRequestTest extends TestCase {

    private String password= null;

    private String subject= null;
    private String email= null;
    
    private int[] keySizes;

    private List extensions= null;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.password= "password";
        this.subject= "C=CH, O=Switch - Teleinformatikdienste fuer Lehre und Forschung, CN=Valery Tschopp 9FEE5EE3";
        this.email= "tschopp@switch.ch";
        this.keySizes= new int[2];
        this.keySizes[0]= 1024;
        this.keySizes[1]= 2048;
        this.extensions= new ArrayList();
        extensions.add(CertificateExtensionFactory.createCertificateExtension("KeyUsage", "DigitalSignature,KeyEncipherment"));
        extensions.add(CertificateExtensionFactory.createCertificateExtension("ExtendedKeyUsage", "ClientAuth,ServerAuth"));
        extensions.add(CertificateExtensionFactory.createCertificateExtension("CertificatePolicies", "2.16.756.1.2.6.3,2.16.756.1.2.6.4"));
        extensions.add(CertificateExtensionFactory.createCertificateExtension("SubjectAltName","email:" + this.email + ", dns:macvt.switch.ch"));
    }


    public void testStoreCSR() {
        for (int i= 0; i < this.keySizes.length; i++) {
            int size= this.keySizes[i];
            try {
                char[] pass= password.toCharArray();
                CertificateKeys keys= new CertificateKeys(size, pass);
                CertificateRequest csr= new CertificateRequest(keys,
                                                               this.subject,
                                                               this.extensions);
                String filename= "testStoreCSR_" + size + ".csr";
                File file= new File(filename);
                // delete existing
                file.delete();
                file.deleteOnExit();
                csr.storePEM(file);
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }

    public void testStoreAndLoadCSR() {
        for (int i= 0; i < this.keySizes.length; i++) {
            int size= this.keySizes[i];
            try {
                char[] pass= this.password.toCharArray();
                CertificateKeys keys= new CertificateKeys(size, pass);
                CertificateRequest csr0= new CertificateRequest(keys,
                                                                this.subject,
                                                                this.extensions);
                String filename= "testStoreAndLoadCSR_" + size + ".csr";
                File file= new File(filename);
                // delete existing
                file.delete();
                file.deleteOnExit();
                // store
                csr0.storePEM(file);

                // reload
                CertificateRequest csr1= CertificateRequest.loadPEM(file);

                // test principal and subject
                Principal p0= csr0.getPrincipal();
                String s0= p0.getName();
                System.out.println("csr0 principal: " + p0);
                Principal p1= csr1.getPrincipal();
                String s1= p1.getName();
                System.out.println("csr1 principal: " + p1);
                assertEquals("Not the same Principal", p0, p1);
                assertEquals("Not the same subject", s0, s1);
                String pem0= csr0.getPEMEncoded();
                String pem1= csr1.getPEMEncoded();
                System.out.println("PEM:\n" + pem0);
                assertEquals("Not the same PEM encoded", pem0, pem1);

            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

    }
}
