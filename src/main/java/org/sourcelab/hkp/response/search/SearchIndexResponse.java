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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the response from an index search request.
 */
public class SearchIndexResponse {
    private final int version;
    private final int count;
    private final List<Entry> entries;

    /**
     * Constructor.
     * @param version Version value.
     * @param count How many records were returned.
     * @param entries The records returned.
     */
    public SearchIndexResponse(final int version, final int count, final List<Entry> entries) {
        Objects.requireNonNull(entries);

        this.version = version;
        this.count = count;
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public int getVersion() {
        return version;
    }

    public int getCount() {
        return count;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * New Builder instance.
     * @return New Builder instance.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "SearchIndexResponse{"
            + "version=" + version
            + ", count=" + count
            + ", entries=" + entries
            + '}';
    }

    /**
     * Builder instance.
     */
    public static final class Builder {
        private int version;
        private int count;
        private final List<Entry> entries = new ArrayList<>();

        private Builder() {
        }

        public Builder withVersion(final int version) {
            this.version = version;
            return this;
        }

        public Builder withCount(final int count) {
            this.count = count;
            return this;
        }

        /**
         * Add a list of entries to the builder.
         * @param entries List of entries to append to builder.
         * @return Builder instance.
         */
        public Builder withEntries(final List<Entry> entries) {
            Objects.requireNonNull(entries, "Entries parameter may not be null.");
            if (entries != null) {
                this.entries.addAll(entries);
            }
            return this;
        }

        /**
         * Adds an entry to the builder.
         * @param entry Entry to append to builder.
         * @return Builder instance.
         */
        public Builder withEntry(final Entry entry) {
            if (entry != null) {
                this.entries.add(entry);
            }
            return this;
        }

        /**
         * Create SearchIndexResponse instance from builder.
         * @return new SearchIndexResponse instance.
         */
        public SearchIndexResponse build() {
            return new SearchIndexResponse(version, count, entries);
        }
    }
}
