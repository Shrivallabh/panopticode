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
package org.panopticode.supplement.cobertura;

import junit.framework.TestCase;

import org.panopticode.*;

import static org.panopticode.TestHelpers.*;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;

import java.util.LinkedList;
import java.util.List;

public class CoberturaSupplementTest extends TestCase {
    private CoberturaSupplement supplement = new CoberturaSupplement();

    public void testAddCoverageToClass() {
        Element classElement;
        PanopticodeClass panopticodeClass;

        classElement = createDummyElement();
        
        Element lines=classElement.addElement("lines");
        addNewLine(lines, "10", false, null);
		
        panopticodeClass = createDummyClass();

        supplement.getDeclaration();
        supplement.addCoverageToClass(classElement, panopticodeClass);

        RatioMetric branchMetric = (RatioMetric)panopticodeClass.getMetricByName("Branch Coverage");
		assertEquals(0.0, branchMetric.getNumeratorValue(), 0.1);
		assertEquals(0.0, branchMetric.getDenominatorValue(), 0.1);

        RatioMetric lineMetric = (RatioMetric)panopticodeClass.getMetricByName("Line Coverage");
		assertEquals(1.0, lineMetric.getNumeratorValue(), 0.1);
		assertEquals(1.0, lineMetric.getDenominatorValue(), 0.1);
    }

    public void testAddCoverageToMethod() {
        Element methodElement;
        PanopticodeMethod panopticodeMethod;

        methodElement = createDummyElement();

        Element lines=methodElement.addElement("lines");
        addNewLine(lines, "10", false, null);
		
        panopticodeMethod = createDummyMethod();

        supplement.getDeclaration();
        supplement.addCoverageToMethod(methodElement, panopticodeMethod);

        assertNull(panopticodeMethod.getMetricByName("Method Coverage"));

        RatioMetric branchMetric = (RatioMetric)panopticodeMethod.getMetricByName("Branch Coverage");
		assertEquals(1.0, branchMetric.getNumeratorValue(), 1.0);
		assertEquals(1.0, branchMetric.getDenominatorValue(), 1.0);

        RatioMetric lineMetric = (RatioMetric)panopticodeMethod.getMetricByName("Line Coverage");
		assertEquals(1.0, lineMetric.getNumeratorValue(), 1.0);
		assertEquals(1.0, lineMetric.getDenominatorValue(), 1.0);
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
//
    public void testFormatMethodName() {
        LinkedList<PanopticodeArgument> arguments;
        PanopticodeMethod panopticodeMethod;

        panopticodeMethod = createMethod("Foo.Inner", new LinkedList<PanopticodeArgument>(), 1, 1);
        assertEquals("Foo$Inner", supplement.formatMethodName(panopticodeMethod));


        panopticodeMethod = createDummyMethod();
        assertEquals("sayHello", supplement.formatMethodName(panopticodeMethod));

        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("one", "int", "int"));
        arguments.add(createArgument("two", "long", "long"));

        panopticodeMethod = createMethod("whatever", arguments, 1, 1);
        assertEquals("whatever", supplement.formatMethodName(panopticodeMethod));
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
                                              "(Ljava.lang.String;IC)V"));
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
//
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
//
    public void testMetricsDeclaredOnlyOnce() {
        assertSame(supplement.getDeclaration(), supplement.getDeclaration());
    }
    
    public void testMetricsDeclaredProperly() {
        SupplementDeclaration supplementDeclaration = null;
        RatioMetricDeclaration branchCoverageMetricDeclaration = null;
        RatioMetricDeclaration lineCoverageMetricDeclaration = null;

        supplementDeclaration = supplement.getDeclaration();

        for(MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
            if("Branch Coverage".equals(metricDeclaration.getName())) {
            	branchCoverageMetricDeclaration = (RatioMetricDeclaration) metricDeclaration;
            } else if ("Line Coverage".equals(metricDeclaration.getName())) {
                lineCoverageMetricDeclaration = (RatioMetricDeclaration) metricDeclaration;
            } else {
                fail("Improper declaration found: " + metricDeclaration.getName());
            }
        }


        assertFalse(branchCoverageMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(branchCoverageMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(branchCoverageMetricDeclaration.isLevel(Level.FILE));
        assertTrue(branchCoverageMetricDeclaration.isLevel(Level.CLASS));
        assertTrue(branchCoverageMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(lineCoverageMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(lineCoverageMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(lineCoverageMetricDeclaration.isLevel(Level.FILE));
        assertTrue(lineCoverageMetricDeclaration.isLevel(Level.CLASS));
        assertTrue(lineCoverageMetricDeclaration.isLevel(Level.METHOD));
    }

    public void testGetElementByPackage() throws Exception {
        Element packageElement;
        PanopticodePackage panopticodePackage;

        panopticodePackage = createPackage(createDummyProject(), "");
        packageElement = supplement.getElementByPackage(createCoberturaDocument(), panopticodePackage);
        assertEquals("", packageElement.attributeValue("name"));

        panopticodePackage = createPackage(createDummyProject(), "org.panopticode");
        packageElement = supplement.getElementByPackage(createCoberturaDocument(), panopticodePackage);
        assertEquals("org.panopticode", packageElement.attributeValue("name"));

        panopticodePackage = createPackage(createDummyProject(), "org.nope");
        try {
            supplement.getElementByPackage(createCoberturaDocument(), panopticodePackage);
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
        classElement = supplement.getElementByClass(createCoberturaDocument(), panopticodeClass);
        assertEquals("Hello", classElement.attributeValue("name"));

        panopticodeClass = createClass(panopticodeFile, "Hello.Inner", 1, 1);
        classElement = supplement.getElementByClass(createCoberturaDocument(), panopticodeClass);
        assertEquals("Hello$Inner", classElement.attributeValue("name"));

        panopticodeClass = createClass(panopticodeFile, "Nope", 1, 1);
        try {
            supplement.getElementByClass(createCoberturaDocument(), panopticodeClass);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find class 'Nope' in file 'Hello.java'", e.getMessage());
        }

        panopticodePackage = createPackage(createDummyProject(), "org.nope");
        panopticodeFile = createFile(panopticodePackage, "Nope.java", "Nope.java");
        panopticodeClass = createClass(panopticodeFile, "Nope", 1, 1);
        try {
            supplement.getElementByClass(createCoberturaDocument(), panopticodeClass);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find package 'org.nope'", e.getMessage());
        }
    }
//
    public void testGetElementByMethod() throws Exception {
        Document doc;
        Element methodElement;
        LinkedList<PanopticodeArgument> arguments;
        PanopticodeClass panopticodeClass;
        PanopticodeFile panopticodeFile;
        PanopticodeMethod panopticodeMethod;
        PanopticodePackage panopticodePackage;

        doc = createCoberturaDocument();
        panopticodePackage = createPackage(createDummyProject(), "");
        panopticodeFile = createFile(panopticodePackage, "Hello.java", "Hello.java");
        panopticodeClass = createClass(panopticodeFile, "Hello", 1, 1);

        // one match with no arguments
        arguments = new LinkedList<PanopticodeArgument>();
        panopticodeMethod = createMethod("say", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        methodElement = supplement.getElementByMethod(doc, panopticodeMethod);
        assertEquals("say", methodElement.attributeValue("name"));

        // one match with arguments
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "int", "int"));
        panopticodeMethod = createMethod("sayHello", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        methodElement = supplement.getElementByMethod(doc, panopticodeMethod);
        assertEquals("sayHello", methodElement.attributeValue("name"));

        // no match because of different argument list lengths
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "int", "int"));
        panopticodeMethod = createMethod("say", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        try {
            supplement.getElementByMethod(doc, panopticodeMethod);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find method 'say' in class 'Hello'", e.getMessage());
        }

        // no match because method name not found
        arguments = new LinkedList<PanopticodeArgument>();
        panopticodeMethod = createMethod("nope", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        try {
            supplement.getElementByMethod(doc, panopticodeMethod);
            fail();
        } catch (CouldntFindElementException e) {
            assertEquals("Couldn't find method 'nope' in class 'Hello'", e.getMessage());
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
        methodElement.addAttribute("name", "sayHello");
        methodElement.addAttribute("signature", "(LThing;)");
        methodElement = classElement.addElement("method");
        methodElement.addAttribute("name", "sayHello (Thing): String");
        try {
            supplement.getElementByMethod(doc, panopticodeMethod);
            fail();
        } catch (CouldntFindElementException e) {
//            assertEquals("Couldn't narrow match for method 'Hello.sayHello(org.panopticode.bar.Thing)' from ['sayHello (Thing): String', 'sayHello (Thing): String'] in class 'Hello'", e.getMessage());
        }

        // multiple matches narrowed by exact argument matching
        arguments = new LinkedList<PanopticodeArgument>();
        arguments.add(createArgument("foo", "Thing", "Thing"));
        panopticodeMethod = createMethod("sayHello", arguments, 1, 1);
        panopticodeMethod.setParentClass(panopticodeClass);
        doc = createCoberturaDocument();
        classElement = supplement.getElementByClass(doc, panopticodeClass);
        Element methodsElement = classElement.element("methods");
		methodElement = methodsElement.addElement("method");
        methodElement.addAttribute("name", "sayHello");
        methodElement.addAttribute("signature", "(LTheThing;)Ljava.lang.String");
        methodElement = methodsElement.addElement("method");
        methodElement.addAttribute("name", "sayHello");
        methodElement.addAttribute("signature", "(LThing;)Ljava.lang.String");
        methodElement = supplement.getElementByMethod(doc, panopticodeMethod);
        assertEquals("sayHello", methodElement.attributeValue("name"));

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
        PanopticodePackage pkg;
        PanopticodeFile file;
        
        project = createDummyProject();
        pkg=project.createAndAddPackage("org.norg");
        file = pkg.createAndAddFile("norg/foo.java", "foo.java");
        file.createAndAddClass("Foo", 1, 1);
        
        supplement.loadData(project, new String[] { "sample_data/cobertura.xml" });

        declared = project.getSupplementsDeclared();

        assertEquals(1, declared.size());
        assertEquals(supplement.getClass().getName(), declared.get(0).getSupplementClass());
    }
//
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
        Document doc = createCoberturaDocument();
        supplement.getDeclaration();
        supplement.loadMethodData(panopticodeProject, doc);
        supplement.loadClassData(panopticodeProject, doc);

        
        // ensure no coverage metrics are loaded for interfaces
        assertNull(panopticodeInterfaceClass.getMetricByName("Method Coverage"));
        assertNull(panopticodeInterfaceClass.getMetricByName("Branch Coverage"));
        assertNull(panopticodeInterfaceClass.getMetricByName("Line Coverage"));

        // ensure no coverage metrics are loaded for abstract methods
        assertNull(panopticodeAbstractMethod.getMetricByName("Method Coverage"));
        assertNull(panopticodeAbstractMethod.getMetricByName("Branch Coverage"));
        assertNull(panopticodeAbstractMethod.getMetricByName("Line Coverage"));

        // check correct data is loaded for class
        assertMetricValue(panopticodeClass, "Branch Coverage", 2.0, 2.0);
        assertMetricValue(panopticodeClass, "Line Coverage", 2.0, 2.0);
        
        // check correct data is loaded for method
        assertMetricValue(panopticodeMethod, "Branch Coverage", 0.0, 0.0);
        assertMetricValue(panopticodeMethod, "Line Coverage", 2.0, 2.0);
    }

    private void assertMetricValue(PanopticodeClass panopticodeClass, String metricName,
                                   double numeratorValue, double denominatorValue) {
    	RatioMetric decimalMetric;
        decimalMetric = (RatioMetric) panopticodeClass.getMetricByName(metricName);
        assertEquals(numeratorValue, decimalMetric.getNumeratorValue(), 0.1);
        assertEquals(denominatorValue, decimalMetric.getDenominatorValue(), 0.1);
    }

    private void assertMetricValue(PanopticodeMethod panopticodeMethod, String metricName,
    		double numeratorValue, double denominatorValue) {
    	RatioMetric decimalMetric;

        decimalMetric = (RatioMetric) panopticodeMethod.getMetricByName(metricName);

        assertEquals(numeratorValue, decimalMetric.getNumeratorValue(), 0.1);
        assertEquals(denominatorValue, decimalMetric.getNumeratorValue(), 0.1);
    }

    private Document createCoberturaDocument() {
        Document doc;
        Element packageesElement;
        Element package1Element;
        Element package2Element;
        Element class1Element;
        Element class2Element;
        Element method1Element;
        Element method2Element;
        Element linesElement;
        Element lineElement;
        
        doc = createDummyDocument();
        packageesElement = doc.addElement("coverage").addElement("packages");

        package1Element = packageesElement.addElement("package");
        package1Element.addAttribute("name", "");

        package2Element = packageesElement.addElement("package");
        package2Element.addAttribute("name", "org.panopticode");


        Element classesElement = package1Element.addElement("classes");
		class1Element = classesElement.addElement("class");
        class1Element.addAttribute("name", "Hello");
        class1Element.addAttribute("line-rate", "0.30");
        class1Element.addAttribute("branch-rate", "1.0");
        linesElement = class1Element.addElement("lines");
        addNewLine(linesElement, "10", true, "100% (2/2)");
        addNewLine(linesElement, "10", false, null);
        
        
        class2Element = classesElement.addElement("class");
        class2Element.addAttribute("name", "Hello$Inner");
        class2Element.addAttribute("line-rate", "0.40");
        class2Element.addAttribute("branch-rate", "1.0");
        linesElement = class2Element.addElement("lines");
        addNewLine(linesElement, "10", false, null);
        addNewLine(linesElement, "10", false, null);
        
        Element methodsElement = class1Element.addElement("methods");
		method1Element = methodsElement.addElement("method");
        method1Element.addAttribute("name", "say");
        method1Element.addAttribute("signature", "()V");
        method1Element.addAttribute("line-rate", "0.50");
        method1Element.addAttribute("branch-rate", "1.0");
        linesElement = method1Element.addElement("lines");
        addNewLine(linesElement, "10", false, null);
        addNewLine(linesElement, "10", false, null);
        
        
        method2Element = methodsElement.addElement("method");
        method2Element.addAttribute("name", "sayHello");
        method2Element.addAttribute("signature", "(I)Ljava.lang.String");
        method2Element.addAttribute("line-rate", "0.60");
        method2Element.addAttribute("branch-rate", "1.0");
        linesElement = method2Element.addElement("lines");
        addNewLine(linesElement, "10", false, null);
        addNewLine(linesElement, "10", false, null);
 
        return doc;
    }
    
    void addNewLine(Element linesElement, String hits, boolean branch, String conditionCoverage) {
    	Element lineElement = linesElement.addElement("line");
    	lineElement.addAttribute("hits", hits);
    	lineElement.addAttribute("branch", Boolean.toString(branch));
    	if(branch) {
    		lineElement.addAttribute("condition-coverage", conditionCoverage);
    	}
    }
    
}
