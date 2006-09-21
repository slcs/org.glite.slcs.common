package org.glite.slcs.httpclient.ssl;

import java.io.FileInputStream;
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
 * Protocol https= new Protocol(&quot;https&quot;,
 *                              new ExtendedProtocolSocketFactory(&quot;my.truststore.jks&quot;),
 *                              443);
 * 
 * HttpClient client= new HttpClient();
 * client.getHostConfiguration().setHost(&quot;localhost&quot;, 443, https);
 * // use relative url only
 * GetMethod httpget= new GetMethod(&quot;/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 * <p>
 * Example of using custom protocol socket factory per default instead of the
 * standard one:
 * 
 * <pre>
 * Protocol https= new Protocol(&quot;https&quot;,
 *                              new ExtendedProtocolSocketFactory(&quot;my.truststore.jks&quot;),
 *                              443);
 * Protocol.registerProtocol(&quot;https&quot;, https);
 * 
 * HttpClient client= new HttpClient();
 * GetMethod httpget= new GetMethod(&quot;https://localhost/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 */

public class ExtendedProtocolSocketFactory implements ProtocolSocketFactory {

    private static String SSL_CONTEXT_PROTOCOL= "SSL";

    /** Log object for this class. */
    private static final Log LOG= LogFactory.getLog(ExtendedProtocolSocketFactory.class);

    private String truststorePath_= null;

    private String keystorePath_= null;

    private String keystorePassword_= null;

    private SSLContext sslContext_= null;

    /**
     * Extends the trust capabilities of the ProtocolSocketFactory with the
     * given TrustStore.
     * 
     * @param truststorePath
     *            Path of the truststore file (JKS).
     * @throws IOException 
     * @throws Exception
     */
    public ExtendedProtocolSocketFactory(String truststorePath) throws IOException {
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
     */
    public ExtendedProtocolSocketFactory(String keystorePath,
            String keystorePassword, String truststorePath) throws IOException  {
        super();
        this.keystorePath_= keystorePath;
        this.keystorePassword_= keystorePassword;
        this.truststorePath_= truststorePath;

        // init the SSL context
        this.sslContext_= createSSLContext();

    }

    private KeyStore createTrustStore(String path) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        if (path == null) {
            throw new IllegalArgumentException("Trust keystore path may not be null");
        }
        LOG.debug("Initializing trust store: " + path);
        KeyStore keystore= KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fis= new FileInputStream(path);
        keystore.load(fis, null);
        return keystore;
    }

    private KeyStore createKeyStore(String path, String password)
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        if (path == null) {
            throw new IllegalArgumentException("Key keystore path may not be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Key keystore password may not be null");
        }
        LOG.debug("Initializing key store: " + path);
        KeyStore keystore= KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fis= new FileInputStream(path);
        keystore.load(fis, password.toCharArray());
        return keystore;
    }

    private KeyManager[] createKeyManagers(KeyStore keystore, String password)
            throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException {
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Keystore password may not be null");
        }
        LOG.debug("Initializing key manager");
        KeyManagerFactory kmfactory= KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, password.toCharArray());
        return kmfactory.getKeyManagers();
    }

    private TrustManager[] createExtendedTrustManagers(KeyStore truststore)
            throws KeyStoreException, NoSuchAlgorithmException,
            NoSuchProviderException {
        if (truststore == null) {
            throw new IllegalArgumentException("Truststore may not be null");
        }
        LOG.debug("Initializing TrustManager");
        // initialize with JSSE default trustStore
        TrustManagerFactory tmfactory= TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init((KeyStore) null);
        TrustManager[] trustmanagers= tmfactory.getTrustManagers();
        // extend the default TrustManager
        // LOG.debug("default JSSE TrustManager# " + trustmanagers.length);
        for (int i= 0; i < trustmanagers.length; i++) {
            if (trustmanagers[i] instanceof X509TrustManager) {
                LOG.debug("Installing the ExtendedTrustX509TrustManager["
                        + truststorePath_ + "]");
                trustmanagers[i]= new ExtendedX509TrustManager(truststore,
                                                               (X509TrustManager) trustmanagers[i]);
            }
        }
        return trustmanagers;
    }

    private SSLContext createSSLContext() throws IOException {
        KeyManager[] keymanagers= null;
        TrustManager[] trustmanagers= null;
        SSLContext sslcontext= null;
        LOG.debug("Create the extended SSLContext");
        if (keystorePath_ != null && keystorePassword_ != null) {
            try {
                LOG.debug("Create the KeyManagers[" + keystorePath_ + "]");
                KeyStore keystore= createKeyStore(keystorePath_,
                                                  keystorePassword_);
                keymanagers= createKeyManagers(keystore, keystorePassword_);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to create the KeyManagers", e);
            } catch (IOException e) {
                LOG.error("Failed to create the KeyManagers", e);
                throw e;
            }
        }
        if (truststorePath_ != null) {
            try {
                LOG.debug("Create the extended TrustManagers["
                        + truststorePath_ + "]");
                KeyStore truststore= createTrustStore(this.truststorePath_);
                trustmanagers= createExtendedTrustManagers(truststore);
            } catch (GeneralSecurityException e) {
                LOG.error("Failed to create the TrustManagers", e);
            } catch (IOException e) {
                LOG.error("Failed to create the TrustManagers", e);
                throw e;
            }
        }
        try {
            sslcontext= SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
            sslcontext.init(keymanagers, trustmanagers, null);
        } catch (GeneralSecurityException e) {
            // e.printStackTrace();
            LOG.error("Failed to initialize the SSL context", e);
        }
        return sslcontext;

    }

    private synchronized SSLContext getSSLContext() throws IOException {
        if (this.sslContext_ == null) {
            this.sslContext_= createSSLContext();
        }
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
        int timeout= params.getConnectionTimeout();
        SocketFactory socketfactory= getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host,
                                              port,
                                              localAddress,
                                              localPort);
        }
        Socket socket= socketfactory.createSocket();
        SocketAddress localaddr= new InetSocketAddress(localAddress,
                                                       localPort);
        SocketAddress remoteaddr= new InetSocketAddress(host, port);
        socket.bind(localaddr);
        socket.connect(remoteaddr, timeout);
        return socket;
    }

    /**
     * @see ProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
     */
    public Socket createSocket(String host, int port, InetAddress clientHost,
            int clientPort) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host,
                                                               port,
                                                               clientHost,
                                                               clientPort);
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
        return getSSLContext().getSocketFactory().createSocket(socket,
                                                               host,
                                                               port,
                                                               autoClose);
    }
}
