/*
 * $Id: ExtendedX509TrustManager.java,v 1.6 2007/03/01 13:46:29 vtschopp Exp $
 * 
 * Created on Aug 8, 2006 by tschopp
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.httpclient.ssl;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ExtendedTrustX509TrustManager can be used to extend the default JSSE
 * {@link X509TrustManager} with additional trusted CAs stored in a trust store.
 * 
 * @author Valery Tschoppp <tschopp@switch.ch>
 * @version $Revision: 1.6 $
 */
public class ExtendedX509TrustManager implements X509TrustManager {

    /** The default JSSE TrustManager used as delegate */
    private X509TrustManager defaultTrustManager_ = null;

    /**
     * List of trusted X509Certificate (trusted CA).
     */
    private List trustedIssuers_ = null;

    /** Log object for this class. */
    private static final Log LOG = LogFactory.getLog(ExtendedX509TrustManager.class);

    /**
     * Constructor for ExtendedX509TrustManager.
     * 
     * @param truststore
     *            The trust KeyStore containing the additional trusted CA.
     * @param defaultTrustManager
     *            The default JSSE X509TrustManager
     * @throws KeyStoreException
     */
    public ExtendedX509TrustManager(KeyStore trustStore,
            X509TrustManager defaultTrustManager) throws KeyStoreException {
        super();
        if (trustStore == null) {
            throw new IllegalArgumentException("Trust KeyStore may not be null");
        }
        if (defaultTrustManager == null) {
            throw new IllegalArgumentException("Default X509TrustManager may not be null");
        }

        defaultTrustManager_ = defaultTrustManager;
        trustedIssuers_ = createTrustedIssuers(trustStore);

        if (LOG.isDebugEnabled()) {
            // dumpTrustStore(trustStore);
            dumpTrustedIssuers(trustedIssuers_);
        }
    }

    static protected List createTrustedIssuers(KeyStore truststore)
            throws KeyStoreException {
        List trustedcerts = new ArrayList();
        Enumeration aliases = truststore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            Certificate trustedcert = truststore.getCertificate(alias);
            if (trustedcert != null && trustedcert instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate) trustedcert;
                trustedcerts.add(cert);
            }
        }
        return trustedcerts;
    }

    static private void dumpTrustedIssuers(List trustedIssuers) {
        LOG.debug("Trusted Issuers:");
        Iterator certs = trustedIssuers.iterator();
        while (certs.hasNext()) {
            X509Certificate cert = (X509Certificate) certs.next();
            dumpCertificate(cert);
        }
    }

    static private void dumpCertificate(X509Certificate cert) {
        LOG.debug("Certificate:");
        LOG.debug("  Subject: " + cert.getSubjectDN());
        LOG.debug("  Issuer: " + cert.getIssuerDN());
        LOG.debug("  Valid from: " + cert.getNotBefore());
        LOG.debug("  Valid until: " + cert.getNotAfter());
        LOG.debug("  Fingerprint: " + getCertificateFingerprint(cert, "MD5"));
    }

    static private String getCertificateFingerprint(
            X509Certificate certificate, String algorithm) {
        byte[] digest = null;
        try {
            byte[] certificateBytes = certificate.getEncoded();
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(certificateBytes);
            digest = md.digest();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new String(algorithm + ": " + byteArrayToHex(digest));
    }

    static private String byteArrayToHex(byte[] byteData) {
        if (byteData == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            if (i != 0)
                sb.append(":");
            int b = byteData[i] & 0xff;
            String hex = Integer.toHexString(b);
            if (hex.length() == 1)
                sb.append("0");
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String
     *      authType)
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // use delegate for client certificates
        if (LOG.isDebugEnabled()) {
            LOG.debug("Certificate chain:");
            if (chain != null) {
                for (int i = 0; i < chain.length; i++) {
                    X509Certificate certificate = chain[i];
                    LOG.debug(i + ": S: "
                            + certificate.getSubjectX500Principal());
                    LOG.debug(i + ": I: "
                            + certificate.getIssuerX500Principal());
                }
            }
        }
        defaultTrustManager_.checkClientTrusted(chain, authType);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String
     *      authType)
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Certificate chain:");
            if (chain != null) {
                for (int i = 0; i < chain.length; i++) {
                    X509Certificate certificate = chain[i];
                    LOG.debug(i + ": S: " + certificate.getSubjectDN());
                    LOG.debug(i + ": I: " + certificate.getIssuerDN());
                }
            }
        }
        try {
            // delegate to default JSSE TrustManager
            defaultTrustManager_.checkServerTrusted(chain, authType);
        } catch (CertificateException ce) {
            LOG.debug("Extended checking of certificate chain");
            // Start with the root and see if the subject or the issuer is
            // in the trustedIssuers HashTable.
            // The root is at the end of the chain.
            boolean trusted = false;
            for (int i = chain.length - 1; i >= 0; i--) {
                X509Certificate cert = chain[i];

                if (isCertificateIssuerTrusted(cert)) {
                    LOG.debug("Trusted X509 Issuer: " + cert.getIssuerDN());
                    trusted = true;
                    break;
                }
                else if (isCertificateTrusted(cert)) {
                    LOG.debug("Trusted X509 Certificate: "
                            + cert.getSubjectDN());
                    trusted = true;
                    break;
                }
            }

            if (!trusted) {
                LOG.error("No suitable trusted certificate found in truststore: ", ce);
                throw ce;
            }

        }
    }

    /**
     * Checks if the certificate is store in our trust store.
     * 
     * @param cert
     *            The X509 certificate to check.
     * @return <code>true</code> if the certificate is in trustedIssuers
     *         hashtable as value.
     */
    protected boolean isCertificateTrusted(X509Certificate cert) {
        return trustedIssuers_.contains(cert);
    }

    /**
     * Returns <code>true</code> iff the certificate issuer is in our trust
     * store and it have signed the cert.
     * 
     * @param cert
     *            The X509 certificate to check.
     * @return <code>true</code> if the certificate issuer is in
     *         trustedIssuers list and have signed the cert.
     */
    protected boolean isCertificateIssuerTrusted(X509Certificate cert) {
        //TODO: checks CA CRL
        // checks if an trusted issuer have signed the certificate
        boolean trusted = false;
        Iterator issuers = trustedIssuers_.iterator();
        while (issuers.hasNext()) {
            X509Certificate issuer = (X509Certificate) issuers.next();
            PublicKey issuerPublicKey = issuer.getPublicKey();
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("checking: " + issuer.getSubjectDN());
                }
                cert.verify(issuerPublicKey);
                trusted = true;
                break;
            } catch (GeneralSecurityException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e);
                }
            }
        }

        if (!trusted) {
            LOG.warn("No trusted issuer found in TrustStore for: " + cert.getSubjectDN());
        }

        return trusted;
    }

    /**
     * Merges the system wide accepted issuers and the own ones and returns
     * them.
     * 
     * @return Array of X509 certificates of the accepted issuers.
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] defaultAcceptedIssuers = defaultTrustManager_.getAcceptedIssuers();

        // merge JSSE default and trusted CA from truststore
        int length = trustedIssuers_.size() + defaultAcceptedIssuers.length;
        X509Certificate[] allAcceptedIssuers = new X509Certificate[length];
        int i = 0;
        for (int j = 0; j < defaultAcceptedIssuers.length; j++) {
            X509Certificate certificate = defaultAcceptedIssuers[j];
            allAcceptedIssuers[i] = certificate;
            i++;
        }
        Iterator trustedCerts = trustedIssuers_.iterator();
        while (trustedCerts.hasNext()) {
            X509Certificate certificate = (X509Certificate) trustedCerts.next();
            allAcceptedIssuers[i] = certificate;
            i++;
        }

        return allAcceptedIssuers;
    }
}
