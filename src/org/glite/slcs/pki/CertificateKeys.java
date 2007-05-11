/*
 * $Id: CertificateKeys.java,v 1.4 2007/05/11 11:33:05 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.pki;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.pki.bouncycastle.Codec;
import org.glite.slcs.pki.bouncycastle.KeyPairGenerator;
import org.glite.slcs.util.Utils;

/**
 * CertificateKeys is a wrapper class for a KeyPair. Adds functionalities to
 * store the PrivateKey encrypted in PEM format.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.4 $
 */
public class CertificateKeys {

    /** Logging */
    static private Log LOG = LogFactory.getLog(CertificateKeys.class);

    /** Default keys algorithm */
    private static final String KEY_ALGORITHM = "RSA";

    /** Default keys size */
    private static final int KEY_SIZE = 1024;

    /** Default unix file permission for stored private key */
    private static final int KEY_FILE_PERMISSION = 600;

    /** The public/private key pair */
    private KeyPair keyPair_ = null;

    /** The private key password */
    private char[] password_ = null;

    /**
     * Creates a new key pair (private and public) for the given key size. The
     * password is used to store the private key crypted.
     * 
     * @param keySize
     *            The keys size.
     * @param password
     *            The password to store the private key crypted.
     * @throws GeneralSecurityException
     *             If an error occurs.
     */
    public CertificateKeys(int keySize, char[] password)
            throws GeneralSecurityException {
        try {
            KeyPairGenerator generator = new KeyPairGenerator(KEY_ALGORITHM);
            generator.initialize(keySize);
            keyPair_ = generator.generateKeyPair();
            password_ = password;
        } catch (NoSuchAlgorithmException e) {
            // shoud never occurs
            LOG.error("Failed to create keys", e);
            throw e;
        }
    }

    /**
     * Constructor. Encrypted private key and public key with default private
     * key length of 1024.
     * 
     * @param password
     *            The encryption password.
     * @throws GeneralSecurityException
     */
    public CertificateKeys(char[] password) throws GeneralSecurityException {
        this(KEY_SIZE, password);
    }

    /**
     * Contructor. Unencrypted private and public key with a given key size.
     * 
     * @param keySize
     *            512, 1024 or 2048, The key length.
     * @throws GeneralSecurityException
     */
    public CertificateKeys(int keySize) throws GeneralSecurityException {
        this(keySize, null);
    }

    /**
     * Constructor. Default keySize is <code>1024</code>.
     * 
     * @throws GeneralSecurityException
     */
    public CertificateKeys() throws GeneralSecurityException {
        this(KEY_SIZE, null);
    }

    /**
     * @return The private key or <code>null</code> if the key pair doesn't
     *         exist
     */
    public PrivateKey getPrivate() {
        if (keyPair_ == null) {
            return null;
        }
        return keyPair_.getPrivate();
    }

    /**
     * @return The public key or <code>null</code> if the key pair doesn't
     *         exist
     */
    public PublicKey getPublic() {
        if (keyPair_ == null) {
            return null;
        }
        return keyPair_.getPublic();

    }

    /**
     * Sets the private key encryption password.
     * 
     * @param password
     *            The private key password.
     */
    public void setPassword(String password) {
        this.password_ = password.toCharArray();
    }

    /**
     * Sets the private key encryption password.
     * 
     * @param password
     *            The private key password.
     */
    public void setPassword(char[] password) {
        this.password_ = password;
    }

    /**
     * Stores the private key in PEM format in the given filename. If the
     * password is set the private key is store encrypted.
     * 
     * @param filename
     *            The filename of the PEM file.
     * @throws IOException
     *             If an IO error occurs.
     */
    public void storePEMPrivate(String filename) throws IOException {
        File file = new File(filename);
        storePEMPrivate(file);
    }

    /**
     * Stores the private key in PEM format in the given file. If the password
     * is set the private key is store encrypted.
     * 
     * @param file
     *            The PEM file.
     * @throws IOException
     *             If an IO error occurs.
     */
    public void storePEMPrivate(File file) throws IOException {
        boolean permOk = Utils.setFilePermissions(file, KEY_FILE_PERMISSION);
        if (!permOk) {
            LOG.warn("Failed to set permissions: " + KEY_FILE_PERMISSION
                    + " for file: " + file);
        }
        if (password_ != null) {
            Codec.storePEMEncoded(getPrivate(), password_, file);
        }
        else {
            Codec.storePEMEncoded(getPrivate(), file);
        }
    }

    /**
     * Gets the private key PEM encoded. If the password is set, the PEM block
     * is crypted.
     * 
     * @return The PEM encoded private key.
     * @throws IOException
     */
    public String getPEMPrivate() throws IOException {
        if (password_ != null) {
            return Codec.getPEMEncoded(getPrivate(), password_);
        }
        return Codec.getPEMEncoded(getPrivate());
    }

    /**
     * @return The private key password.
     */
    public char[] getPassword() {
        return password_;
    }

}
