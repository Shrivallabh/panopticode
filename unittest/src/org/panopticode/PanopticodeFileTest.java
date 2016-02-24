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

import java.util.LinkedList;
import java.util.List;

public class PanopticodeFileTest extends TestCase {
    private PanopticodePackage panopticodePackage;
    private String path;
    private String name;

    protected void setUp() throws Exception {
        super.setUp();

        panopticodePackage = createDummyPackage();
        path = "";
        name = "";
    }

    public void testAddingSameMetricTwiceOnlyStoresOnce() {
        PanopticodeFile panopticodeFile;

        panopticodeFile = createDummyFile();
        assertEquals(0, panopticodeFile.getMetrics().size());

        panopticodeFile.addMetric(createDummyMetric());
        assertEquals(1, panopticodeFile.getMetrics().size());

        panopticodeFile.addMetric(createDummyMetric());
        assertEquals(1, panopticodeFile.getMetrics().size());
    }

    public void testConstructorSetPropertiesToMatchArgumentsPassed() {
        PanopticodeFile panopticodeFile;

        panopticodeFile = new PanopticodeFile(panopticodePackage, path, name);
        assertEquals(panopticodePackage, panopticodeFile.getParentPackage());
        assertEquals(panopticodePackage, panopticodeFile.getParent());
        assertEquals(path, panopticodeFile.getPath());
        assertEquals(name, panopticodeFile.getName());
    }

    public void testConstructorPullsBasePathOffOfPath() {
        PanopticodeFile panopticodeFile;
        String expectedFilePath;
        PanopticodeProject panopticodeProject;
        
        path = "/foo/something/src/HelloWorld.java";
        expectedFilePath = "src/HelloWorld.java";
        panopticodeProject = new PanopticodeProject("Hello World", "/foo/something");
        panopticodePackage = new PanopticodePackage(panopticodeProject, path);

        panopticodeFile = new PanopticodeFile(panopticodePackage, path, name);
        assertEquals(panopticodePackage, panopticodeFile.getParentPackage());
        assertEquals(panopticodePackage, panopticodeFile.getParent());
        assertEquals(expectedFilePath, panopticodeFile.getPath());
    }

    public void testGetMetricByName() {
        PanopticodeFile panopticodeFile;
        Metric metric;

        metric = createDummyMetric();

        panopticodeFile = new PanopticodeFile(panopticodePackage, path, name);
        assertNull(panopticodeFile.getMetricByName(metric.getName()));

        panopticodeFile.addMetric(metric);
        assertSame(metric, panopticodeFile.getMetricByName(metric.getName()));
    }

    public void testClassAddingAndRetrieval() {
        PanopticodeClass anotherDummyClass;
        PanopticodeClass createdClass;
        PanopticodeClass dummyClass;
        PanopticodeFile panopticodeFile;

        panopticodeFile = new PanopticodeFile(panopticodePackage, path, name);

        assertEquals(0, panopticodeFile.getClasses().size());
        assertEquals(0, panopticodeFile.getChildren().size());

        dummyClass = createDummyClass();

        createdClass = panopticodeFile.createAndAddClass(dummyClass.getName(),
                                                         dummyClass.getPositionLine(),
                                                         dummyClass.getPositionColumn());

        assertEquals(1, panopticodeFile.getChildren().size());
        assertEquals(dummyClass.getName(), createdClass.getName());

        assertSame(createdClass, panopticodeFile.createAndAddClass(dummyClass.getName(),
                                                                   dummyClass.getPositionLine(),
                                                                   dummyClass.getPositionColumn()));

        assertEquals(1, panopticodeFile.getChildren().size());

        anotherDummyClass = panopticodeFile.createAndAddClass(dummyClass.getName() + "Foo",
                                                              dummyClass.getPositionLine() + 10,
                                                              dummyClass.getPositionColumn());

        assertEquals(2, panopticodeFile.getChildren().size());
        assertEquals(dummyClass.getName() + "Foo",  anotherDummyClass.getName());
    }

    public void testLevel() {
        PanopticodeFile panopticodeFile;

        panopticodeFile = createDummyFile();

        assertEquals(Level.FILE, panopticodeFile.getLevel());
        assertTrue(panopticodeFile.isLevel(Level.FILE));
    }
    
    public void testToXML() {
        Element classElement;
        Element fileElement;
        Element parentElement;
        int column;
        int line;
        PanopticodeFile panopticodeFile;
        String className;

        parentElement = createDummyElement();
        panopticodeFile = new PanopticodeFile(panopticodePackage, path, name);
        panopticodeFile.toXML(parentElement);

        fileElement = parentElement.element("file");
        assertEquals(path, fileElement.attributeValue("projectPath"));
        assertEquals(0, fileElement.elements("class").size());

        className = "HelloWorld";
        line = 12;
        column = 5;
        panopticodeFile.createAndAddClass(className, line, column);
        parentElement = createDummyElement();
        panopticodeFile.toXML(parentElement);

        fileElement = parentElement.element("file");
        assertEquals(path, fileElement.attributeValue("projectPath"));
        assertEquals(1, fileElement.elements("class").size());
        classElement = (Element) fileElement.elements("class").get(0); 
        assertEquals(className, classElement.attributeValue("name"));
    }

    public void testFromXML() throws DocumentException {
        Element element;
        PanopticodeFile panopticodeFile;
        StringBuffer sb;

        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<file name='Fubar.java' projectPath='src/Fubar.java'>");
        sb.append("  <class name='Fubar'><filePosition line='1' column='2' />");
        sb.append("    <method name='bar'><filePosition line='5' column='3' /></method>");
        sb.append("  </class>");
        sb.append("</file>");

        element = DocumentHelper.parseText(sb.toString()).getRootElement();

        panopticodeFile = PanopticodeFile.fromXML(element, createDummyPackage(), createDummyProject());
        assertEquals("Fubar.java", panopticodeFile.getName());
        assertEquals("src/Fubar.java", panopticodeFile.getPath());
        assertEquals(1, panopticodeFile.getClasses().size());
        assertEquals("Fubar", panopticodeFile.getClasses().get(0).getName());
        assertEquals(1, panopticodeFile.getClasses().get(0).getMethods().size());
        assertEquals("bar", panopticodeFile.getClasses().get(0).getMethods().get(0).getName());
    }

    public void testGetMethods() {
        PanopticodeFile panopticodeFile;
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod1;
        PanopticodeMethod panopticodeMethod2;

        panopticodeFile = new PanopticodeFile(panopticodePackage, path, name);
        panopticodeClass = panopticodeFile.createAndAddClass("Foo", 1, 1);
        panopticodeMethod1 = panopticodeClass.createAndAddMethod("one", new LinkedList<PanopticodeArgument>(), 2, 2);
        panopticodeMethod2 = panopticodeClass.createAndAddMethod("two", new LinkedList<PanopticodeArgument>(), 3, 3);

        List<PanopticodeMethod> methods = panopticodeFile.getMethods();
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
}
