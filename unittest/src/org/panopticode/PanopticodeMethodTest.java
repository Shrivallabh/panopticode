/*
 * Copyright (c) 2006-2007 Julias R. Shaw Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and
 * this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED
 * "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.panopticode;

import static org.panopticode.TestHelpers.createDummyArgument;
import static org.panopticode.TestHelpers.createDummyArgumentList;
import static org.panopticode.TestHelpers.createDummyClass;
import static org.panopticode.TestHelpers.createDummyElement;
import static org.panopticode.TestHelpers.createDummyMethod;
import static org.panopticode.TestHelpers.createDummyMetric;
import static org.panopticode.TestHelpers.createDummyProject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.panopticode.supplement.javancss.JavaNCSSSupplement;

public class PanopticodeMethodTest extends TestCase {
	int goodColumn;
	int goodLine;
	List<PanopticodeArgument> goodArguments;
	String goodName;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		PanopticodeMethod dummyMethod = createDummyMethod();
		goodName = dummyMethod.getName();
		goodArguments = dummyMethod.getArguments();
		goodLine = dummyMethod.getPositionLine();
		goodColumn = dummyMethod.getPositionColumn();
	}

	public void testAddingSameMetricTwiceOnlyStoresOnce() {
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = createDummyMethod();
		assertEquals(0, panopticodeMethod.getMetrics().size());

		panopticodeMethod.addMetric(createDummyMetric());
		assertEquals(1, panopticodeMethod.getMetrics().size());

		panopticodeMethod.addMetric(createDummyMetric());
		assertEquals(1, panopticodeMethod.getMetrics().size());
	}

	public void testChildren() {
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = createDummyMethod();

		assertEquals(0, panopticodeMethod.getChildren().size());
	}

	public void testLevel() {
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = createDummyMethod();

		assertEquals(Level.METHOD, panopticodeMethod.getLevel());
		assertTrue(panopticodeMethod.isLevel(Level.METHOD));
	}

	public void testConstructorFailsWhenRequiredValuesNotSet() {
		assertNotNull(new PanopticodeMethod(goodName, goodArguments, goodLine, goodColumn));

		assertConstructorFailsOnRequiredArgumentsThatAreEmpty(null);
		assertConstructorFailsOnRequiredArgumentsThatAreEmpty("");
		assertConstructorFailsOnRequiredArgumentsThatAreEmpty(" ");
		assertConstructorFailsOnRequiredArgumentsThatAreEmpty("\t");

		try {
			new PanopticodeMethod(goodName, null, goodLine, goodColumn);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Argument 'arguments' may not be null.", e.getMessage());
		}

		assertConstructorFailsOnRequiredArgumentsThatAreNotPositive(0, goodColumn);
		assertConstructorFailsOnRequiredArgumentsThatAreNotPositive(-1, goodColumn);
		assertConstructorFailsOnRequiredArgumentsThatAreNotPositive(Integer.MIN_VALUE, goodColumn);

		assertConstructorFailsOnRequiredArgumentsThatAreNotPositive(goodLine, 0);
		assertConstructorFailsOnRequiredArgumentsThatAreNotPositive(goodLine, -1);
		assertConstructorFailsOnRequiredArgumentsThatAreNotPositive(goodLine, Integer.MIN_VALUE);
	}

	private void assertConstructorFailsOnRequiredArgumentsThatAreNotPositive(int line, int column) {
		try {
			new PanopticodeMethod(goodName, goodArguments, line, column);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().startsWith("Argument '"));
			assertTrue(e.getMessage().endsWith("' may not be zero or negative."));
		}
	}

	private void assertConstructorFailsOnRequiredArgumentsThatAreEmpty(String badName) {
		try {
			new PanopticodeMethod(badName, goodArguments, goodLine, goodColumn);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Argument 'name' may not be null or empty.", e.getMessage());
		}
	}

	public void testConstructorSetsPropertiesToMatchArgumentsPassed() {
		PanopticodeMethod panopticodeMethod;

		List<PanopticodeArgument> arguments;
		arguments = new LinkedList<PanopticodeArgument>();
		arguments.add(createDummyArgument());

		panopticodeMethod = new PanopticodeMethod(goodName, arguments, goodLine, goodColumn);
		assertEquals(goodName, panopticodeMethod.getName());
		assertEquals(arguments, panopticodeMethod.getArguments());
		assertEquals(goodLine, panopticodeMethod.getPositionLine());
		assertEquals(goodColumn, panopticodeMethod.getPositionColumn());
	}

	public void testIsConstructorSetConstructor() {
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = new PanopticodeMethod(goodName, goodArguments, goodLine, goodColumn);
		assertFalse(panopticodeMethod.isConstructor());

		panopticodeMethod.setConstructor(true);
		assertTrue(panopticodeMethod.isConstructor());

		panopticodeMethod.setConstructor(false);
		assertFalse(panopticodeMethod.isConstructor());
	}

	public void testAbstract() {
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = new PanopticodeMethod(goodName, goodArguments, goodLine, goodColumn);
		assertFalse(panopticodeMethod.isAbstract());

		panopticodeMethod.setAbstract(true);
		assertTrue(panopticodeMethod.isAbstract());

		panopticodeMethod.setAbstract(false);
		assertFalse(panopticodeMethod.isAbstract());
	}

	public void testToXML() {
		Element methodElement;
		Element parentElement;
		Element filePositionELement;
		List<PanopticodeArgument> arguments;
		PanopticodeMethod panopticodeMethod;

		parentElement = createDummyElement();
		panopticodeMethod = new PanopticodeMethod(goodName, goodArguments, goodLine, goodColumn);
		panopticodeMethod.toXML(parentElement);

		methodElement = parentElement.element("method");
		assertEquals(goodName, methodElement.attributeValue("name"));
		assertNull(methodElement.attribute("constructor"));
		assertEquals(0, methodElement.elements("metric").size());
		assertEquals(0, methodElement.elements("argument").size());
		filePositionELement = methodElement.element("filePosition");
		assertEquals("" + goodLine, filePositionELement.attributeValue("line"));
		assertEquals("" + goodColumn, filePositionELement.attributeValue("column"));

		// now try one with a constructor, argument, and metric
		arguments = createDummyArgumentList();
		panopticodeMethod = new PanopticodeMethod(goodName, arguments, goodLine, goodColumn);
		panopticodeMethod.setConstructor(true);
		panopticodeMethod.addMetric(createDummyMetric());

		parentElement = createDummyElement();
		panopticodeMethod.toXML(parentElement);

		methodElement = parentElement.element("method");
		assertEquals("true", methodElement.attributeValue("constructor"));
		assertEquals(1, methodElement.elements("argument").size());
		assertEquals(1, methodElement.elements("metric").size());

		// now try one with an abstract method
		arguments = createDummyArgumentList();
		panopticodeMethod = new PanopticodeMethod(goodName, arguments, goodLine, goodColumn);
		panopticodeMethod.setAbstract(true);

		parentElement = createDummyElement();
		panopticodeMethod.toXML(parentElement);

		methodElement = parentElement.element("method");
		assertEquals("true", methodElement.attributeValue("abstract"));
	}

	public void testFromXML() throws DocumentException {
		Element element;
		PanopticodeMethod panopticodeMethod;
		StringBuffer sb;

		sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<method name='bar' constructor='true'><filePosition line='5' column='3' /></method>");

		element = DocumentHelper.parseText(sb.toString()).getRootElement();

		panopticodeMethod = PanopticodeMethod.fromXML(element, createDummyProject());
		assertEquals("bar", panopticodeMethod.getName());
		assertFalse(panopticodeMethod.isAbstract());
		assertTrue(panopticodeMethod.isConstructor());
		assertEquals(5, panopticodeMethod.getPositionLine());
		assertEquals(3, panopticodeMethod.getPositionColumn());
		assertEquals(0, panopticodeMethod.getArguments().size());

		sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<method name='blah' abstract='true'>");
		sb.append("  <filePosition line='15' column='2' />");
		sb.append("  <argument name='bar' type='int' simpleType='int' />");
		sb.append("  <metric name='CCN' value='2' />");
		sb.append("  <metric name='NCSS' value='4' />");
		sb.append("</method>");

		element = DocumentHelper.parseText(sb.toString()).getRootElement();

		PanopticodeProject project = createDummyProject();
		project.addSupplementDeclaration(new JavaNCSSSupplement().getDeclaration());

		panopticodeMethod = PanopticodeMethod.fromXML(element, project);
		assertEquals("blah", panopticodeMethod.getName());
		assertTrue(panopticodeMethod.isAbstract());
		assertFalse(panopticodeMethod.isConstructor());
		assertEquals(15, panopticodeMethod.getPositionLine());
		assertEquals(2, panopticodeMethod.getPositionColumn());
		assertEquals(1, panopticodeMethod.getArguments().size());
		assertEquals("2", panopticodeMethod.getMetricByName("CCN").getStringValue());
		assertEquals("4", panopticodeMethod.getMetricByName("NCSS").getStringValue());
		assertEquals("bar", panopticodeMethod.getArguments().get(0).getName());
	}

	public void testGetSetParentClass() {
		PanopticodeClass panopticodeClass;
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = new PanopticodeMethod(goodName, goodArguments, goodLine, goodColumn);
		assertNull(panopticodeMethod.getParentClass());

		panopticodeClass = createDummyClass();
		panopticodeMethod.setParentClass(panopticodeClass);
		assertEquals(panopticodeClass, panopticodeMethod.getParentClass());
		assertEquals(panopticodeClass, panopticodeMethod.getParent());
	}

	public void testGetFullyQualifiedName() {
		PanopticodeClass panopticodeClass;
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = new PanopticodeMethod(goodName, goodArguments, goodLine, goodColumn);

		panopticodeClass = createDummyClass();
		panopticodeMethod.setParentClass(panopticodeClass);
		assertEquals("HelloWorld.sayHello", panopticodeMethod.getFullyQualifiedName());
	}

	public void testGetMetricByName() {
		Metric metric;
		PanopticodeMethod panopticodeMethod;

		metric = createDummyMetric();

		panopticodeMethod = new PanopticodeMethod(goodName, goodArguments, goodLine, goodColumn);
		assertNull(panopticodeMethod.getMetricByName(metric.getName()));

		panopticodeMethod.addMetric(metric);
		assertSame(metric, panopticodeMethod.getMetricByName(metric.getName()));
	}

	public void testGetSignature() {
		PanopticodeClass panopticodeClass;
		PanopticodeMethod panopticodeMethod;
		PanopticodePackage panopticodePackage;
		PanopticodeFile panopticodeFile;
		List<PanopticodeArgument> arguments;

		panopticodePackage = new PanopticodePackage(createDummyProject(), "");
		panopticodeFile = new PanopticodeFile(panopticodePackage, "foo", "HelloWorld.java");
		panopticodeClass = new PanopticodeClass(panopticodeFile, "HelloWorld", 1, 1);
		arguments = new LinkedList<PanopticodeArgument>();

		panopticodeMethod = new PanopticodeMethod(goodName, arguments, goodLine, goodColumn);
		panopticodeMethod.setParentClass(panopticodeClass);

		assertEquals("HelloWorld.sayHello()", panopticodeMethod.getSignature());
		assertEquals("sayHello()", panopticodeMethod.getShortSignature());

		panopticodePackage = new PanopticodePackage(createDummyProject(), "foo");
		panopticodeFile = new PanopticodeFile(panopticodePackage, "foo", "HelloWorld.java");
		panopticodeClass = new PanopticodeClass(panopticodeFile, "HelloWorld", 1, 1);
		arguments = new LinkedList<PanopticodeArgument>();

		panopticodeMethod = new PanopticodeMethod(goodName, arguments, goodLine, goodColumn);
		panopticodeMethod.setParentClass(panopticodeClass);

		assertEquals("foo.HelloWorld.sayHello()", panopticodeMethod.getSignature());
		assertEquals("sayHello()", panopticodeMethod.getShortSignature());

		panopticodePackage = new PanopticodePackage(createDummyProject(), "foo");
		panopticodeFile = new PanopticodeFile(panopticodePackage, "foo", "HelloWorld.java");
		panopticodeClass = new PanopticodeClass(panopticodeFile, "HelloWorld", 1, 1);
		arguments = new LinkedList<PanopticodeArgument>();
		arguments.add(new PanopticodeArgument("one", "int", "int"));
		arguments.add(new PanopticodeArgument("two", "java.lang.String", "String"));

		panopticodeMethod = new PanopticodeMethod(goodName, arguments, goodLine, goodColumn);
		panopticodeMethod.setParentClass(panopticodeClass);

		assertEquals("foo.HelloWorld.sayHello(int, java.lang.String)", panopticodeMethod.getSignature());
		assertEquals("sayHello(int, String)", panopticodeMethod.getShortSignature());
	}

	public void testCollectionContainsMethodWorks() {
		PanopticodeMethod method = createDummyMethod("hello");
		PanopticodeMethod anotherMethod = createDummyMethod("anotherHello");
		assertFalse(method.equals(anotherMethod));
		List<PanopticodeMethod> methods = new ArrayList<PanopticodeMethod>();
		methods.add(method);
		assertTrue(methods.contains(method));
		assertFalse(methods.contains(anotherMethod));
		assertTrue(methods.contains(createDummyMethod("hello")));
	}
}
