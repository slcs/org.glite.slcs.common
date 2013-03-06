/*
 * $Id: KeyUsageTest.java,v 1.1 2006/09/21 12:42:58 vtschopp Exp $
 * 
 * Created on Sep 9, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.pki;

import org.bouncycastle.asn1.x509.KeyUsage;

import junit.framework.TestCase;

public class KeyUsageTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testKeyUsage() {
        System.out.println("KeyUsage.digitalSignature:=" + KeyUsage.digitalSignature);
        System.out.println("KeyUsage.keyEncipherment:=" + KeyUsage.keyEncipherment);
    }

}
