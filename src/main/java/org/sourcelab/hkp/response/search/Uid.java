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
 * Represents the UID row from an index search.
 *
 * uid:escaped uid string:creationdate:expirationdate:flags
 */
public class Uid {
    private final String uid;
    private final LocalDateTime creationDate;
    private final LocalDateTime expirationDate;
    private final String flags;

    /**
     * Constructor.  See Buidler instance.
     * @param uid UID Value.
     * @param creationDate Creation Date.
     * @param expirationDate Expiration date.
     * @param flags Flags.
     */
    public Uid(final String uid, final LocalDateTime creationDate, final LocalDateTime expirationDate, final String flags) {
        this.uid = uid;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.flags = flags;
    }

    public String getUid() {
        return uid;
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
     * Creates a new Builder instance.
     * @return new Builder instance.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Uid{"
            + "uid='" + uid + '\''
            + ", creationDate='" + creationDate + '\''
            + ", expirationDate='" + expirationDate + '\''
            + ", flags='" + flags + '\''
            + '}';
    }

    /**
     * Builder for Uid object.
     * uid:escaped uid string:creationdate:expirationdate:flags
     */
    public static final class Builder {
        private String uid = "";
        private LocalDateTime creationDate = null;
        private LocalDateTime expirationDate = null;
        private String flags = "";

        private Builder() {
        }

        /**
         * Defines the Uid.
         * @param uid uid value.
         * @return Builder instance.
         */
        public Builder withUid(final String uid) {
            this.uid = Objects.requireNonNull(uid);
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

        /**
         * Set the flags parameter.
         * @param flags flags.
         * @return Builder instance.
         */
        public Builder withFlags(final String flags) {
            this.flags = Objects.requireNonNull(flags);
            return this;
        }

        /**
         * Creates new Uid instance.
         * @return Uid instance.
         */
        public Uid build() {
            return new Uid(uid, creationDate, expirationDate, flags);
        }
    }
}
