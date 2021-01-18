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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * For parsing HKS Responses.
 */
public class FieldParser {
    /**
     * Parses a line from a HKS response, where the fields are separated by : characters.
     * @param line The line to parse.
     * @return List of all the field values.
     * @throws IOException on parse error.
     */
    public static List<String> parseLine(final String line) throws IOException {
        if (line == null) {
            throw new IOException("Unable to parse null line!");
        }
        final List<String> fields = new ArrayList<>();

        int index = 0;
        do {
            final int nextSeparator = line.indexOf(":", index);
            if (nextSeparator == -1) {
                fields.add(line.substring(index));
                index = -1;
            } else {
                fields.add(line.substring(index, nextSeparator));
                index = nextSeparator + 1;
            }
        }
        while (index > 0);

        return Collections.unmodifiableList(fields);
    }
}
