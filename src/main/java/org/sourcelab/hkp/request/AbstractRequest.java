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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Shared Abstract Request.
 */
public abstract class AbstractRequest implements Request {
    private final Map<String, String> params = new HashMap<>();

    /**
     * Add a parameter to the request.
     * @param name Name of the parameter.
     * @param value value of the parameter.
     */
    void withParameter(final String name, final String value) {
        Objects.requireNonNull(name, "Parameter name may not be null.");
        if (value == null) {
            params.remove(name);
        } else {
            params.put(name, value);
        }
    }

    /**
     * Get all request parameters associated with the request.
     * @return Map of request parameters.
     */
    @Override
    public Map<String, String> getRequestParameters() {
        withParameter("options", "mr");
        return Collections.unmodifiableMap(params);
    }
}
