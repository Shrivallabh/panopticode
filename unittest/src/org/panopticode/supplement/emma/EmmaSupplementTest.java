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

import org.panopticode.*;
import static org.panopticode.TestHelpers.*;
import org.dom4j.Element;
import org.dom4j.Document;

import java.util.LinkedList;
import java.util.List;

public class EmmaSupplementTest extends TestCase {
    private EmmaSupplement supplement = new EmmaSupplement();

    public void testAddCoverageToClass() {
        Element childElement;
        Element classElement;
        PanopticodeClass panopticodeClass;

        classElement = createDummyElement();

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "method, %");
        childElement.addAttribute("value", "50% (1/2)");

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "block, %");
        childElement.addAttribute("value", "50% (4/8)");

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "line, %");
        childElement.addAttribute("value", "50% (16/32)");

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "foo, %");
        childElement.addAttribute("value", "50% (64/128)");

        panopticodeClass = createDummyClass();

        supplement.getDeclaration();
        supplement.addCoverageToClass(classElement, panopticodeClass);

        assertEquals(1.0, ((RatioMetric)panopticodeClass.getMetricByName("Method Coverage")).getNumeratorValue(), 0.1);
        assertEquals(2.0, ((RatioMetric)panopticodeClass.getMetricByName("Method Coverage")).getDenominatorValue(), 0.1);

        assertEquals(4.0, ((RatioMetric)panopticodeClass.getMetricByName("Block Coverage")).getNumeratorValue(), 0.1);
        assertEquals(8.0, ((RatioMetric)panopticodeClass.getMetricByName("Block Coverage")).getDenominatorValue(), 0.1);

        assertEquals(16.0, ((RatioMetric)panopticodeClass.getMetricByName("Line Coverage")).getNumeratorValue(), 0.1);
        assertEquals(32.0, ((RatioMetric)panopticodeClass.getMetricByName("Line Coverage")).getDenominatorValue(), 0.1);
    }

    public void testAddCoverageToMethod() {
        Element childElement;
        Element classElement;
        PanopticodeMethod panopticodeMethod;

        classElement = createDummyElement();

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "method, %");
        childElement.addAttribute("value", "50% (1/2)");

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "block, %");
        childElement.addAttribute("value", "50% (4/8)");

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "line, %");
        childElement.addAttribute("value", "50% (16/32)");

        childElement = classElement.addElement("coverage");
        childElement.addAttribute("type", "foo, %");
        childElement.addAttribute("value", "50% (64/128)");

        panopticodeMethod = createDummyMethod();

        supplement.getDeclaration();
        supplement.addCoverageToMethod(classElement, panopticodeMethod);

        assertNull(panopticodeMethod.getMetricByName("Method Coverage"));

        assertEquals(4.0, ((RatioMetric)panopticodeMethod.getMetricByName("Block Coverage")).getNumeratorValue(), 0.1);
        assertEquals(8.0, ((RatioMetric)panopticodeMethod.getMetricByName("Block Coverage")).getDenominatorValue(), 0.1);

        assertEquals(16.0, ((RatioMetric)panopticodeMethod.getMetricByName("Line Coverage")).getNumeratorValue(), 0.1);
        assertEquals(32.0, ((RatioMetric)panopticodeMethod.getMetricByName("Line Coverage")).getDenominatorValue(), 0.1);
    }

    public void testCoveredTotalParsing() {
        assertEquals(5.0, supplement.getCovered("50% (5/10)"));
        assertEquals(10.0, supplement.getTotal("50% (5/10)"));
    }

    public void testCoverageAppliesToMethod() {
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;

        panopticodeClass = createDummyClass();

        panopticodeMethod = createDummyMethod();
        panopticodeMethod.setParentClass(panopticodeClass);

        assertTrue(supplement.coverageAppliesTo(panopticodeMethod));
    }

    public void testCoverageAppliesToAbstractMethod() {
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;

        panopticodeClass = createDummyClass();

        panopticodeMethod = createDummyMethod();
        panopticodeMethod.setParentClass(panopticodeClass);
        panopticodeMethod.setAbstract(true);

        assertFalse(supplement.coverageAppliesTo(panopticodeMethod));
    }

    public void testCoverageAppliesToInterfaceMethod() {
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;

        panopticodeClass = createDummyClass();
        panopticodeClass.setInterface(true);

        panopticodeMethod = createDummyMethod();
        panopticodeMethod.setParentClass(panopticodeClass);

        assertFalse(supplement.coverageAppliesTo(panopticodeMethod));
    }

    public void testCoverageAppliesToClass() {
        PanopticodeClass panopticodeClass;

        panopticodeClass = createDummyClass();

        assertTrue(supplement.coverageAppliesTo(panopticodeClass));
    }

    public void testCoverageAppliesToInterface() {
        PanopticodeClass panopticodeClass;

        panopticodeClass = createDummyClass();
        panopticodeClass.setInterface(true);

        assertFalse(supplement.coverageAppliesTo(panopticodeClass));
    }

    public void testFormatMethodName() {
        LinkedList<PanopticodeArgument> arguments;
        PanopticodeMethod panopticodeMethod;

        panopticodeMethod = createMethod("Foo.Inner", new LinkedList<PanopticodeArgument>(), 1, 1);
        assertEquals("Foo$Inner (", supplement.formatMethodName(panopticodeMethod));


        panopticodeMethod = createDummyMethod();
        assertEquals("sayHello (", supplement.formatMethodName(panopticodeMethod));

        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("one", "int", "int"));
        arguments.add(createArgument("two", "long", "long"));

        panopticodeMethod = createMethod("whatever", arguments, 1, 1);
        assertEquals("whatever (", supplement.formatMethodName(panopticodeMethod));
    }

    public void testSyntheticEnumConstructorArgumentsAreIgnoredInMatching() {
        LinkedList<PanopticodeArgument> arguments;
        PanopticodeClass panopticodeClass;
        PanopticodeMethod panopticodeMethod;

        panopticodeClass = createDummyClass();
        panopticodeClass.setEnum(true);

        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(new PanopticodeArgument("foo", "char", "char"));
        panopticodeMethod = createMethod(panopticodeClass.getName(), arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        panopticodeMethod.setConstructor(true);

        assertTrue(supplement.argumentsCouldMatch(panopticodeMethod,
                                              panopticodeClass.getName() + " (String, int, char): void"));
    }

    public void testFormatClassName() {
        PanopticodeFile panopticodeFile;
        PanopticodeClass panopticodeClass;

        panopticodeFile = createDummyFile();

        panopticodeClass = panopticodeFile.createAndAddClass("HelloWorld", 1, 1);
        assertEquals("HelloWorld" , supplement.formatClassName(panopticodeClass));

        panopticodeClass = panopticodeFile.createAndAddClass("HelloWorld.Inner", 1, 1);
        assertEquals("HelloWorld$Inner" , supplement.formatClassName(panopticodeClass));
    }

    public void testFormatType() {
        assertFormattedTypeEquals("That", "That");
        assertFormattedTypeEquals("That[]", "That []");
        assertFormattedTypeEquals("T", "Object");
        assertFormattedTypeEquals("T[]", "Object []");
        assertFormattedTypeEquals("Foo.Bar", "Foo$Bar");
    }

    private void assertFormattedTypeEquals(String simpleType, String formatted) {
        PanopticodeArgument panopticodeArgument;

        panopticodeArgument = createArgument("foo", "bar", simpleType);
        assertEquals(formatted, supplement.formatType(panopticodeArgument));
    }

    public void testMetricsDeclaredOnlyOnce() {
        assertSame(supplement.getDeclaration(), supplement.getDeclaration());
    }
    
    public void testMetricsDeclaredProperly() {
        SupplementDeclaration supplementDeclaration = null;
        RatioMetricDeclaration methodCoverageMetricDeclaration = null;
        RatioMetricDeclaration blockCoverageMetricDeclaration = null;
        RatioMetricDeclaration lineCoverageMetricDeclaration = null;

        supplementDeclaration = supplement.getDeclaration();

        for(MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
            if("Method Coverage".equals(metricDeclaration.getName())) {
                methodCoverageMetricDeclaration = (RatioMetricDeclaration) metricDeclaration;
            } else if ("Block Coverage".equals(metricDeclaration.getName())) {
                blockCoverageMetricDeclaration = (RatioMetricDeclaration) metricDeclaration;
            } else if ("Line Coverage".equals(metricDeclaration.getName())) {
                lineCoverageMetricDeclaration = (RatioMetricDeclaration) metricDeclaration;
            } else {
                fail("Improper declaration found: " + metricDeclaration.getName());
            }
        }

        assertFalse(methodCoverageMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(methodCoverageMetricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(methodCoverageMetricDeclaration.isLevel(Level.FILE));
        assertTrue(methodCoverageMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(methodCoverageMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(blockCoverageMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(blockCoverageMetricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(blockCoverageMetricDeclaration.isLevel(Level.FILE));
        assertTrue(blockCoverageMetricDeclaration.isLevel(Level.CLASS));
        assertTrue(blockCoverageMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(lineCoverageMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(lineCoverageMetricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(lineCoverageMetricDeclaration.isLevel(Level.FILE));
        assertTrue(lineCoverageMetricDeclaration.isLevel(Level.CLASS));
        assertTrue(lineCoverageMetricDeclaration.isLevel(Level.METHOD));
    }

    public void testGetElementByPackage() throws Exception {
        Element packageElement;
        PanopticodePackage panopticodePackage;

        panopticodePackage = createPackage(createDummyProject(), "");
        packageElement = supplement.getElementByPackage(createEmmaDocument(), panopticodePackage);
        assertEquals("default package", packageElement.attributeValue("name"));

        panopticodePackage = createPackage(createDummyProject(), "org.panopticode");
        packageElement = supplement.getElementByPackage(createEmmaDocument(), panopticodePackage);
        assertEquals("org.panopticode", packageElement.attributeValue("name"));

        panopticodePackage = createPackage(createDummyProject(), "org.nope");
        try {
            supplement.getElementByPackage(createEmmaDocument(), panopticodePackage);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find package 'org.nope'", e.getMessage());
        }
    }

    public void testGetElementByFile() throws Exception {
        Element fileElement;
        PanopticodeFile panopticodeFile;
        PanopticodePackage panopticodePackage;

        panopticodePackage = createPackage(createDummyProject(), "");

        panopticodeFile = createFile(panopticodePackage, "Hello.java", "Hello.java");
        fileElement = supplement.getElementByFile(createEmmaDocument(), panopticodeFile);
        assertEquals("Hello.java", fileElement.attributeValue("name"));

        panopticodeFile = createFile(panopticodePackage, "World.java", "World.java");
        fileElement = supplement.getElementByFile(createEmmaDocument(), panopticodeFile);
        assertEquals("World.java", fileElement.attributeValue("name"));

        panopticodeFile = createFile(panopticodePackage, "Nope.java", "Nope.java");
        try {
            supplement.getElementByFile(createEmmaDocument(), panopticodeFile);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find file 'Nope.java' in package ''", e.getMessage());
        }

        panopticodePackage = createPackage(createDummyProject(), "org.nope");
        panopticodeFile = createFile(panopticodePackage, "Nope.java", "Nope.java");
        try {
            supplement.getElementByFile(createEmmaDocument(), panopticodeFile);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find package 'org.nope'", e.getMessage());
        }
    }

    public void testGetElementByClass() throws Exception {
        Element classElement;
        PanopticodeClass panopticodeClass;
        PanopticodeFile panopticodeFile;
        PanopticodePackage panopticodePackage;

        panopticodePackage = createPackage(createDummyProject(), "");
        panopticodeFile = createFile(panopticodePackage, "Hello.java", "Hello.java");

        panopticodeClass = createClass(panopticodeFile, "Hello", 1, 1);
        classElement = supplement.getElementByClass(createEmmaDocument(), panopticodeClass);
        assertEquals("Hello", classElement.attributeValue("name"));

        panopticodeClass = createClass(panopticodeFile, "Hello.Inner", 1, 1);
        classElement = supplement.getElementByClass(createEmmaDocument(), panopticodeClass);
        assertEquals("Hello$Inner", classElement.attributeValue("name"));

        panopticodeClass = createClass(panopticodeFile, "Nope", 1, 1);
        try {
            supplement.getElementByClass(createEmmaDocument(), panopticodeClass);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find class 'Nope' in file 'Hello.java'", e.getMessage());
        }

        panopticodePackage = createPackage(createDummyProject(), "org.nope");
        panopticodeFile = createFile(panopticodePackage, "Nope.java", "Nope.java");
        panopticodeClass = createClass(panopticodeFile, "Nope", 1, 1);
        try {
            supplement.getElementByClass(createEmmaDocument(), panopticodeClass);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find package 'org.nope'", e.getMessage());
        }
    }

    public void testGetElementByMethod() throws Exception {
        Document doc;
        Element methodElement;
        LinkedList<PanopticodeArgument> arguments;
        PanopticodeClass panopticodeClass;
        PanopticodeFile panopticodeFile;
        PanopticodeMethod panopticodeMethod;
        PanopticodePackage panopticodePackage;

        doc = createEmmaDocument();
        panopticodePackage = createPackage(createDummyProject(), "");
        panopticodeFile = createFile(panopticodePackage, "Hello.java", "Hello.java");
        panopticodeClass = createClass(panopticodeFile, "Hello", 1, 1);

        // one match with no arguments
        arguments = new LinkedList<PanopticodeArgument>();
        panopticodeMethod = createMethod("say", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        methodElement = supplement.getElementByMethod(doc, panopticodeMethod);
        assertEquals("say (): void", methodElement.attributeValue("name"));

        // one match with arguments
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "int", "int"));
        panopticodeMethod = createMethod("sayHello", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        methodElement = supplement.getElementByMethod(doc, panopticodeMethod);
        assertEquals("sayHello (int): String", methodElement.attributeValue("name"));

        // no match because of different argument list lengths
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "int", "int"));
        panopticodeMethod = createMethod("say", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        try {
            supplement.getElementByMethod(doc, panopticodeMethod);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find method 'say (' in class 'Hello'", e.getMessage());
        }

        // no match because method name not found
        arguments = new LinkedList<PanopticodeArgument>();
        panopticodeMethod = createMethod("nope", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        try {
            supplement.getElementByMethod(doc, panopticodeMethod);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find method 'nope (' in class 'Hello'", e.getMessage());
        }

        // multiple possible matches found
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "org.panopticode.foo.Thing", "Thing"));
        panopticodeMethod = createMethod("sayHello", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "org.panopticode.bar.Thing", "Thing"));
        panopticodeMethod = createMethod("sayHello", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        Element classElement = supplement.getElementByClass(doc, panopticodeClass);
        methodElement = classElement.addElement("method");
        methodElement.addAttribute("name", "sayHello (Thing): String");
        methodElement = classElement.addElement("method");
        methodElement.addAttribute("name", "sayHello (Thing): String");
        try {
            supplement.getElementByMethod(doc, panopticodeMethod);
            fail();
        } catch (CouldntNarrowMethodMatchesException e) {
            assertEquals("Couldn't narrow match for method 'Hello.sayHello(org.panopticode.bar.Thing)' from ['sayHello (Thing): String', 'sayHello (Thing): String'] in class 'Hello'", e.getMessage());
        }

        // multiple matches narrowed by exact argument matching
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "Thing", "Thing"));
        panopticodeMethod = createMethod("sayHello", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        doc = createEmmaDocument();
        classElement = supplement.getElementByClass(doc, panopticodeClass);
        methodElement = classElement.addElement("method");
        methodElement.addAttribute("name", "sayHello (TheThing): String");
        methodElement = classElement.addElement("method");
        methodElement.addAttribute("name", "sayHello (Thing): String");
        methodElement = supplement.getElementByMethod(doc, panopticodeMethod);
        assertEquals("sayHello (Thing): String", methodElement.attributeValue("name"));

        // no match because package not found
        panopticodePackage = createPackage(createDummyProject(), "org.nope");
        panopticodeFile = createFile(panopticodePackage, "Nope.java", "Nope.java");
        panopticodeClass = createClass(panopticodeFile, "Nope", 1, 1);
        arguments = new LinkedList<PanopticodeArgument>();
        panopticodeMethod = createMethod("nope", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        try {
            supplement.getElementByMethod(doc, panopticodeMethod);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find package 'org.nope'", e.getMessage());
        }
    }

    public void testSupplementDeclarationAddedToProjectOnLoad() {
        List<SupplementDeclaration> declared;
        PanopticodeProject project;

        project = createDummyProject();

        supplement.loadData(project, new String[] { "sample_data/emma.xml" });

        declared = project.getSupplementsDeclared();

        assertEquals(1, declared.size());
        assertEquals(supplement.getClass().getName(), declared.get(0).getSupplementClass());
    }

    public void testLoadData() {
        LinkedList<PanopticodeArgument> arguments;
        PanopticodeClass panopticodeClass;
        PanopticodeClass panopticodeInterfaceClass;
        PanopticodeFile panopticodeFile;
        PanopticodeMethod panopticodeMethod;
        PanopticodeMethod panopticodeAbstractMethod;
        PanopticodePackage panopticodePackage;
        PanopticodeProject panopticodeProject;

        // setup project
        panopticodeProject = createDummyProject();
        panopticodePackage = panopticodeProject.createAndAddPackage("");
        panopticodeFile = panopticodePackage.createAndAddFile("Hello.java", "Hello.java");
        panopticodeClass = panopticodeFile.createAndAddClass("Hello", 1, 1);
        panopticodeFile.createAndAddClass("Nope", 1, 1);
        panopticodeInterfaceClass = panopticodeFile.createAndAddClass("HelloInterface", 1, 1);
        panopticodeInterfaceClass.setInterface(true);
        arguments = new LinkedList<PanopticodeArgument>();
        panopticodeMethod = panopticodeClass.createAndAddMethod("say", arguments, 1, 1);
        panopticodeClass.createAndAddMethod("nope", arguments, 1, 1);
        panopticodeAbstractMethod = panopticodeClass.createAndAddMethod("sayAbstract", arguments, 1, 1);
        panopticodeAbstractMethod.setAbstract(true);

        // load the data
        Document doc = createEmmaDocument();
        supplement.getDeclaration();
        supplement.loadMethodData(panopticodeProject, doc);
        supplement.loadClassData(panopticodeProject, doc);

        // ensure no coverage metrics are loaded for interfaces
        assertNull(panopticodeInterfaceClass.getMetricByName("Method Coverage"));
        assertNull(panopticodeInterfaceClass.getMetricByName("Bloc Coverage"));
        assertNull(panopticodeInterfaceClass.getMetricByName("Line Coverage"));

        // ensure no coverage metrics are loaded for abstract methods
        assertNull(panopticodeAbstractMethod.getMetricByName("Method Coverage"));
        assertNull(panopticodeAbstractMethod.getMetricByName("Bloc Coverage"));
        assertNull(panopticodeAbstractMethod.getMetricByName("Line Coverage"));

        // check correct data is loaded for class
        assertMetricValue(panopticodeClass, "Method Coverage", 1.0, 2.0);
        assertMetricValue(panopticodeClass, "Block Coverage", 4.0, 8.0);
        assertMetricValue(panopticodeClass, "Line Coverage", 16.0, 32.0);
        
        // check correct data is loaded for method
        assertMetricValue(panopticodeMethod, "Block Coverage", 256.0, 512.0);
        assertMetricValue(panopticodeMethod, "Line Coverage", 1024.0, 2048.0);
    }

    private void assertMetricValue(PanopticodeClass panopticodeClass, String metricName,
                                   double numerator, double denominator) {
        RatioMetric ratioMetric;

        ratioMetric = (RatioMetric) panopticodeClass.getMetricByName(metricName);

        assertEquals(numerator, ratioMetric.getNumeratorValue(), 0.1);
        assertEquals(denominator, ratioMetric.getDenominatorValue(), 0.1);
    }

    private void assertMetricValue(PanopticodeMethod panopticodeMethod, String metricName,
                                   double numerator, double denominator) {
        RatioMetric ratioMetric;

        ratioMetric = (RatioMetric) panopticodeMethod.getMetricByName(metricName);

        assertEquals(numerator, ratioMetric.getNumeratorValue(), 0.1);
        assertEquals(denominator, ratioMetric.getDenominatorValue(), 0.1);
    }

    private Document createEmmaDocument() {
        Document doc;
        Element allElement;
        Element package1Element;
        Element package2Element;
        Element srcfile1Element;
        Element srcfile2Element;
        Element class1Element;
        Element class2Element;
        Element method1Element;
        Element method2Element;
        Element methodCoverageElement;
        Element blockCoverageElement;
        Element lineCoverageElement;

        doc = createDummyDocument();
        allElement = doc.addElement("report").addElement("data").addElement("all");

        package1Element = allElement.addElement("package");
        package1Element.addAttribute("name", "default package");

        package2Element = allElement.addElement("package");
        package2Element.addAttribute("name", "org.panopticode");

        srcfile1Element = package1Element.addElement("srcfile");
        srcfile1Element.addAttribute("name", "Hello.java");

        srcfile2Element = package1Element.addElement("srcfile");
        srcfile2Element.addAttribute("name", "World.java");

        class1Element = srcfile1Element.addElement("class");
        class1Element.addAttribute("name", "Hello");

        methodCoverageElement = class1Element.addElement("coverage");
        methodCoverageElement.addAttribute("type", "method, %");
        methodCoverageElement.addAttribute("value", "50%  (1/2)");

        blockCoverageElement = class1Element.addElement("coverage");
        blockCoverageElement.addAttribute("type", "block, %");
        blockCoverageElement.addAttribute("value", "50%  (4/8)");

        lineCoverageElement = class1Element.addElement("coverage");
        lineCoverageElement.addAttribute("type", "line, %");
        lineCoverageElement.addAttribute("value", "50%  (16/32)");

        class2Element = srcfile1Element.addElement("class");
        class2Element.addAttribute("name", "Hello$Inner");

        method1Element = class1Element.addElement("method");
        method1Element.addAttribute("name", "say (): void");

        methodCoverageElement = method1Element.addElement("coverage");
        methodCoverageElement.addAttribute("type", "method, %");
        methodCoverageElement.addAttribute("value", "50%  (64/128)");

        blockCoverageElement = method1Element.addElement("coverage");
        blockCoverageElement.addAttribute("type", "block, %");
        blockCoverageElement.addAttribute("value", "50%  (256/512)");

        lineCoverageElement = method1Element.addElement("coverage");
        lineCoverageElement.addAttribute("type", "line, %");
        lineCoverageElement.addAttribute("value", "50%  (1024/2048)");

        method2Element = class1Element.addElement("method");
        method2Element.addAttribute("name", "sayHello (int): String");

        return doc;
    }
}
