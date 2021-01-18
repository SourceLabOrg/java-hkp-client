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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sourcelab.hkp.config.Configuration;
import org.sourcelab.hkp.parser.GetResponseParser;
import org.sourcelab.hkp.parser.ResponseParser;
import org.sourcelab.hkp.parser.SearchIndexResponseParser;
import org.sourcelab.hkp.request.GetRequest;
import org.sourcelab.hkp.request.Request;
import org.sourcelab.hkp.request.SearchRequest;
import org.sourcelab.hkp.response.get.PgpPublicKey;
import org.sourcelab.hkp.response.search.SearchIndexResponse;
import org.sourcelab.hkp.rest.HttpClientRestClient;
import org.sourcelab.hkp.rest.RestClient;
import org.sourcelab.hkp.rest.RestResponse;
import org.sourcelab.hkp.response.ErrorResponse;
import org.sourcelab.hkp.response.Result;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * HKS Key Server Client.
 */
public class HkpClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(HkpClient.class);

    /**
     * Our API Configuration.
     */
    private final Configuration configuration;

    /**
     * Underlying RestClient to use.
     */
    private final RestClient restClient;

    /**
     * Internal State flag.
     */
    private boolean isInitialized = false;

    /**
     * Default Constructor.
     * @param configurationBuilder Configuration Builder instance.
     */
    public HkpClient(final ConfigurationBuilder configurationBuilder) {
        this(
            configurationBuilder,
            new HttpClientRestClient()
        );
    }

    /**
     * Constructor for injecting a RestClient implementation.
     * Typically only used in testing.
     * @param configurationBuilder Configuration Builder instance.
     * @param restClient RestClient implementation to use.
     */
    public HkpClient(final ConfigurationBuilder configurationBuilder, final RestClient restClient) {
        this(
            Objects.requireNonNull(configurationBuilder).build(),
            Objects.requireNonNull(restClient)
        );
    }

    /**
     * Package protected constructor for when you need to keep a reference to the actual
     * configuration instance being used.  Typically for test use cases only.
     *
     * @param configuration Configuration instance.
     * @param restClient RestClient implementation to use.
     */
    HkpClient(final Configuration configuration, final RestClient restClient) {
        this.configuration = Objects.requireNonNull(configuration);
        this.restClient = Objects.requireNonNull(restClient);
    }

    private <T> Result<T> submitRequest(final Request request, final ResponseParser<T> responseParser) {
        // Submit request
        final RestResponse restResponse = getRestClient().submitRequest(request);
        final int responseCode = restResponse.getHttpCode();
        String responseStr = restResponse.getResponseStr();

        logger.info("Response: {}", restResponse);

        // If not a success response code
        if (responseCode != 200) {
            // Parse error response
            final ErrorResponse error = new ErrorResponse(
                responseStr,
                restResponse.getHttpCode()
            );

            // Return error response.
            return Result.newError(error);
        }

        // Attempt to parse and return a Success result.
        try {
            return Result.newSuccess(
                responseParser.parseResponse(restResponse.getResponseStr())
            );
        } catch (final IOException exception) {
            throw new ParserException(exception.getMessage(), exception);
        }
    }

    /**
     * Returns the API configuration instance.
     *
     * @return Configuration.
     */
    Configuration getConfiguration() {
        return configuration;
    }

    /**
     * package protected for access in tests.
     * @return Rest Client.
     */
    RestClient getRestClient() {
        // If we haven't initialized.
        if (!isInitialized) {
            // Call Init.
            restClient.init(getConfiguration());

            // Flip state flag
            isInitialized = true;
        }

        // return our rest client.
        return restClient;
    }

    /**
     * Clean up instance, releasing any resources held internally.
     */
    public void close() {
        getRestClient().close();
    }

    /**
     * Issue a Search Request.
     * @param request Defines the search request parameters.
     * @return Response value.
     */
    public SearchIndexResponse search(final SearchRequest request) {
        return submitRequest(request, new SearchIndexResponseParser())
            .handleError((error) -> {
                throw new InvalidRequestException(error.getMessage(), error.getCode());
            });
    }

    /**
     * Get a PublicKey.
     * @param request Defines the Get Key Request.
     * @return Optional of PgpPublicKey.  Optional will be empty if no key found.
     */
    public Optional<PgpPublicKey> get(final GetRequest request) {
        return Optional.ofNullable(submitRequest(request, new GetResponseParser())
            .handleError((error) -> {
                // On 404
                if (error.getCode() == 404) {
                    // Just return empty Optional.
                    return null;
                }
                throw new InvalidRequestException(error.getMessage(), error.getCode());
            }));
    }
}