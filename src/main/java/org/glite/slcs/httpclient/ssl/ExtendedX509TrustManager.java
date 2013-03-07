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
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExtendedTrustX509TrustManager can be used to extend the default JSSE
 * {@link X509TrustManager} with additional trusted CAs stored in a trust store.
 * 
 * @author Valery Tschoppp <valery.tschopp@switch.ch>
 */
public class ExtendedX509TrustManager implements X509TrustManager {

    /** The default JSSE TrustManager used as delegate */
    private X509TrustManager defaultTrustManager_ = null;

    /**
     * List of trusted X509Certificate (trusted CA).
     */
    private List<X509Certificate> trustedIssuers_ = null;

    /** Log object for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(ExtendedX509TrustManager.class);

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

    static protected List<X509Certificate> createTrustedIssuers(KeyStore truststore)
            throws KeyStoreException {
        List<X509Certificate> trustedcerts = new ArrayList<X509Certificate>();
        Enumeration<String> aliases = truststore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate trustedcert = truststore.getCertificate(alias);
            if (trustedcert != null && trustedcert instanceof X509Certificate) {
                trustedcerts.add((X509Certificate) trustedcert);
            }
        }
        return trustedcerts;
    }

    static private void dumpTrustedIssuers(List<X509Certificate> trustedIssuers) {
        LOG.debug("Trusted Issuers:");
        for (X509Certificate cert : trustedIssuers) {
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
        for (X509Certificate issuer : trustedIssuers_) {
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
                    LOG.debug(e.getMessage(),e);
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
        for (X509Certificate certificate : trustedIssuers_) {
            allAcceptedIssuers[i] = certificate;
            i++;
        }

        return allAcceptedIssuers;
    }
}
