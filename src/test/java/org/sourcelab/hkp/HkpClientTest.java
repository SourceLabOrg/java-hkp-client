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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sourcelab.hkp.config.Configuration;
import org.sourcelab.hkp.request.GetRequest;
import org.sourcelab.hkp.request.SearchRequest;
import org.sourcelab.hkp.response.get.PgpPublicKey;
import org.sourcelab.hkp.response.search.SearchIndexResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
class HkpClientTest {
    private static final Logger logger = LoggerFactory.getLogger(HkpClientTest.class);

    /**
     * Instance under test.
     */
    private static HkpClient client;

    @BeforeAll
    static void setup() {
        client = new HkpClient(Configuration.newBuilder()
            //.withKeyServerHost("http://pool.sks-keyservers.net")
            .withKeyServerHost("https://hkps.pool.sks-keyservers.net")
            .withIgnoreInvalidSslCertificates()
        );
    }

    @AfterAll
    static void cleanup() {
        client.close();
    }

    @Test
    void test() {
        final SearchIndexResponse result = client.search(new SearchRequest("stephen.powis@gmail.com"));
        logger.info("Result: {}", result);
        assertNotNull(result);

        assertEquals(3, result.getCount());
        assertEquals(1, result.getVersion());
        assertEquals(3, result.getEntries().size());
    }

    @Test
    void searchByKey() {
        final SearchIndexResponse result = client.search(new SearchRequest("0x92E73960FC59970DFB12F0146D712A2D27F74CE9"));
        logger.info("Result: {}", result);
    }

    @Test
    void getByKey() {
        final Optional<PgpPublicKey> result = client.get(new GetRequest("0x92E73960FC59970DFB12F0146D712A2D27F74CE9"));
        logger.info("Result: {}", result.get());
        assertTrue(result.isPresent());

        final PgpPublicKey key = result.get();
        assertNotNull(key.getPublicKey());
    }

    @Test
    void getByEmail() {
        final Optional<PgpPublicKey> result = client.get(new GetRequest("stephen.powis@gmail.com"));
        logger.info("Result: {}", result.get());
        assertTrue(result.isPresent());
    }

    @Test
    void getByKey_invalidKey_500Error() {
        try {
            client.get(new GetRequest("0xZEROC00l"));
        } catch (final InvalidRequestException exception) {
            logger.info("Result: {}", exception);
            assertEquals(500, exception.getErrorCode());
            assertNotNull(exception.getMessage());
        }
    }

    @Test
    void getByKey_invalidKey_404Error() {
        final Optional<PgpPublicKey> result = client.get(new GetRequest("0x92E73960FC59970DFB12F0146D712A2D27F74CE4"));
        assertFalse(result.isPresent());
    }
}