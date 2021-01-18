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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldParserTest {
    @Test
    void test_simple() throws IOException {
        final String input = "pub:keyid:algo:keylen:creationdate:expirationdate:flags";
        final List<String> fields = FieldParser.parseLine(input);

        // Verify
        assertEquals(7, fields.size(), "invalid length");
        assertEquals("pub", fields.get(0));
        assertEquals("keyid", fields.get(1));
        assertEquals("algo", fields.get(2));
        assertEquals("keylen", fields.get(3));
        assertEquals("creationdate", fields.get(4));
        assertEquals("expirationdate", fields.get(5));
        assertEquals("flags", fields.get(6));
    }

    @Test
    void test_withBlanksInMiddle() throws IOException {
        final String input = "pub:keyid:algo:keylen:::";
        final List<String> fields = FieldParser.parseLine(input);

        // Verify
        assertEquals(7, fields.size(), "invalid length");
        assertEquals("pub", fields.get(0));
        assertEquals("keyid", fields.get(1));
        assertEquals("algo", fields.get(2));
        assertEquals("keylen", fields.get(3));
        assertEquals("", fields.get(4));
        assertEquals("", fields.get(5));
        assertEquals("", fields.get(6));
    }

    @Test
    void test_withBlanksAtEnd() throws IOException {
        final String input = "pub:keyid::::expirationdate:flags";
        final List<String> fields = FieldParser.parseLine(input);

        // Verify
        assertEquals(7, fields.size(), "invalid length");
        assertEquals("pub", fields.get(0));
        assertEquals("keyid", fields.get(1));
        assertEquals("", fields.get(2));
        assertEquals("", fields.get(3));
        assertEquals("", fields.get(4));
        assertEquals("expirationdate", fields.get(5));
        assertEquals("flags", fields.get(6));
    }
}