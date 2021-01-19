/**
 * Copyright 2021 Stephen Powis https://github.com/sourcelaborg/java-hkp-client
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sourcelab.hkp.rest;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sourcelab.hkp.ConnectionFailedException;
import org.sourcelab.hkp.config.Configuration;
import org.sourcelab.hkp.request.Request;
import org.sourcelab.hkp.rest.handlers.RestResponseHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

/**
 * RestClient implementation using HTTPClient.
 */
public class HttpClient5RestClient implements RestClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient5RestClient.class);

    /**
     * Save a copy of the configuration.
     */
    private Configuration configuration;

    /**
     * Our underlying Http Client.
     */
    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;

    /**
     * Constructor.
     */
    public HttpClient5RestClient() {
    }

    /**
     * Initialization method.  This takes in the configuration and sets up the underlying
     * http client appropriately.
     * @param configuration The user defined configuration.
     */
    @Override
    public void init(final Configuration configuration) {
        // Save reference to configuration
        this.configuration = configuration;

        // Create default SSLContext
        final SSLContext sslcontext = SSLContexts.createDefault();

        // Initialize ssl context with configured key and trust managers.
        try {
            sslcontext.init(new KeyManager[0], getTrustManagers(), new SecureRandom());
        } catch (final KeyManagementException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }

        // Create hostname verifier instance.
        final HostnameVerifier hostnameVerifier;
        // Emit an warning letting everyone know we're using an insecure configuration.
        if (configuration.isIgnoreInvalidSslCertificates()) {
            logger.warn("Using insecure configuration, skipping server-side certificate validation checks.");

            // If we're configured to ignore invalid certificates, use the Noop verifier.
            hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        } else {
            // Use default implementation
            hostnameVerifier = new DefaultHostnameVerifier();
        }

        // Allow TLSv1_1 and TLSv1_2 protocols
        final SSLConnectionSocketFactory sslsf = SSLConnectionSocketFactoryBuilder.create()
            .setSslContext(sslcontext)
            .setTlsVersions(TLS.V_1_1, TLS.V_1_2)
            .setHostnameVerifier(hostnameVerifier)
            .build();
        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
            .setSSLSocketFactory(sslsf)
            .build();

        // Setup client builder
        final HttpClientBuilder clientBuilder = HttpClientBuilder.create()
            .setConnectionManager(cm);

        // Define our RequestConfigBuilder
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        // Define our context
        httpClientContext = HttpClientContext.create();

        // Define our auth cache
        final AuthCache authCache = new BasicAuthCache();

        // If we have a configured proxy host
        if (configuration.hasProxyConfigured()) {
            // Define proxy host
            final HttpHost proxyHost = new HttpHost(
                configuration.getProxyConfiguration().getScheme(),
                configuration.getProxyConfiguration().getHost(),
                configuration.getProxyConfiguration().getPort()
            );

            // If we have proxy auth enabled
            if (configuration.getProxyConfiguration().isAuthenticationRequired()) {
                // Create credential provider
                final CredentialsStore credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                    new AuthScope(
                        configuration.getProxyConfiguration().getHost(),
                        configuration.getProxyConfiguration().getPort()
                    ),
                    new UsernamePasswordCredentials(
                        configuration.getProxyConfiguration().getUsername(),
                        configuration.getProxyConfiguration().getPassword().toCharArray()
                    )
                );

                // Preemptive load context with authentication.
                authCache.put(
                    new HttpHost(
                        configuration.getProxyConfiguration().getScheme(),
                        configuration.getProxyConfiguration().getHost(),
                        configuration.getProxyConfiguration().getPort()
                    ),
                    new BasicScheme()
                );

                // Attach Credentials provider to client builder.
                clientBuilder.setDefaultCredentialsProvider(credsProvider);
                httpClientContext.setCredentialsProvider(credsProvider);
            }

            // Attach Proxy to request config builder
            requestConfigBuilder
                .setConnectionRequestTimeout(Timeout.ofSeconds(configuration.getRequestTimeoutSecs()))
                .setConnectTimeout(Timeout.ofSeconds(configuration.getRequestTimeoutSecs()))
                .setProxy(proxyHost);

            // Configure context.
            httpClientContext.setAuthCache(authCache);
        }

        // Attach default request config
        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

        // build http client
        httpClient = clientBuilder.build();
    }

    /**
     * Based on Client Configuration, construct TrustManager instances to use.
     * @return Array of 0 or more TrustManager instances.
     */
    private TrustManager[] getTrustManagers() {
        try {
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // If client configuration is set to ignore invalid certificates
            if (configuration.isIgnoreInvalidSslCertificates()) {
                // Initialize ssl context with a TrustManager instance that just accepts everything blindly.
                // HIGHLY INSECURE / NOT RECOMMENDED!
                return new TrustManager[]{ new NoopTrustManager() };

                // If client configuration has a trust store defined.
            } else {
                // use default TrustManager instances
                trustManagerFactory.init((KeyStore) null);
                return trustManagerFactory.getTrustManagers();
            }
        } catch (final KeyStoreException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException exception) {
                logger.error("Error closing: {}", exception.getMessage(), exception);
            }
        }
        httpClient = null;
    }

    /**
     * Make a request against the Server.
     * @param request The request to submit.
     * @return The response, in UTF-8 String format.
     * @throws RestException if something goes wrong.
     */
    @Override
    public RestResponse submitRequest(final Request request) throws RestException {
        try {
            return submitRequest(request, new RestResponseHandler());
        } catch (final IOException exception) {
            throw new RestException(exception.getMessage(), exception);
        }
    }

    /**
     * For issuing an API Request.
     * @param request The Request to perform.
     * @param responseHandler How to parse the response.
     * @param <T> The return type.
     * @return The parsed API response.
     */
    private <T> T submitRequest(final Request request, final HttpClientResponseHandler<T> responseHandler) throws IOException {
        final String url = constructApiUrl(request);
        return submitRequest(url, request.getRequestParameters(), responseHandler);
    }

    /**
     * Internal POST method.
     * @param url Url to request.
     * @param getParams GET parameters to include in the request
     * @param responseHandler The response Handler to use to parse the response
     * @param <T> The type that ResponseHandler returns.
     * @return Parsed response.
     * @throws ConnectionFailedException if remote server does not accept connection.
     */
    private <T> T submitRequest(final String url, final Map<String, String> getParams, final HttpClientResponseHandler<T> responseHandler) {
        try {
            // Construct URI including our request parameters.
            final URIBuilder uriBuilder = new URIBuilder(url)
                .setCharset(StandardCharsets.UTF_8);

            // Attach submitRequest params
            for (final Map.Entry<String, String> entry : getParams.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }

            // Build Get Request
            final ClassicHttpRequest get = new HttpGet(uriBuilder.build());

            // Debug logging
            logger.info("Executing request {}", get.getRequestUri());

            // Execute and return
            return httpClient.execute(get, httpClientContext, responseHandler);
        } catch (final ClientProtocolException | SocketException | URISyntaxException | SSLHandshakeException connectionException) {
            // Signals that an error occurred while attempting to connect a
            // socket to a remote address and port.  Typically, the connection
            // was refused remotely (e.g., no process is listening on the
            // remote address/port).
            throw new ConnectionFailedException(connectionException.getMessage(), connectionException);
        } catch (final IOException exception) {
            // Typically this is a parse error.
            logger.error("Caught IOException: {}", exception.getMessage(), exception);
        }
        return null;
    }

    /**
     * Internal helper method for generating URLs w/ the appropriate API host and API version.
     * @param request Request we want to execute.
     * @return Constructed URL for the end point.
     */
    private String constructApiUrl(final Request request) {
        return configuration.getKeyServerHost() + configuration.getBasePath();
    }
}
