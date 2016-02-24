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
import static org.panopticode.TestHelpers.createDummyElement;

public class MetricTest extends TestCase {
    public void testMetricDeclarationMustBeSet() {
        try {
            new DummyMetric(null);
            fail();
        } catch(IllegalArgumentException e) {
            assertEquals("Argument 'metricDeclaration' may not be null.", e.getMessage());
        }
    }

    public void testConstructorSetsMetricDeclaration() {
        Metric metric;
        MetricDeclaration metricDeclaration;

        metricDeclaration = new IntegerMetricDeclaration(null, "foo");
        metric = new DummyMetric(metricDeclaration);

        assertEquals(metricDeclaration, metric.getMetricDeclaration());
    }

    public void testGetName() {
        Metric metric;
        MetricDeclaration metricDeclaration;

        metricDeclaration = new IntegerMetricDeclaration(null, "foo");
        metric = new DummyMetric(metricDeclaration);

        assertEquals("foo", metric.getName());
    }

    public void testToXML() {
        DummyMetric metric;
        Element supplementElement;
        Element parentElement;
        MetricDeclaration metricDeclaration;
        String name;

        name = "CCN";
        metricDeclaration = new IntegerMetricDeclaration(null, name);

        parentElement = createDummyElement();
        metric = new DummyMetric(metricDeclaration);
        metric.toXML(parentElement);

        supplementElement = parentElement.element("metric");
        assertEquals(name, supplementElement.attributeValue("name"));
        assertTrue(metric.addValueCalled());
    }

    class DummyMetric extends Metric {
        private boolean addValueCalled = false;

        public DummyMetric(MetricDeclaration metricDeclaration) {
            super(metricDeclaration);
        }

        public void addValue(Element metricElement) {
            addValueCalled = true;
        }

        public String getStringValue() {
            return null;
        }

        public boolean addValueCalled() {
            return addValueCalled;
        }
    }
}
