/*
 * Copyright (c) 2010-2013 SWITCH
 * Copyright (c) 2006-2010 Members of the EGEE Collaboration
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
