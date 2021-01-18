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
 * Defines a Search Request.
 */
public class SearchRequest extends AbstractRequest implements Request {

    /**
     * Constructor.
     * @param search Defines the value to search.
     *               If searching by KeyId pass the keyId prefixed with "0x"
     */
    public SearchRequest(final String search) {
        withSearch(search);
    }

    /**
     * Define the search term.
     * If searching by KeyId, prefix the value with "0x"
     * @param search Defines the value to search.
     * @return SearchRequest instance.
     */
    public SearchRequest withSearch(final String search) {
        withParameter("search", search);
        return this;
    }

    /**
     * Perform an exact match on the search.
     * @param exactMatch True for exact match, false otherwise.
     * @return SearchRequest instance.
     */
    public SearchRequest withExactMatch(final boolean exactMatch) {
        if (exactMatch) {
            return withExactMatch();
        }
        return withOutExactMatch();
    }

    /**
     * Enable exact matching.
     * @return SearchRequest instance.
     */
    public SearchRequest withExactMatch() {
        return withExactMatch(true);
    }

    /**
     * Disable exact matching.
     * @return SearchRequest instance.
     */
    public SearchRequest withOutExactMatch() {
        return withExactMatch(false);
    }

    @Override
    public Map<String, String> getRequestParameters() {
        withParameter("op", "index");
        return super.getRequestParameters();
    }
}
