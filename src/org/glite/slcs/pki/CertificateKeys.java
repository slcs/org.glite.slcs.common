package org.glite.slcs.pki;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.glite.slcs.pki.bouncycastle.Codec;
import org.glite.slcs.util.Utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * CertificateKeys is a wrapper class for a KeyPair. Adds functionalities to
 * store the PrivateKey encrypted in PEM format.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class CertificateKeys {

    /** Logging */
    static private Log LOG= LogFactory.getLog(CertificateKeys.class);

    /** Default keys algorithm */
    private static final String KEY_ALGORITHM= "RSA";

    /** Default keys size */
    private static final int KEY_SIZE= 1024;

    /** Default unix file permission for stored private key */
    private static final int KEY_FILE_PERMISSION= 600;

    /** The public/private key pair */
    private KeyPair keyPair_= null;

    /** The private key password */
    private String password_= null;

    /**
     * 
     * @param keySize
     * @param password
     * @throws GeneralSecurityException
     */
    public CertificateKeys(int keySize, String password)
            throws GeneralSecurityException {
        try {
            KeyPairGenerator generator= KeyPairGenerator.getInstance(KEY_ALGORITHM);
            generator.initialize(keySize);
            keyPair_= generator.generateKeyPair();
            password_= password;
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
    public CertificateKeys(String password) throws GeneralSecurityException {
        this(KEY_SIZE, password);
    }

    /**
     * Contructor. Unencrypted private and public key with a given key size.
     * 
     * @param keySize
     *            512, 1024 or 2048, The key length.
     * 
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
     * @param The
     *            private password.
     */
    public void setPassword(String password) {
        this.password_= password;
    }

    /**
     * 
     * @param filename
     * @throws IOException
     */
    public void storePEMPrivate(String filename) throws IOException {
        File file= new File(filename);
        storePEMPrivate(file);
    }

    /**
     * 
     * @param file
     * @throws IOException
     */
    public void storePEMPrivate(File file) throws IOException {
        boolean permOk= Utils.setFilePermissions(file, KEY_FILE_PERMISSION);
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
     * 
     * @return
     * @throws IOException
     */
    public String getPEMPrivate() throws IOException {
        if (password_ != null) {
            return Codec.getPEMEncoded(getPrivate(), password_);
        }
        return Codec.getPEMEncoded(getPrivate());
    }

}
