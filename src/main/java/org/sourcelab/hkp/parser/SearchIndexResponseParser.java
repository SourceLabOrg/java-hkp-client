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

package org.sourcelab.hkp.parser;

import org.sourcelab.hkp.response.search.Entry;
import org.sourcelab.hkp.response.search.Pub;
import org.sourcelab.hkp.response.search.SearchIndexResponse;
import org.sourcelab.hkp.response.search.Uid;

import java.io.IOException;
import java.util.List;

/**
 * Parses SearchIndex Responses.
 */
public class SearchIndexResponseParser implements ResponseParser<SearchIndexResponse> {

    @Override
    public SearchIndexResponse parseResponse(final String responseStr) throws IOException {
        // avoid NPE
        if (responseStr == null) {
            throw new IOException("NULL Response from server.");
        }

        final String[] lines = responseStr.split("\n");
        if (lines.length < 1) {
            throw new IOException("Invalid Format returned from server: " + responseStr);
        }

        // Parse header into builder
        final SearchIndexResponse.Builder builder = parseHeader(lines[0]);

        for (int index = 1; index < lines.length; index = index + 2) {
            if (lines[index].trim().isEmpty() || lines[index].equals("\r")) {
                break;
            }
            builder.withEntry(
                new Entry(parsePub(lines[index]), parseUid(lines[index + 1]))
            );
        }

        return builder.build();
    }

    private Uid parseUid(final String line) throws IOException {
        final List<String> fields = FieldParser.parseLine(line);
        if (fields.size() != 5) {
            throw new IOException("Unable to uid line: \"" + line + "\"");
        }

        // uid:<escaped uid string>:<creationdate>:<expirationdate>:<flags>
        return Uid.newBuilder()
            .withUid(fields.get(1))
            .withCreationDate(fields.get(2))
            .withExpirationDate(fields.get(3))
            .withFlags(fields.get(4))
            .build();
    }

    private Pub parsePub(final String line) throws IOException {
        final List<String> fields = FieldParser.parseLine(line);
        if (fields.size() != 7) {
            throw new IOException("Unable to pub line: \"" + line + "\"");
        }

        // pub:<keyid>:<algo>:<keylen>:<creationdate>:<expirationdate>:<flags>
        return Pub.newBuilder()
            .withKeyId(fields.get(1))
            .withAlgo(Integer.parseInt(fields.get(2)))
            .withKeyLen(Integer.parseInt(fields.get(3)))
            .withCreationDate(fields.get(4))
            .withExpirationDate(fields.get(5))
            .withFlags(fields.get(6))
            .build();
    }

    private SearchIndexResponse.Builder parseHeader(final String line) throws IOException {
        final List<String> fields = FieldParser.parseLine(line);
        if (fields.size() != 3) {
            throw new IOException("Unable to header line: \"" + line + "\"");
        }

        return SearchIndexResponse.newBuilder()
            .withVersion(Integer.parseInt(fields.get(1)))
            .withCount(Integer.parseInt(fields.get(2)));
    }
}
