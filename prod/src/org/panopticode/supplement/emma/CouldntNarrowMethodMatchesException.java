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
package org.panopticode.supplement.emma;

import org.panopticode.PanopticodeMethod;
import org.dom4j.Element;

import java.util.List;

public class CouldntNarrowMethodMatchesException extends Exception {
    private PanopticodeMethod panopticodeMethod;
    private List<Element> possibleElements;

    public CouldntNarrowMethodMatchesException(PanopticodeMethod panopticodeMethod, List<Element> possibleElements) {
        super("");

        this.panopticodeMethod = panopticodeMethod;
        this.possibleElements = possibleElements;
    }

    public String getMessage() {
        StringBuffer sb = new StringBuffer();

        sb.append("Couldn't narrow match for method '");
        sb.append(panopticodeMethod.getSignature());
        sb.append("' from [");
        boolean first = true;
        for (Element possibleMatch : possibleElements) {
            if (!first) {
                sb.append(", ");
            }

            sb.append("'");
            sb.append(possibleMatch.attributeValue("name"));
            sb.append("'");

            first = false;
        }

        sb.append("] in class '");
        sb.append(panopticodeMethod.getParentClass().getFullyQualifiedName());
        sb.append("'");

        return sb.toString();
    }
}
