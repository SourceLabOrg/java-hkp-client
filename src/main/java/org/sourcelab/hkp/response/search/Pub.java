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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Represents the Pub line of a response.
 * pub:keyid:algo:keylen:creationdate:expirationdate:flags
 */
public class Pub {

    private final String keyId;
    private final int algo;
    private final int keyLen;
    private final LocalDateTime creationDate;
    private final LocalDateTime expirationDate;
    private final String flags;

    /**
     * Constructor. See Builder instance.
     * @param keyId KeyId.
     * @param algo Algorithm Id.
     * @param keyLen Key length.
     * @param creationDate Creation Date.
     * @param expirationDate Expiration Date.
     * @param flags Flags.
     */
    public Pub(final String keyId, final int algo, final int keyLen, final LocalDateTime creationDate, final LocalDateTime expirationDate, final String flags) {
        this.keyId = keyId;
        this.algo = algo;
        this.keyLen = keyLen;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.flags = flags;
    }

    public String getKeyId() {
        return keyId;
    }

    public int getAlgo() {
        return algo;
    }

    public int getKeyLen() {
        return keyLen;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public String getFlags() {
        return flags;
    }

    /**
     * Create new Builder instance.
     * @return new Builder instance.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Pub{"
            + "keyId='" + keyId + '\''
            + ", algo=" + algo
            + ", keyLen=" + keyLen
            + ", creationDate='" + creationDate + '\''
            + ", expirationDate='" + expirationDate + '\''
            + ", flags='" + flags + '\''
            + '}';
    }

    /**
     * Builder for Pub.
     *
     * pub:keyid:algo:keylen:creationdate:expirationdate:flags
     */
    public static final class Builder {

        private String keyId = "";
        private int algo = 0;
        private int keyLen = 0;
        private LocalDateTime creationDate = null;
        private LocalDateTime expirationDate = null;
        private String flags = "";

        private Builder() {
        }

        public Builder withKeyId(final String keyId) {
            this.keyId = Objects.requireNonNull(keyId);
            return this;
        }

        public Builder withAlgo(final int algo) {
            this.algo = algo;
            return this;
        }

        public Builder withKeyLen(final int keyLen) {
            this.keyLen = keyLen;
            return this;
        }

        /**
         * Set the creation date value.
         * @param creationDate Represents the creation date in seconds past Unix Epoch
         * @return Builder instance.
         */
        public Builder withCreationDate(final long creationDate) {
            this.creationDate = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(creationDate), ZoneId.systemDefault()
            );
            return this;
        }

        /**
         * Set the creation date value.
         * @param creationDate Represents the creation date in seconds past Unix Epoch
         * @return Builder instance.
         */
        public Builder withCreationDate(final String creationDate) {
            try {
                return withCreationDate(Long.parseLong(creationDate));
            } catch (final NumberFormatException exception) {
                this.creationDate = null;
            }
            return this;
        }

        /**
         * Set the expiration date value.
         * @param expirationDate Represents the expiration date in seconds past Unix Epoch
         * @return Builder instance.
         */
        public Builder withExpirationDate(final long expirationDate) {
            this.expirationDate = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(expirationDate), ZoneId.systemDefault()
            );
            return this;
        }

        /**
         * Set the expiration date value.
         * @param expirationDate Represents the expiration date in seconds past Unix Epoch
         * @return Builder instance.
         */
        public Builder withExpirationDate(final String expirationDate) {
            try {
                return withExpirationDate(Long.parseLong(expirationDate));
            } catch (final NumberFormatException exception) {
                this.expirationDate = null;
            }
            return this;
        }

        public Builder withFlags(final String flags) {
            this.flags = flags;
            return this;
        }

        /**
         * Creates new Pub instance.
         * @return new Pub instance.
         */
        public Pub build() {
            return new Pub(keyId, algo, keyLen, creationDate, expirationDate, flags);
        }
    }
}
