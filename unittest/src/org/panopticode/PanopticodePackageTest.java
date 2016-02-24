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

import static org.panopticode.TestHelpers.*;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.util.LinkedList;
import java.util.List;

public class PanopticodePackageTest extends TestCase {
    private PanopticodeProject panopticodeProject;
    private String name;

    protected void setUp() throws Exception {
        super.setUp();

        panopticodeProject = createDummyProject();
        name = "";
    }

    public void testLevel() {
        PanopticodePackage panopticodePackage;

        panopticodePackage = createDummyPackage();

        assertEquals(Level.PACKAGE, panopticodePackage.getLevel());
        assertTrue(panopticodePackage.isLevel(Level.PACKAGE));
    }

    public void testConstructorSetPropertiesToMatchArgumentsPassed() {
        PanopticodePackage panopticodePackage;

        panopticodePackage = new PanopticodePackage(panopticodeProject, name);
        assertEquals(name, panopticodePackage.getName());
        assertSame(panopticodeProject, panopticodePackage.getParentProject());
        assertSame(panopticodeProject, panopticodePackage.getParent());
    }

    public void testGetMetricByName() {
        PanopticodePackage panopticodePackage;
        Metric metric;

        metric = createDummyMetric();

        panopticodePackage = new PanopticodePackage(panopticodeProject, name);
        assertNull(panopticodePackage.getMetricByName(metric.getName()));

        panopticodePackage.addMetric(metric);
        assertSame(metric, panopticodePackage.getMetricByName(metric.getName()));
    }

    public void testAddingSameMetricTwiceOnlyStoresOnce() {
        PanopticodePackage panopticodePackage;

        panopticodePackage = createDummyPackage();
        assertEquals(0, panopticodePackage.getMetrics().size());

        panopticodePackage.addMetric(createDummyMetric());
        assertEquals(1, panopticodePackage.getMetrics().size());

        panopticodePackage.addMetric(createDummyMetric());
        assertEquals(1, panopticodePackage.getMetrics().size());
    }

    public void testFileAddingAndRetrieval() {
        PanopticodeFile anotherDummyFile;
        PanopticodeFile createdFile;
        PanopticodeFile dummyFile;
        PanopticodePackage panopticodePackage;
        String anotherCanonicalPath;
        String canonicalPath;

        panopticodePackage = new PanopticodePackage(panopticodeProject, name);

        assertEquals(0, panopticodePackage.getFiles().size());
        assertEquals(0, panopticodePackage.getChildren().size());

        dummyFile = createDummyFile();
        canonicalPath = dummyFile.getPath();

        createdFile = panopticodePackage.createAndAddFile(canonicalPath, "");

        assertEquals(1, panopticodePackage.getFiles().size());
        assertEquals(1, panopticodePackage.getChildren().size());
        assertEquals(dummyFile.getPath(), createdFile.getPath());

        assertSame(createdFile, panopticodePackage.createAndAddFile(canonicalPath, ""));

        assertEquals(1, panopticodePackage.getFiles().size());
        assertEquals(1, panopticodePackage.getChildren().size());

        anotherCanonicalPath = "Foo" + dummyFile.getPath();
        
        anotherDummyFile = panopticodePackage.createAndAddFile(anotherCanonicalPath, "");

        assertEquals(2, panopticodePackage.getFiles().size());
        assertEquals(2, panopticodePackage.getChildren().size());
        assertEquals("Foo" + dummyFile.getPath(),  anotherDummyFile.getPath());
    }

    public void testToXML() {
        Element fileElement;
        Element packageElement;
        Element parentElement;
        PanopticodePackage panopticodePackage;
        String path;


        parentElement = createDummyElement();
        panopticodePackage = new PanopticodePackage(panopticodeProject, name);
        panopticodePackage.addMetric(createDummyMetric());
        panopticodePackage.toXML(parentElement);

        packageElement = parentElement.element("package");
        assertEquals(name, packageElement.attributeValue("name"));
        assertEquals(0, packageElement.elements("file").size());

        path = "foo";
        panopticodePackage.createAndAddFile(path, "");
        parentElement = createDummyElement();
        panopticodePackage.toXML(parentElement);

        packageElement = parentElement.element("package");
        assertEquals(name, packageElement.attributeValue("name"));
        assertEquals(1, packageElement.elements("file").size());
        fileElement = (Element) packageElement.elements("file").get(0);
        assertEquals(path, fileElement.attributeValue("projectPath"));
    }

    public void testFromXML() throws DocumentException {
        Element element;
        PanopticodePackage panopticodePackage;
        StringBuffer sb;

        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<package name='org.panopticode'>");
        sb.append("  <file name='Fubar.java' projectPath='src/Fubar.java'>");
        sb.append("    <class name='Fubar'><filePosition line='1' column='2' />");
        sb.append("      <method name='bar'><filePosition line='5' column='3' /></method>");
        sb.append("    </class>");
        sb.append("  </file>");
        sb.append("</package>");

        element = DocumentHelper.parseText(sb.toString()).getRootElement();

        panopticodePackage = PanopticodePackage.fromXML(element, createDummyProject());
        assertEquals("org.panopticode", panopticodePackage.getName());
        assertEquals(1, panopticodePackage.getFiles().size());
        assertEquals("Fubar.java", panopticodePackage.getFiles().get(0).getName());
        assertEquals(1, panopticodePackage.getFiles().get(0).getClasses().size());
        assertEquals("Fubar", panopticodePackage.getFiles().get(0).getClasses().get(0).getName());
        assertEquals(1, panopticodePackage.getFiles().get(0).getClasses().get(0).getMethods().size());
        assertEquals("bar", panopticodePackage.getFiles().get(0).getClasses().get(0).getMethods().get(0).getName());
    }

    public void testIsDefaultPackage() {
        PanopticodePackage panopticodePackage;

        panopticodePackage = new PanopticodePackage(panopticodeProject, "");
        assertTrue(panopticodePackage.isDefaultPackage());

        panopticodePackage = new PanopticodePackage(panopticodeProject, "x");
        assertFalse(panopticodePackage.isDefaultPackage());
    }

    public void testGetMethods() {
        PanopticodePackage panopticodePackage;
        PanopticodeFile panopticodeFile;
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod1;
        PanopticodeMethod panopticodeMethod2;

        panopticodePackage = createDummyPackage();
        panopticodeFile = panopticodePackage.createAndAddFile("foo", "foo");
        panopticodeClass = panopticodeFile.createAndAddClass("Foo", 1, 1);
        panopticodeMethod1 = panopticodeClass.createAndAddMethod("one", new LinkedList<PanopticodeArgument>(), 2, 2);
        panopticodeMethod2 = panopticodeClass.createAndAddMethod("two", new LinkedList<PanopticodeArgument>(), 3, 3);

        List<PanopticodeMethod> methods = panopticodePackage.getMethods();
        assertEquals(2, methods.size());
        for (PanopticodeMethod panopticodeMethod : methods) {
            if("one".equals(panopticodeMethod.getName())) {
                assertSame(panopticodeMethod1, panopticodeMethod);
            } else if ("two".equals(panopticodeMethod.getName())) {
                assertSame(panopticodeMethod2, panopticodeMethod);
            } else {
                fail();
            }
        }
    }

    public void testGetClasses() {
        PanopticodePackage panopticodePackage;
        PanopticodeFile panopticodeFile;
        PanopticodeClass panopticodeClass1;
        PanopticodeClass panopticodeClass2;

        panopticodePackage = createDummyPackage();
        panopticodeFile = panopticodePackage.createAndAddFile("foo", "One");
        panopticodeClass1 = panopticodeFile.createAndAddClass("One", 1, 1);
        panopticodeFile = panopticodePackage.createAndAddFile("foo", "Two");
        panopticodeClass2 = panopticodeFile.createAndAddClass("Two", 1, 1);

        List<PanopticodeClass> classes = panopticodePackage.getClasses();
        assertEquals(2, classes.size());
        for (PanopticodeClass panopticodeClass : classes) {
            if("One".equals(panopticodeClass.getName())) {
                assertSame(panopticodeClass1, panopticodeClass);
            } else if ("Two".equals(panopticodeClass.getName())) {
                assertSame(panopticodeClass2, panopticodeClass);
            } else {
                fail();
            }
        }
    }
}
