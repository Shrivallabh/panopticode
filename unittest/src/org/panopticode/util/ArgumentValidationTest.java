/*
 * Copyright (c) 2006-2007 Julias R. Shaw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.panopticode.util;

import junit.framework.TestCase;

public class ArgumentValidationTest extends TestCase {
    public void testFailIfEmpty() {
        assertFailIfEmptyThrowsIllegalArgumentException(null);
        assertFailIfEmptyThrowsIllegalArgumentException("");
        assertFailIfEmptyThrowsIllegalArgumentException(" ");
        assertFailIfEmptyThrowsIllegalArgumentException("\t");

        ArgumentValidation.failIfEmpty("a", "foo");
    }

    public void testFailIfNull() {
        try {
            ArgumentValidation.failIfNull(null, "foo");
            fail();
        } catch(IllegalArgumentException e) {
            assertEquals("Argument 'foo' may not be null.", e.getMessage());
        }

        ArgumentValidation.failIfNull("", "foo");
    }

    public void testFailIfNotPositive() {
        try {
            ArgumentValidation.failIfNotPositive(0, "foo");
            fail();
        } catch(IllegalArgumentException e) {
            assertEquals("Argument 'foo' may not be zero or negative.", e.getMessage());
        }

        ArgumentValidation.failIfNotPositive(1, "foo");
    }
    
    private void assertFailIfEmptyThrowsIllegalArgumentException(String argument) {
        try {
            ArgumentValidation.failIfEmpty(argument, "foo");
            fail();
        } catch(IllegalArgumentException e) {
            assertEquals("Argument 'foo' may not be null or empty.", e.getMessage());
        }
    }
}
