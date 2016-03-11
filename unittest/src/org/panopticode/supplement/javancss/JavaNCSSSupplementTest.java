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
package org.panopticode.supplement.javancss;

import junit.framework.TestCase;

import org.panopticode.*;

import static org.panopticode.TestHelpers.*;

import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;
import java.util.LinkedList;

public class JavaNCSSSupplementTest extends TestCase {
    private JavaNCSSSupplement supplement = new JavaNCSSSupplement();

    protected void setUp() throws Exception {
        super.setUp();
        supplement.getDeclaration();
    }

    public void testMetricsDeclaredOnlyOnce() {
        assertSame(supplement.getDeclaration(), supplement.getDeclaration());
    }

    public void testGetAdjustedInnerClassConstructorName() {
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;

        panopticodeClass = createClass(createDummyFile(), "Bar.Foo", 1, 1);
        panopticodeMethod = createMethod("Foo.Foo", new LinkedList<PanopticodeArgument>(), 1, 1);
        panopticodeMethod.setConstructor(true);
        panopticodeMethod.setParentClass(panopticodeClass);

        assertEquals("Bar.Foo.Foo", supplement.getAdjustedInnerClassConstructorName(panopticodeMethod));
    }

    public void testFormatMethodName() {
        LinkedList<PanopticodeArgument> arguments;
        PanopticodeArgument panopticodeArgument;
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;

        panopticodeClass = createClass(createDummyFile(), "Fubar", 1, 1);
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createDummyArgument());
        panopticodeMethod = createMethod("whatever", arguments, 1, 1);
        panopticodeMethod.setConstructor(true);
        panopticodeMethod.setParentClass(panopticodeClass);

        assertEquals("Fubar.whatever(String)", supplement.formatMethodName(panopticodeMethod));

        panopticodeClass = createClass(createDummyFile(), "Fubar", 1, 1);
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createDummyArgument());
        panopticodeArgument = createArgument("blah", "int[]", "int[]");
        panopticodeArgument.setVarArg(true);
        arguments.add(panopticodeArgument);
        panopticodeMethod = createMethod("whatever", arguments, 1, 1);
        panopticodeMethod.setConstructor(true);
        panopticodeMethod.setParentClass(panopticodeClass);

        assertEquals("Fubar.whatever(String,int)", supplement.formatMethodName(panopticodeMethod));

        panopticodeClass = createClass(createDummyFile(), "Bar.Foo", 1, 1);
        panopticodeMethod = createMethod("Foo.Foo", new LinkedList<PanopticodeArgument>(), 1, 1);
        panopticodeMethod.setConstructor(true);
        panopticodeMethod.setParentClass(panopticodeClass);

        assertEquals("Bar.Foo.Foo()", supplement.formatMethodName(panopticodeMethod));
    }

    public void testMetricsDeclaredProperly() {
        SupplementDeclaration supplementDeclaration = null;
        IntegerMetricDeclaration ccnMetricDeclaration = null;
        IntegerMetricDeclaration ncssMetricDeclaration = null;
        IntegerMetricDeclaration maxCcnMetricDeclaration = null;
        supplementDeclaration = supplement.getDeclaration();

        for(MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
            if("NCSS".equals(metricDeclaration.getName())) {
                ncssMetricDeclaration = (IntegerMetricDeclaration) metricDeclaration;
            } else if ("CCN".equals(metricDeclaration.getName())) {
                ccnMetricDeclaration = (IntegerMetricDeclaration) metricDeclaration;
            } else if ("MAX-CCN".equals(metricDeclaration.getName())) {
                maxCcnMetricDeclaration = (IntegerMetricDeclaration) metricDeclaration;
            } else {
                fail("Improper declaration found: " + metricDeclaration.getName());
            }
        }

        assertFalse(ncssMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(ncssMetricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(ncssMetricDeclaration.isLevel(Level.FILE));
        assertTrue(ncssMetricDeclaration.isLevel(Level.CLASS));
        assertTrue(ncssMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(ccnMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(ccnMetricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(ccnMetricDeclaration.isLevel(Level.FILE));
        assertFalse(ccnMetricDeclaration.isLevel(Level.CLASS));
        assertTrue(ccnMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(maxCcnMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(maxCcnMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(maxCcnMetricDeclaration.isLevel(Level.FILE));
        assertFalse(maxCcnMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(maxCcnMetricDeclaration.isLevel(Level.METHOD));

        
    }

    public void testSupplementDeclarationAddedToProjectOnLoad() {
        List<SupplementDeclaration> declared;
        PanopticodeProject project;

        project = createDummyProject();

        supplement.loadData(project, new String[] { "sample_data/javancss.xml" });

        declared = project.getSupplementsDeclared();

        assertEquals(1, declared.size());
        assertEquals(supplement.getClass().getName(), declared.get(0).getSupplementClass());
    }

    public void testFormatsClassNameInDefaultPackage() {
        assertEquals("Whatever", supplement.formatClassName(buildClass("", "Whatever")));
    }

    public void testFormatsClassNameInNonDefaultPackage() {
        assertEquals("why.Not", supplement.formatClassName(buildClass("why", "Not")));
    }

    public void testGetElementByClassFailsCorrectlyWhenNoneExists() {
        Document doc;
        Element objectsElement;
        PanopticodeClass panopticodeClass;

        doc = createDummyDocument();

        panopticodeClass = buildClass("foo", "Bar");

        objectsElement = doc.addElement("javancss").addElement("objects");
        objectsElement.addElement("object").addElement("name").addText("Bar");
        objectsElement.addElement("object").addElement("name").addText("foo.FuBar");

        assertNull(supplement.getElementByClass(doc, panopticodeClass));
    }

    public void testGetElementByClassFindsCorrectElement() {
        Document doc;
        Element objectsElement;
        PanopticodeClass panopticodeClass;
        String className;

        doc = createDummyDocument();

        panopticodeClass = buildClass("foo", "Bar");
        className = "foo.Bar";

        objectsElement = doc.addElement("javancss").addElement("objects");
        objectsElement.addElement("object").addElement("name").addText("Bar");
        objectsElement.addElement("object").addElement("name").addText(className);
        objectsElement.addElement("object").addElement("name").addText("foo.FuBar");

        assertEquals(className, supplement.getElementByClass(doc, panopticodeClass).elementText("name"));
    }

    public void testGetElementByMethodFailsCorrectlyWhenNoneExists() {
        Document doc;
        Element functionsElement;
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;

        doc = createDummyDocument();

        panopticodeClass = buildClass("foo", "Bar");
        panopticodeMethod = panopticodeClass.createAndAddMethod("something", new LinkedList<PanopticodeArgument>(), 10, 5);

        functionsElement = doc.addElement("javancss").addElement("functions");
        functionsElement.addElement("function").addElement("name").addText("Bar.whatever()");
        functionsElement.addElement("function").addElement("name").addText("foo.FuBar.whatever");

        assertNull(supplement.getElementByMethod(doc, panopticodeMethod));
    }

    public void testGetElementByMethodFindsCorrectElement() {
        Document doc;
        Element functionsElement;
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;
        String methodName;

        doc = createDummyDocument();

        panopticodeClass = buildClass("foo", "Bar");
        panopticodeMethod = panopticodeClass.createAndAddMethod("something", new LinkedList<PanopticodeArgument>(), 10, 5);
        methodName = "foo.Bar.something()";

        functionsElement = doc.addElement("javancss").addElement("functions");
        functionsElement.addElement("function").addElement("name").addText("Bar.whatever()");
        functionsElement.addElement("function").addElement("name").addText(methodName);
        functionsElement.addElement("function").addElement("name").addText("foo.FuBar.whatever");

        assertEquals(methodName, supplement.getElementByMethod(doc, panopticodeMethod).elementText("name"));
    }

    public void testLoadClassData() {
        Document doc;
        Element objectsElement;
        Element objectElement;
        IntegerMetricDeclaration metricDeclaration;
        PanopticodeProject panopticodeProject;
        PanopticodePackage panopticodePackage;
        PanopticodeFile panopticodeFile;
        PanopticodeMethod panopticodeMethod;

        // Setup project
        panopticodeProject = createDummyProject();
        panopticodePackage = panopticodeProject.createAndAddPackage("foo");
        panopticodeFile = panopticodePackage.createAndAddFile("sdfg", "Bar");
        panopticodeFile.createAndAddClass("Bar", 1, 1);
        panopticodeFile = panopticodePackage.createAndAddFile("sdfg", "Fu");
        panopticodeFile.createAndAddClass("Fu", 1, 1);
        panopticodeMethod = panopticodeFile.createAndAddClass("Fu.Inner", 1, 1).createAndAddMethod("foo", new LinkedList<PanopticodeArgument>(), 1, 1);
        metricDeclaration = new IntegerMetricDeclaration(null, "NCSS");
        panopticodeMethod.addMetric(metricDeclaration.createMetric(7));
        panopticodeFile = panopticodePackage.createAndAddFile("sdfg", "FuBar");
        panopticodeFile.createAndAddClass("FuBar", 1, 1);

        // Setup document
        doc = createDummyDocument();

        objectsElement = doc.addElement("javancss").addElement("objects");

        objectElement = objectsElement.addElement("object");
        objectElement.addElement("name").addText("foo.Bar");
        objectElement.addElement("ncss").addText("3456");

        objectElement = objectsElement.addElement("object");
        objectElement.addElement("name").addText("foo.Fu");
        objectElement.addElement("ncss").addText("3");

        // Load the data
        supplement.loadClassData(panopticodeProject, doc);

        // Check results
        List<String> errors = supplement.getDeclaration().getErrors();
        assertEquals(1, errors.size());
        assertEquals("ERROR - JavaNCSSSupplement - Could not find match for class 'foo.FuBar'", errors.get(0));
        for (PanopticodeClass panopticodeClass : panopticodeProject.getClasses()) {
            if ("foo.Bar".equals(panopticodeClass.getFullyQualifiedName())) {
                assertEquals(3456, (int) ((IntegerMetric)panopticodeClass.getMetricByName("NCSS")).getValue());
            } else if ("foo.Fu".equals(panopticodeClass.getFullyQualifiedName())) {
                assertEquals(3, (int) ((IntegerMetric)panopticodeClass.getMetricByName("NCSS")).getValue());
            } else if ("foo.FuBar".equals(panopticodeClass.getFullyQualifiedName())) {
                assertNull(panopticodeClass.getMetricByName("NCSS"));
            } else if ("foo.Fu.Inner".equals(panopticodeClass.getFullyQualifiedName())) {
                assertEquals(7, (int) ((IntegerMetric)panopticodeClass.getMetricByName("NCSS")).getValue());
            } else {
                fail("Found class that shouldn't exist: " + panopticodeClass.getFullyQualifiedName());
            }
        }
    }
    public void testLoadMethodData() {
        Document doc;
        Element functionsElement;
        Element functionElement;
        List<PanopticodeArgument> arguments;
        PanopticodeProject panopticodeProject;
        PanopticodePackage panopticodePackage;
        PanopticodeFile panopticodeFile;
        PanopticodeClass panopticodeClass;

        // Setup project
        panopticodeProject = createDummyProject();
        panopticodePackage = panopticodeProject.createAndAddPackage("foo");
        panopticodeFile = panopticodePackage.createAndAddFile("sdfg", "Bar");
        panopticodeClass = panopticodeFile.createAndAddClass("Bar", 1, 1);
        panopticodeClass.setEnum(true);
        arguments = new LinkedList<PanopticodeArgument>();
        panopticodeClass.createAndAddMethod("one", arguments, 2, 1);
        panopticodeClass.createAndAddMethod("two", arguments, 3, 1);
        panopticodeClass.createAndAddMethod("three", arguments, 4, 1);
        panopticodeClass.createAndAddMethod("values", arguments, 5, 1);

        // Setup document
        doc = createDummyDocument();

        functionsElement = doc.addElement("javancss").addElement("functions");

        functionElement = functionsElement.addElement("function");
        functionElement.addElement("name").addText("foo.Bar.one()");
        functionElement.addElement("ncss").addText("3456");
        functionElement.addElement("ccn").addText("14");

        functionElement = functionsElement.addElement("function");
        functionElement.addElement("name").addText("foo.Bar.three()");
        functionElement.addElement("ncss").addText("3");
        functionElement.addElement("ccn").addText("1");

        // Load the data
        supplement.loadMethodData(panopticodeProject, doc);

        // Check results
        List<String> errors = supplement.getDeclaration().getErrors();
        assertEquals(1, errors.size());
        assertEquals("WARNING - JavaNCSSSupplement - Could not find match for method 'foo.Bar.two()'", errors.get(0));
        for (PanopticodeMethod panopticodeMethod : panopticodeProject.getMethods()) {
            if ("foo.Bar.values()".equals(panopticodeMethod.getSignature())) {
                assertNull(panopticodeMethod.getMetricByName("NCSS"));
                assertNull(panopticodeMethod.getMetricByName("CCN"));
            } else if ("foo.Bar.one()".equals(panopticodeMethod.getSignature())) {
                assertEquals(3456, (int) ((IntegerMetric)panopticodeMethod.getMetricByName("NCSS")).getValue());
                assertEquals(14, (int) ((IntegerMetric)panopticodeMethod.getMetricByName("CCN")).getValue());
            } else if ("foo.Bar.three()".equals(panopticodeMethod.getSignature())) {
                assertEquals(3, (int) ((IntegerMetric)panopticodeMethod.getMetricByName("NCSS")).getValue());
                assertEquals(1, (int) ((IntegerMetric)panopticodeMethod.getMetricByName("CCN")).getValue());
            } else if ("foo.Bar.two()".equals(panopticodeMethod.getSignature())) {
                assertNull(panopticodeMethod.getMetricByName("NCSS"));
                assertNull(panopticodeMethod.getMetricByName("CCN"));
            } else {
                fail("Found method that shouldn't exist: " + panopticodeMethod.getSignature());
            }
        }
    }


    private PanopticodeClass buildClass(PanopticodeProject project, String packageName, String className) {
        PanopticodePackage panopticodePackage;
        PanopticodeFile panopticodeFile;
        PanopticodeClass panopticodeClass;

        panopticodePackage = createPackage(project, packageName);
        panopticodeFile = createFile(panopticodePackage, "", "");
        panopticodeClass = createClass(panopticodeFile, className, 1, 1);

        return panopticodeClass;
    }

    private PanopticodeClass buildClass(String packageName, String className) {
        return buildClass(createDummyProject(), packageName, className);
    }

    public void testIsSyntheticEnumMethod() {
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;
        List<PanopticodeArgument> arguments;

        arguments = new LinkedList<PanopticodeArgument>();

        panopticodeClass = createClass(createDummyFile(), "HelloWorld", 1, 1);
        panopticodeMethod = createMethod("foo", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        assertFalse(supplement.isSyntheticEnumMethod(panopticodeMethod));

        panopticodeClass = createClass(createDummyFile(), "HelloWorld", 1, 1);
        panopticodeMethod = createMethod("values", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        assertFalse(supplement.isSyntheticEnumMethod(panopticodeMethod));

        panopticodeClass = createClass(createDummyFile(), "HelloWorld", 1, 1);
        panopticodeClass.setEnum(true);
        panopticodeMethod = createMethod("values", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        assertTrue(supplement.isSyntheticEnumMethod(panopticodeMethod));

        arguments.add(createArgument("whatever", "java.lang.String", "String"));

        panopticodeClass = createClass(createDummyFile(), "HelloWorld", 1, 1);
        panopticodeClass.setEnum(true);
        panopticodeMethod = createMethod("values", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        assertFalse(supplement.isSyntheticEnumMethod(panopticodeMethod));

        panopticodeClass = createClass(createDummyFile(), "HelloWorld", 1, 1);
        panopticodeMethod = createMethod("foo", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        assertFalse(supplement.isSyntheticEnumMethod(panopticodeMethod));

        panopticodeClass = createClass(createDummyFile(), "HelloWorld", 1, 1);
        panopticodeMethod = createMethod("valueOf", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        assertFalse(supplement.isSyntheticEnumMethod(panopticodeMethod));

        panopticodeClass = createClass(createDummyFile(), "HelloWorld", 1, 1);
        panopticodeClass.setEnum(true);
        panopticodeMethod = createMethod("valueOf", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        assertTrue(supplement.isSyntheticEnumMethod(panopticodeMethod));
    }
}
