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

import junit.framework.TestCase;
import org.panopticode.PanopticodeMethod;
import static org.panopticode.TestHelpers.*;
import org.dom4j.Element;

import java.util.LinkedList;


public class CouldntNarrowMethodMatchesExceptionTest extends TestCase {
    public void testGetMessage() {
        CouldntNarrowMethodMatchesException ex;
        LinkedList<Element> possibleElements;
        PanopticodeMethod panopticodeMethod;

        panopticodeMethod = createDummyMethod();
        panopticodeMethod.setParentClass(createDummyClass());
        possibleElements = new LinkedList<Element>();
        possibleElements.add(createElement("foo"));
        possibleElements.add(createElement("bar"));

        ex = new CouldntNarrowMethodMatchesException(panopticodeMethod, possibleElements);

        assertEquals("Couldn't narrow match for method 'HelloWorld.sayHello()' from ['foo', 'bar'] in class 'HelloWorld'",
                     ex.getMessage());
    }

    private Element createElement(String name) {
        Element element;
        
        element = createDummyElement();
        element.addAttribute("name", name);

        return element;
    }
}
