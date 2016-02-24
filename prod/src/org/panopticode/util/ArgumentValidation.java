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

public class ArgumentValidation {
    public static void failIfEmpty(String argument, String argumentName) {
        if (argument == null || argument.trim().length() == 0) {
            throw new IllegalArgumentException("Argument '" + argumentName + "' may not be null or empty.");
        }
    }

    public static void failIfNotANumber(double argument, String argumentName) {
        if ("NaN".equals(String.valueOf(argument))) {
            throw new IllegalArgumentException("Argument '" + argumentName + "' may not be NaN.");
        }
    }

    public static void failIfNotPositive(int argument, String argumentName) {
        if (argument < 1) {
            throw new IllegalArgumentException("Argument '" + argumentName + "' may not be zero or negative.");
        }
    }

    public static void failIfNull(Object argument, String argumentName) {
        if (argument == null) {
            throw new IllegalArgumentException("Argument '" + argumentName + "' may not be null.");
        }
    }
}
