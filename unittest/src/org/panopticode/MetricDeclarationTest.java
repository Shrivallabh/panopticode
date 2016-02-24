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
import org.panopticode.supplement.javancss.JavaNCSSSupplement;
import org.panopticode.supplement.emma.EmmaSupplement;
import org.panopticode.supplement.jdepend.JDependSupplement;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import junit.framework.TestCase;

public class MetricDeclarationTest extends TestCase {
    MetricDeclaration metricDeclaration;
    Supplement supplement;
    String name;

    protected void setUp() throws Exception {
        super.setUp();

        supplement = createDummySupplement();
        name = "LCOM";

        metricDeclaration = new IntegerMetricDeclaration(supplement, name);
    }

    public void testMetricLevelSetting() {
        assertFalse(metricDeclaration.isLevel(Level.PROJECT));
        assertFalse(metricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(metricDeclaration.isLevel(Level.FILE));
        assertFalse(metricDeclaration.isLevel(Level.CLASS));
        assertFalse(metricDeclaration.isLevel(Level.METHOD));

        metricDeclaration.addLevel(Level.PROJECT);
        assertTrue(metricDeclaration.isLevel(Level.PROJECT));
        assertFalse(metricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(metricDeclaration.isLevel(Level.FILE));
        assertFalse(metricDeclaration.isLevel(Level.CLASS));
        assertFalse(metricDeclaration.isLevel(Level.METHOD));

        metricDeclaration.addLevel(Level.METHOD);
        assertTrue(metricDeclaration.isLevel(Level.PROJECT));
        assertFalse(metricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(metricDeclaration.isLevel(Level.FILE));
        assertFalse(metricDeclaration.isLevel(Level.CLASS));
        assertTrue(metricDeclaration.isLevel(Level.METHOD));
    }

    public void testConstructorSetsSourceSupplementAndName() {
        assertSame(supplement, metricDeclaration.getSource());
        assertEquals(name, metricDeclaration.getName());
    }

    public void testDescription() {
        String description;

        assertNull(metricDeclaration.getDescription());

        description = "Lack of Cohesion of Methods";
        metricDeclaration.setDescription(description);
        assertEquals(description, metricDeclaration.getDescription());
    }

    public void testToXML() {
        Element metricDeclarationElement;
        Element parentElement;

        parentElement = createDummyElement();
        metricDeclaration.toXML(parentElement);

        metricDeclarationElement = parentElement.element("metricDeclaration");
        assertEquals(name, metricDeclarationElement.attributeValue("name"));
        assertEquals(metricDeclaration.getType(), metricDeclarationElement.attributeValue("type"));
        assertNull(metricDeclarationElement.attribute("description"));
        assertNull(metricDeclarationElement.attribute("project"));
        assertNull(metricDeclarationElement.attribute("package"));
        assertNull(metricDeclarationElement.attribute("file"));
        assertNull(metricDeclarationElement.attribute("class"));
        assertNull(metricDeclarationElement.attribute("method"));

        metricDeclaration.setDescription("Blah blah blah");
        metricDeclaration.addLevel(Level.PROJECT);
        metricDeclaration.addLevel(Level.PACKAGE);
        metricDeclaration.addLevel(Level.FILE);
        metricDeclaration.addLevel(Level.CLASS);
        metricDeclaration.addLevel(Level.METHOD);

        parentElement = createDummyElement();
        metricDeclaration.toXML(parentElement);

        metricDeclarationElement = parentElement.element("metricDeclaration");
        assertEquals(name, metricDeclarationElement.attributeValue("name"));
        assertEquals(metricDeclaration.getType(), metricDeclarationElement.attributeValue("type"));
        assertEquals(metricDeclaration.getDescription(), metricDeclarationElement.attributeValue("description"));
        assertEquals("true", metricDeclarationElement.attributeValue("project"));
        assertEquals("true", metricDeclarationElement.attributeValue("package"));
        assertEquals("true", metricDeclarationElement.attributeValue("file"));
        assertEquals("true", metricDeclarationElement.attributeValue("class"));
        assertEquals("true", metricDeclarationElement.attributeValue("method"));
    }

    public void testLoadAllMetricsFromXML() throws Exception {
        Element element;
        PanopticodeMethod panopticodeMethod;
        PanopticodePackage panopticodePackage;
        PanopticodeProject panopticodeProject;
        StringBuffer sb;

        // test with a method who has ratio and integer metrics
        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<method name='bar' constructor='true'>");
        sb.append("  <position line='5' column='3' />");
        sb.append("  <metric name='Line Coverage' numerator='3.0' denominator='3.0'/>");
        sb.append("  <metric name='CCN' value='1' />");
        sb.append("  <metric name='Pi' value='3.14159' />");
        sb.append("</method>");

        element = DocumentHelper.parseText(sb.toString()).getRootElement();

        panopticodeMethod = createDummyMethod();
        panopticodeProject = createDummyProject();
        panopticodeProject.addSupplementDeclaration(new JavaNCSSSupplement().getDeclaration());
        panopticodeProject.addSupplementDeclaration(new EmmaSupplement().getDeclaration());

        MetricDeclaration.loadAllMetricsFromXML(element, panopticodeMethod, panopticodeProject);

        assertEquals("1", panopticodeMethod.getMetricByName("CCN").getStringValue());
        assertEquals("100.0% (3.0/3.0)", panopticodeMethod.getMetricByName("Line Coverage").getStringValue());
        assertNull(panopticodeMethod.getMetricByName("Pi"));

        // test with a package who has decimal metrics
        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<package name='com.bar'>");
        sb.append("  <metric name='Distance (D)' value='0.51' />");
        sb.append("</package>");

        element = DocumentHelper.parseText(sb.toString()).getRootElement();

        panopticodePackage = createDummyPackage();
        panopticodeProject = createDummyProject();
        panopticodeProject.addSupplementDeclaration(new JDependSupplement().getDeclaration());

        MetricDeclaration.loadAllMetricsFromXML(element, panopticodePackage, panopticodeProject);

        assertEquals("0.51", panopticodePackage.getMetricByName("Distance (D)").getStringValue());
    }
}
