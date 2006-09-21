/*
 * $Id: CertificateKeysTest.java,v 1.1 2006/09/21 12:42:58 vtschopp Exp $
 * 
 * Created on Jun 14, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.pki;

import java.io.File;
import java.security.GeneralSecurityException;

import org.glite.slcs.pki.CertificateKeys;


import junit.framework.TestCase;

public class CertificateKeysTest extends TestCase {

    public void testGenerateKeys() {
        String password= "password";
        int[] keySizes= { 512, 1024, 2048 };
        for (int i= 0; i < keySizes.length; i++) {
            int size= keySizes[i];
            try {
                new CertificateKeys(size, password);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }

    public void testStoreKeys() {
        String password= "password";
        // TEST drive: check with 'openssl rsa -check -in <filename.key>'
        int[] keySizes= { 512, 1024, 2048 };
        for (int i= 0; i < keySizes.length; i++) {
            int size= keySizes[i];
            try {
                CertificateKeys keys= new CertificateKeys(size, password);
                keys.getPEMPrivate();
                String filename= "junit_" + size + ".key";
                File file= new File(filename);
                // delete existing
                file.delete();
                file.deleteOnExit();
                keys.storePEMPrivate(file);
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }

}
