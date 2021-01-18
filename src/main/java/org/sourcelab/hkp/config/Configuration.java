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

package org.sourcelab.hkp.config;

import org.sourcelab.hkp.ConfigurationBuilder;

import java.util.Objects;

/**
 * Defines API Client Configuration.
 * Use {@link ConfigurationBuilder} to create instances of these.
 */
public class Configuration {
    // Proxy Configuration
    private final ProxyConfiguration proxyConfiguration;

    // Defines upstream keyserver host.
    private final String keyServerHost;

    /**
     * Optional setting to skip validating SSL certificate.
     * There should be no real valid use case for this option other then use against
     * development environments.
     */
    private final boolean ignoreInvalidSslCertificates;

    private final int requestTimeoutSecs;

    private final String basePath = "/pks/lookup";

    /**
     * Creates a new ConfigurationBuilder instance.
     * @return ConfigurationBuilder instance.
     */
    public static ConfigurationBuilder newBuilder() {
        return new ConfigurationBuilder();
    }

    /**
     * Constructor.
     * Note: Use {@link ConfigurationBuilder} to create instances instead of calling this constructor.
     *
     * @param proxyConfiguration Defines Proxy Configuration.
     * @param keyServerHost Defines KeyServer Host.
     * @param ignoreInvalidSslCertificates Should SSL certificates be validated.
     * @param requestTimeoutSecs Defines how long (in seconds) before a request times out.
     */
    public Configuration(
        final String keyServerHost,
        final ProxyConfiguration proxyConfiguration,
        final boolean ignoreInvalidSslCertificates,
        final int requestTimeoutSecs) {

        this.proxyConfiguration = Objects.requireNonNull(proxyConfiguration);
        this.keyServerHost = Objects.requireNonNull(keyServerHost);
        this.ignoreInvalidSslCertificates = ignoreInvalidSslCertificates;
        this.requestTimeoutSecs = requestTimeoutSecs;
    }

    public boolean hasProxyConfigured() {
        return proxyConfiguration.isConfigured();
    }

    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }

    public boolean isIgnoreInvalidSslCertificates() {
        return ignoreInvalidSslCertificates;
    }

    public String getKeyServerHost() {
        return keyServerHost;
    }

    public int getRequestTimeoutSecs() {
        return requestTimeoutSecs;
    }

    public String getBasePath() {
        return basePath;
    }

    @Override
    public String toString() {
        return "Configuration{"
            + "keyServerHost='" + keyServerHost + '\''
            + ", proxyConfiguration=" + proxyConfiguration
            + ", ignoreInvalidSslCertificates=" + ignoreInvalidSslCertificates
            + '}';
    }
}
