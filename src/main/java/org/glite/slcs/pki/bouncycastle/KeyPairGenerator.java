/*
 * $Id: KeyPairGenerator.java,v 1.1 2007/05/11 11:31:34 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki.bouncycastle;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Wrapper class for the BouncyCastle KeyPairGenerator. Uses a BouncyCastle
 * org.bouncycastle.jce.provider.JDKKeyPairGenerator key pair generator as
 * delegate.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.1 $
 */
public class KeyPairGenerator {

    /** Logging */
    static private Log LOG = LogFactory.getLog(KeyPairGenerator.class);

    /**
     * Sets BouncyCastle security provider as boot time
     */
    static {
        // add only once
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            LOG.info("add BouncyCastle security provider");
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * BouncyCaslte KeyPairGenerator delegate
     */
    private java.security.KeyPairGenerator generator_ = null;

    /**
     * Construtor.
     * 
     * @param algorithm
     * @throws GeneralSecurityException
     */
    public KeyPairGenerator(String algorithm) throws GeneralSecurityException {
        generator_ = java.security.KeyPairGenerator.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * Initializes the generator to generate key pair of the given size.
     * 
     * @param keysize
     *            The size of the keys to generate.
     */
    public void initialize(int keysize) {
        generator_.initialize(keysize);
    }

    /**
     * Generates a new {@link KeyPair}
     * 
     * @return The new {@link KeyPair}
     */
    public KeyPair generateKeyPair() {
        return generator_.generateKeyPair();
    }

}
