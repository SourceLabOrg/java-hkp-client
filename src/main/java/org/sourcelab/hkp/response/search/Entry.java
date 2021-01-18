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

package org.sourcelab.hkp.response.search;

import java.util.Objects;

/**
 * Defines an Entry in the Search Response.
 */
public class Entry {
    private final Pub pub;
    private final Uid uid;

    /**
     * Contructor.
     * @param pub defines the pub row.
     * @param uid defines the Uid row.
     */
    public Entry(final Pub pub, final Uid uid) {
        this.pub = Objects.requireNonNull(pub);
        this.uid = Objects.requireNonNull(uid);
    }

    public Pub getPub() {
        return pub;
    }

    public Uid getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "Entry{"
            + "pub=" + pub
            + ", uid=" + uid
            + '}';
    }
}
