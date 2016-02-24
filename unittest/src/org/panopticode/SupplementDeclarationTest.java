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

import junit.framework.TestCase;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import static org.panopticode.TestHelpers.*;

public class SupplementDeclarationTest extends TestCase {
    SupplementDeclaration supplementDeclaration;
    String className;

    protected void setUp() throws Exception {
        super.setUp();

        className = "Foo";
        supplementDeclaration = new SupplementDeclaration(className);
    }

    public void testGetSupplementClass() {
        assertEquals(className, supplementDeclaration.getSupplementClass());
    }

    public void testGetErrors() {
        String errorMessage;

        supplementDeclaration = new SupplementDeclaration(className);
        assertEquals(0, supplementDeclaration.getErrors().size());

        errorMessage = "bad, very bad";
        supplementDeclaration.addError(errorMessage);
        supplementDeclaration.addError(errorMessage);
        assertEquals(1, supplementDeclaration.getErrors().size());
        assertEquals(errorMessage, supplementDeclaration.getErrors().get(0));
    }

    public void testGetMetricsDeclared() {
        MetricDeclaration metricDeclaration;

        supplementDeclaration = new SupplementDeclaration(className);
        assertEquals(0, supplementDeclaration.getMetricsDeclared().size());

        metricDeclaration = createDummyMetric().getMetricDeclaration();
        supplementDeclaration.addMetricDeclaration(metricDeclaration);
        assertEquals(1, supplementDeclaration.getMetricsDeclared().size());
        assertEquals(metricDeclaration, supplementDeclaration.getMetricsDeclared().get(0));
    }

    public void testToXML() {
        Element supplementElement;
        Element parentElement;
        MetricDeclaration metricDeclaration;
        String error;

        parentElement = createDummyElement();
        supplementDeclaration.toXML(parentElement);

        supplementElement = parentElement.element("supplement");
        assertEquals(className, supplementElement.attributeValue("class"));
        assertNull(supplementElement.attribute("errorCount"));
        assertEquals(0, supplementElement.elements("error").size());
        assertEquals(0, supplementElement.elements("metricDeclaration").size());

        metricDeclaration = new IntegerMetricDeclaration(null, "metricName");
        supplementDeclaration.addMetricDeclaration(metricDeclaration);

        error = "soemthign bad";
        supplementDeclaration.addError(error);

        parentElement = createDummyElement();
        supplementDeclaration.toXML(parentElement);

        supplementElement = parentElement.element("supplement");
        assertEquals(className, supplementElement.attributeValue("class"));
        assertEquals("1", supplementElement.element("errors").attributeValue("numErrors"));
        assertEquals(1, supplementElement.element("errors").elements("error").size());
        assertEquals(error, supplementElement.element("errors").element("error").attributeValue("message"));
        assertEquals(1, supplementElement.elements("metricDeclaration").size());
    }

    public void testFromXML() throws DocumentException {
        Element element;
        SupplementDeclaration supplementDeclaration;
        StringBuffer sb;

        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<supplement class='org.panopticode.FakeSupplement'>");
        sb.append("  <errors>");
        sb.append("    <error message='oops' />");
        sb.append("  </errors>");
        sb.append("</supplement>");

        element = DocumentHelper.parseText(sb.toString()).getRootElement();

        supplementDeclaration = SupplementDeclaration.fromXML(element);
        assertEquals("org.panopticode.FakeSupplement", supplementDeclaration.getSupplementClass());
        assertEquals(1, supplementDeclaration.getErrors().size());
        assertEquals("oops", supplementDeclaration.getErrors().get(0));
    }

}
