/*
 * Copyright (c) Members of the EGEE Collaboration. 2007.
 * See http://www.eu-egee.org/partners/ for details on the copyright
 * holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Auhtor: Valery Tschopp <tschopp@switch.ch>
 * Version: $Id: ExtendedProtocolSocketFactory.java,v 1.6 2009/09/15 12:32:09 vtschopp Exp $
 */
package org.glite.slcs.httpclient.ssl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ExtendedProtocolSocketFactory extends the default system TrustManager with
 * your custom truststore (KeyStore of trusted CA).
 * 
 * <p>
 * Use JDK keytool utility to import a trusted certificate and generate a
 * truststore file:
 * 
 * <pre>
 *                 keytool -import -alias &quot;my server cert&quot; -file server.crt -keystore my.truststore
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Example of using custom protocol socket factory for a specific host:
 * 
 * <pre>
 * Protocol https = new Protocol(&quot;https&quot;, new ExtendedProtocolSocketFactory(
 *      &quot;my.truststore.jks&quot;), 443);
 * 
 * HttpClient client = new HttpClient();
 * client.getHostConfiguration().setHost(&quot;localhost&quot;, 443, https);
 * // use relative url only
 * GetMethod httpget = new GetMethod(&quot;/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 * <p>
 * Example of using custom protocol socket factory per default instead of the
 * standard one:
 * 
 * <pre>
 * Protocol https = new Protocol(&quot;https&quot;, new ExtendedProtocolSocketFactory(
 *      &quot;my.truststore.jks&quot;), 443);
 * Protocol.registerProtocol(&quot;https&quot;, https);
 * 
 * HttpClient client = new HttpClient();
 * GetMethod httpget = new GetMethod(&quot;https://localhost/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.6 $
 */
public class ExtendedProtocolSocketFactory implements SecureProtocolSocketFactory {

    private static String SSL_CONTEXT_PROTOCOL = "SSL";

    /** Log object for this class. */
    private static final Log LOG = LogFactory
            .getLog(ExtendedProtocolSocketFactory.class);

    /** The SSLContext used by the factory to create the SSL sockets */
    private SSLContext sslContext_ = null;

    /**
     * Extends the trust capabilities of the ProtocolSocketFactory with the
     * given TrustStore.
     * 
     * @param truststorePath
     *            Path of the truststore file (JKS).
     * @throws IOException
     *             If an error occurs while loading the truststore.
     * @throws GeneralSecurityException
     *             If an error occurs while initializing the {@link SSLContext}.
     */
    public ExtendedProtocolSocketFactory(String truststorePath)
            throws IOException, GeneralSecurityException {
        this(null, null, truststorePath);
    }

    /**
     * Extends the trust and client authentication of the ProtocolSocketFactory
     * with the given KeyStore and TrustStore.
     * 
     * @param keystorePath
     *            Path of the keystore file (JKS).
     * @param keystorePassword
     *            Password of the keystore.
     * @param truststorePath
     *            Path of the truststore file (JKS).
     * @throws IOException
     *             If an error occurs while loading the keystore or the
     *             truststore.
     * @throws GeneralSecurityException
     *             If an error occurs while initializing the {@link SSLContext}.
     * 
     */
    public ExtendedProtocolSocketFactory(String keystorePath,
            String keystorePassword, String truststorePath) throws IOException,
            GeneralSecurityException {
        super();

        KeyStore keystore = null;
        KeyStore truststore = null;
        if (keystorePath != null && keystorePassword != null) {
            try {
                LOG.debug("Create the KeyStore[" + keystorePath + "]");
                keystore = createKeyStore(keystorePath, keystorePassword);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to create the KeyStore: " + keystorePath, e);
                throw e;
            } catch (IOException e) {
                LOG.error("Failed to load the KeyStore: " + keystorePath, e);
                throw e;
            }
        }
        if (truststorePath != null) {
            try {
                LOG.debug("Create TrustStore[" + truststorePath + "]");
                truststore = createTrustStore(truststorePath);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to create the TrustStore: " + truststorePath,
                        e);
                throw e;
            } catch (IOException e) {
                LOG
                        .error("Failed to load the TrustStore: "
                                + truststorePath, e);
                throw e;
            }
        }

        // create the SSL context
        this.sslContext_ = createSSLContext(keystore, keystorePassword,
                truststore);

    }

    /**
     * Creates a {@link ExtendedProtocolSocketFactory} with the given keystore
     * and keystore password, and the truststore.
     * 
     * @param keystore
     *            The already loaded keystore object
     * @param keystorePassword
     *            The password of the keystore.
     * @param truststore
     *            The already loaded truststore object
     * @throws IOException
     *             Should never occurs in this context
     * @throws GeneralSecurityException
     *             If an error occurs while initializing the {@link SSLContext}.
     */
    public ExtendedProtocolSocketFactory(KeyStore keystore,
            String keystorePassword, KeyStore truststore) throws IOException,
            GeneralSecurityException {
        // create the SSL context
        this.sslContext_ = createSSLContext(keystore, keystorePassword,
                truststore);
    }

    /**
     * Creates and loads a truststore.
     * 
     * @param path
     *            The truststore filename in classpath or the absolute filename
     * @return A new initialized {@link KeyStore} containing the trust anchors.
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     *             If an error occurs while loading the truststore.
     */
    private KeyStore createTrustStore(String path) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        if (path == null) {
            throw new IllegalArgumentException(
                    "Trust keystore path may not be null");
        }
        // first search file in classpath, then as absolute filename
        LOG.debug("Load truststore from classpath: /" + path);
        InputStream is = getClass().getResourceAsStream("/" + path);
        if (is == null) {
            LOG.debug("Not in classpath, load truststore from file: " + path);
            is = new FileInputStream(path);
        }
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, null);
        return keystore;
    }

    /**
     * Creates and loads a keystore.
     * 
     * @param path
     *            The keystore filename in classpath or the absolute filename
     * @param password
     *            The keystore password.
     * @return A new initialized {@link KeyStore} containing the client certificate and key.
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     *             If an error occurs while loading the truststore.
     */
    private KeyStore createKeyStore(String path, String password)
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        if (path == null) {
            throw new IllegalArgumentException(
                    "Key keystore path may not be null");
        }
        if (password == null) {
            throw new IllegalArgumentException(
                    "Key keystore password may not be null");
        }
        // first search file in classpath, then as absolute filename
        LOG.debug("Load keystore from classpath: /" + path);
        InputStream is = getClass().getResourceAsStream("/" + path);
        if (is == null) {
            LOG.debug("Not in classpath, load keystore from file: " + path);
            is = new FileInputStream(path);
        }
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, password.toCharArray());
        return keystore;
    }

    private KeyManager[] createKeyManagers(KeyStore keystore, String password)
            throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException {
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        if (password == null) {
            throw new IllegalArgumentException(
                    "Keystore password may not be null");
        }
        LOG.debug("Initializing key manager");
        KeyManagerFactory kmfactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, password.toCharArray());
        return kmfactory.getKeyManagers();
    }

    /**
     * Adds the given truststore to the existing default JSSE {@link TrustManager}.
     * 
     * @param truststore The truststore to add to the list.
     * @return An array of {@link TrustManager}
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    private TrustManager[] createExtendedTrustManagers(KeyStore truststore)
            throws KeyStoreException, NoSuchAlgorithmException,
            NoSuchProviderException {
        if (truststore == null) {
            throw new IllegalArgumentException("Truststore may not be null");
        }
        LOG.debug("Initializing TrustManager");
        // initialize with the JSSE default trustStore
        TrustManagerFactory tmfactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init((KeyStore) null);
        TrustManager[] trustmanagers = tmfactory.getTrustManagers();
        // extend the default TrustManager
        // LOG.debug("default JSSE TrustManager# " + trustmanagers.length);
        for (int i = 0; i < trustmanagers.length; i++) {
            if (trustmanagers[i] instanceof X509TrustManager) {
                LOG.debug("Installing the ExtendedTrustX509TrustManager");
                trustmanagers[i] = new ExtendedX509TrustManager(truststore,
                        (X509TrustManager) trustmanagers[i]);
            }
        }
        return trustmanagers;
    }

    /**
     * Creates the {@link SSLContext} used by the {@link ProtocolSocketFactory}
     * to create SSL sockets.
     * 
     * @param keystore
     *            The already loaded keystore object
     * @param keystorePassword
     *            The password of the keystore.
     * @param truststore
     *            The already loaded truststore object
     * @return The initialized {@link SSLContext}
     * @throws GeneralSecurityException
     *             If an error occurs while creating the {@link KeyManager} or
     *             the {@link TrustManager} or while initializing the
     *             {@link SSLContext}
     */
    private SSLContext createSSLContext(KeyStore keystore,
            String keystorePassword, KeyStore truststore)
            throws GeneralSecurityException {
        KeyManager[] keymanagers = null;
        TrustManager[] trustmanagers = null;
        SSLContext sslcontext = null;
        LOG.debug("Create the extended SSLContext");
        if (keystore != null && keystorePassword != null) {
            try {
                LOG.debug("Create the KeyManagers");
                keymanagers = createKeyManagers(keystore, keystorePassword);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to create the KeyManagers", e);
                throw e;
            }
        }
        if (truststore != null) {
            try {
                LOG.debug("Create the extended TrustManagers");
                trustmanagers = createExtendedTrustManagers(truststore);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to create the extended TrustManagers", e);
                throw e;
            }
        }
        try {
            sslcontext = SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
            sslcontext.init(keymanagers, trustmanagers, null);
        } catch (GeneralSecurityException e) {
            // e.printStackTrace();
            LOG.error("Failed to initialize the SSL context", e);
            throw e;
        }
        return sslcontext;

    }

    /**
     * Callback for the createSocket(...) functions.
     * 
     * @return The initialized {@link SSLContext}
     */
    private SSLContext getSSLContext() {
        return this.sslContext_;
    }

    /**
     * Attempts to get a new socket connection to the given host within the
     * given time limit.
     * <p>
     * To circumvent the limitations of older JREs that do not support connect
     * timeout a controller thread is executed. The controller thread attempts
     * to create a new socket within the given limit of time. If socket
     * constructor does not return until the timeout expires, the controller
     * terminates and throws an {@link ConnectTimeoutException}
     * </p>
     * 
     * @param host
     *            the host name/IP
     * @param port
     *            the port on the host
     * @param clientHost
     *            the local host name/IP to bind the socket to
     * @param clientPort
     *            the port on the local machine
     * @param params
     *            {@link HttpConnectionParams Http connection parameters}
     * 
     * @return Socket a new socket
     * 
     * @throws IOException
     *             if an I/O error occurs while creating the socket
     * @throws UnknownHostException
     *             if the IP address of the host cannot be determined
     */
    public Socket createSocket(final String host, final int port,
            final InetAddress localAddress, final int localPort,
            final HttpConnectionParams params) throws IOException,
            UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress,
                    localPort);
        }
        Socket socket = socketfactory.createSocket();
        SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
        SocketAddress remoteaddr = new InetSocketAddress(host, port);
        socket.bind(localaddr);
        socket.connect(remoteaddr, timeout);
        return socket;
    }

    /**
     * @see ProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
     */
    public Socket createSocket(String host, int port, InetAddress clientHost,
            int clientPort) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port,
                clientHost, clientPort);
    }

    /**
     * @see ProtocolSocketFactory#createSocket(java.lang.String,int)
     */
    public Socket createSocket(String host, int port) throws IOException,
            UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    /**
     * @see ProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
     */
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(socket, host,
                port, autoClose);
    }
}
