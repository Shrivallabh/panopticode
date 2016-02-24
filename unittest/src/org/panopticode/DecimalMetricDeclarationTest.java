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
package org.panopticode;

import static org.panopticode.TestHelpers.*;
import org.dom4j.Element;

import junit.framework.TestCase;

public class DecimalMetricDeclarationTest extends TestCase {
    private DecimalMetricDeclaration metricDeclaration;

    protected void setUp() throws Exception {
        super.setUp();
        metricDeclaration = new DecimalMetricDeclaration(createDummySupplement(), "Foo");
    }

    public void testCreateMetric() {
        Element element;
        DecimalMetric metric;

        element = createDummyElement();
        metric = metricDeclaration.createMetric(3.1415);

        metric.addValue(element);
        assertEquals("3.1415", element.attributeValue("value"));

        assertEquals("3.1415", metric.getStringValue());
    }

    public void testGetType() {
        assertEquals("org.panopticode.DecimalMetric", metricDeclaration.getType());
    }
}
