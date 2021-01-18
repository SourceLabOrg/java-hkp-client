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

package org.sourcelab.hkp;

import org.sourcelab.hkp.config.Configuration;
import org.sourcelab.hkp.config.ProxyConfiguration;

import java.util.Objects;

/**
 * HKP Client Configuration Builder.
 * Used to construct {@link Configuration} instances.
 */
public class ConfigurationBuilder {
    // Optional Proxy Configuration
    private String proxyHost = null;
    private int proxyPort = 0;
    private String proxyScheme = "HTTP";

    // Optional Proxy Authentication.
    private String proxyUsername = null;
    private String proxyPassword = null;

    // Defines the HKS Key Server Host.
    private String keyServerHost = null;

    // Optional setting to skip validating SSL certificate.
    private boolean ignoreInvalidSslCertificates = false;

    // Optional setting to define request timeout.
    private int requestTimeoutSecs = 10;

    /**
     * Allow setting optional proxy configuration over HTTP.
     *
     * @param proxyHost Host for the proxy to use.
     * @param proxyPort Post for the proxy to use.
     * @return Configuration instance.
     */
    public ConfigurationBuilder withProxyHttp(final String proxyHost, final int proxyPort) {
        return withProxy(proxyHost, proxyPort, "HTTP");
    }

    /**
     * Allow setting optional proxy configuration over HTTPS.
     *
     * @param proxyHost Host for the proxy to use.
     * @param proxyPort Post for the proxy to use.
     * @return Configuration instance.
     */
    public ConfigurationBuilder withProxyHttps(final String proxyHost, final int proxyPort) {
        return withProxy(proxyHost, proxyPort, "HTTPS");
    }

    /**
     * Allow setting optional proxy configuration.
     *
     * @param proxyHost Host for the proxy to use.
     * @param proxyPort Post for the proxy to use.
     * @param proxyScheme Scheme to use, HTTP/HTTPS
     * @return Builder instance.
     */
    public ConfigurationBuilder withProxy(final String proxyHost, final int proxyPort, final String proxyScheme) {
        this.proxyHost = Objects.requireNonNull(proxyHost);
        this.proxyPort = proxyPort;
        this.proxyScheme = Objects.requireNonNull(proxyScheme);
        return this;
    }

    /**
     * Allow setting credentials for a proxy that requires authentication.
     *
     * @param proxyUsername Username for proxy.
     * @param proxyPassword Password for proxy.
     * @return Builder instance.
     */
    public ConfigurationBuilder useProxyAuthentication(final String proxyUsername, final String proxyPassword) {
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
        return this;
    }

    /**
     * Allows for configuring the HKS Key Server Address.
     *
     * @param host the key server host in format of "http://my.host:80" or "https://my.host".
     * @return Builder instance.
     */
    public ConfigurationBuilder withKeyServerHost(final String host) {
        this.keyServerHost = Objects.requireNonNull(host);
        return this;
    }

    /**
     * Disable all validation of SSL Certificates.
     * Disabling validations is insecure and highly discouraged!
     *
     * @return Builder instance.
     */
    public ConfigurationBuilder withIgnoreInvalidSslCertificates() {
        return withIgnoreInvalidSslCertificates(true);
    }

    /**
     * Configuration validation of SSL Certificates. Disabling validations is insecure and highly discouraged!
     *
     * @param ignoreInvalidSslCertificates Pass a value of true to disable SSL certificate validation.
     *                                     Pass a value of false to enable SSL certificate validation.
     * @return Builder instance.
     */
    public ConfigurationBuilder withIgnoreInvalidSslCertificates(final boolean ignoreInvalidSslCertificates) {
        this.ignoreInvalidSslCertificates = ignoreInvalidSslCertificates;
        return this;
    }

    public ConfigurationBuilder withRequestTimeout(int seconds) {
        this.requestTimeoutSecs = seconds;
        return this;
    }

    /**
     * Create {@link Configuration} instance using properties defined on the builder.
     * @return Configuration instance.
     */
    public Configuration build() {
        // Create optional proxy config
        final ProxyConfiguration proxyConfiguration;
        if (proxyHost == null) {
            // Creates an empty ProxyConfiguration object (not configured).
            proxyConfiguration = new ProxyConfiguration();
        } else {
            // Creates a ProxyConfiguration instance with appropriate values.
            proxyConfiguration = new ProxyConfiguration(
                proxyHost,
                proxyPort,
                proxyScheme,
                proxyUsername,
                proxyPassword
            );
        }

        // Create instance.
        return new Configuration(
            keyServerHost,
            proxyConfiguration,
            ignoreInvalidSslCertificates,
            requestTimeoutSecs
        );
    }
}
