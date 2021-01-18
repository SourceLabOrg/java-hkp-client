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

package org.sourcelab.hkp.request;

import java.util.Map;

/**
 * Defines a Get Key request.
 */
public class GetRequest extends AbstractRequest implements Request {

    /**
     * Constructor.
     * @param keyId Id of key to retrieve.
     */
    public GetRequest(final String keyId) {
        withKeyId(keyId);
    }

    /**
     * Define the keyId.
     * @param keyId Id of key to retrieve.
     * @return GetRequest instance.
     */
    public GetRequest withKeyId(final String keyId) {
        withParameter("search" , keyId);
        return this;
    }

    @Override
    public Map<String, String> getRequestParameters() {
        withParameter("op", "get");
        return super.getRequestParameters();
    }
}
