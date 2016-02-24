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

import static org.panopticode.TestHelpers.createDummyClass;
import static org.panopticode.TestHelpers.createDummyElement;
import static org.panopticode.TestHelpers.createDummyFile;
import static org.panopticode.TestHelpers.createDummyMethod;
import static org.panopticode.TestHelpers.createDummyMetric;
import static org.panopticode.TestHelpers.createDummyProject;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class PanopticodeClassTest extends TestCase {
	private int column;
	private int line;
	private PanopticodeFile panopticodeFile;
	private String name;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		panopticodeFile = createDummyFile();
		name = "foo";
		line = 42;
		column = 3;
	}

	public void testLevel() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);

		assertEquals(Level.CLASS, panopticodeClass.getLevel());
		assertTrue(panopticodeClass.isLevel(Level.CLASS));
	}

	public void testAddingSameMetricTwiceOnlyStoresOnce() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = createDummyClass();
		assertEquals(0, panopticodeClass.getMetrics().size());

		panopticodeClass.addMetric(createDummyMetric());
		assertEquals(1, panopticodeClass.getMetrics().size());

		panopticodeClass.addMetric(createDummyMetric());
		assertEquals(1, panopticodeClass.getMetrics().size());
	}

	public void testConstructorSetPropertiesToMatchArgumentsPassed() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertEquals(panopticodeFile, panopticodeClass.getParentFile());
		assertEquals(panopticodeFile, panopticodeClass.getParent());
		assertEquals(name, panopticodeClass.getName());
		assertEquals(line, panopticodeClass.getPositionLine());
		assertEquals(column, panopticodeClass.getPositionColumn());
	}

	public void testConstructorFailsWithNullFile() {
		try {
			new PanopticodeClass(null, name, line, column);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Argument 'panopticodefile' may not be null.", e.getMessage());
		}
	}

	public void testConstructorFailsWithNullOrEmptyName() {
		assertIllegalArgumentExceptionThrownWhenConstructedWithName(null);
		assertIllegalArgumentExceptionThrownWhenConstructedWithName("");
		assertIllegalArgumentExceptionThrownWhenConstructedWithName(" ");
		assertIllegalArgumentExceptionThrownWhenConstructedWithName("\t");
	}

	public void testToXML() {
		Element classElement;
		Element parentElement;
		Element positionElement;
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		parentElement = createDummyElement();

		panopticodeClass.toXML(parentElement);

		classElement = parentElement.element("class");
		assertEquals(name, classElement.attributeValue("name"));
		assertNull(classElement.attributeValue("enum"));

		positionElement = classElement.element("filePosition");
		assertEquals(String.valueOf(line), positionElement.attributeValue("line"));
		assertEquals(String.valueOf(column), positionElement.attributeValue("column"));
		assertEquals(0, classElement.elements("metric").size());
		assertEquals(0, classElement.elements("method").size());

		// now as an enum and a metric
		panopticodeClass.setEnum(true);
		panopticodeClass.addMetric(createDummyMetric());
		PanopticodeMethod panopticodeMethod = createDummyMethod();
		panopticodeClass.createAndAddMethod(panopticodeMethod.getName(),
											panopticodeMethod.getArguments(),
											panopticodeMethod.getPositionLine(),
											panopticodeMethod.getPositionColumn());
		parentElement = createDummyElement();

		panopticodeClass.toXML(parentElement);

		classElement = parentElement.element("class");
		assertEquals("true", classElement.attributeValue("enum"));
		assertEquals(1, classElement.elements("metric").size());
		assertEquals(1, classElement.elements("method").size());

		// now static
		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		panopticodeClass.setStatic(true);

		parentElement = createDummyElement();
		panopticodeClass.toXML(parentElement);

		classElement = parentElement.element("class");
		assertEquals("true", classElement.attributeValue("static"));

		// now as an interface
		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		panopticodeClass.setInterface(true);

		parentElement = createDummyElement();
		panopticodeClass.toXML(parentElement);

		classElement = parentElement.element("class");
		assertEquals("true", classElement.attributeValue("interface"));

		// now abstract
		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		panopticodeClass.setAbstract(true);

		parentElement = createDummyElement();
		panopticodeClass.toXML(parentElement);

		classElement = parentElement.element("class");
		assertEquals("true", classElement.attributeValue("abstract"));

	}

	public void testFromXML() throws DocumentException {
		Element element;
		PanopticodeClass panopticodeClass;
		StringBuffer sb;

		sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<class name='Fubar' interface='true'><filePosition line='1' column='2' />");
		sb.append("  <method name='bar' constructor='true'><filePosition line='5' column='3' /></method>");
		sb.append("</class>");

		element = DocumentHelper.parseText(sb.toString()).getRootElement();

		panopticodeClass = PanopticodeClass.fromXML(element, createDummyFile(), createDummyProject());
		assertEquals("Fubar", panopticodeClass.getName());
		assertFalse(panopticodeClass.isEnum());
		assertTrue(panopticodeClass.isInterface());
		assertEquals(1, panopticodeClass.getPositionLine());
		assertEquals(2, panopticodeClass.getPositionColumn());
		assertEquals(1, panopticodeClass.getMethods().size());
		assertEquals("bar", panopticodeClass.getMethods().get(0).getName());
	}

	public void testFromXMLDoesNotAddDuplicateMethods() throws DocumentException {
		Element element;
		PanopticodeClass panopticodeClass;
		StringBuffer sb;

		sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<class name='Fubar' interface='true'><filePosition line='1' column='2' />");
		sb.append("  <method name='bar' constructor='true'><filePosition line='5' column='3' /></method>");
		sb.append("  <method name='bar' constructor='true'><filePosition line='5' column='3' /></method>");
		sb.append("</class>");

		element = DocumentHelper.parseText(sb.toString()).getRootElement();

		panopticodeClass = PanopticodeClass.fromXML(element, createDummyFile(), createDummyProject());
		assertEquals("Fubar", panopticodeClass.getName());
		assertFalse(panopticodeClass.isEnum());
		assertTrue(panopticodeClass.isInterface());
		assertEquals(1, panopticodeClass.getPositionLine());
		assertEquals(2, panopticodeClass.getPositionColumn());
		assertEquals(1, panopticodeClass.getMethods().size());
		assertEquals("bar", panopticodeClass.getMethods().get(0).getName());
	}

	public void testMethodAddingAndRetrieval() {
		List<PanopticodeArgument> arguments;
		PanopticodeMethod anotherDummyMethod;
		PanopticodeMethod createdMethod;
		PanopticodeMethod dummyMethod;
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);

		assertEquals(0, panopticodeClass.getMethods().size());
		assertEquals(0, panopticodeClass.getChildren().size());

		dummyMethod = createDummyMethod();

		createdMethod = panopticodeClass.createAndAddMethod(dummyMethod.getName(),
															dummyMethod.getArguments(),
															dummyMethod.getPositionLine(),
															dummyMethod.getPositionColumn());

		assertEquals(1, panopticodeClass.getMethods().size());
		assertEquals(1, panopticodeClass.getChildren().size());
		assertEquals(dummyMethod.getName(), createdMethod.getName());
		assertEquals(dummyMethod.getArguments(), createdMethod.getArguments());

		assertSame(createdMethod, panopticodeClass.createAndAddMethod(dummyMethod.getName(),
																		dummyMethod.getArguments(),
																		dummyMethod.getPositionLine(),
																		dummyMethod.getPositionColumn()));

		assertEquals(1, panopticodeClass.getMethods().size());
		assertEquals(1, panopticodeClass.getChildren().size());

		arguments = new LinkedList<PanopticodeArgument>(dummyMethod.getArguments());
		arguments.add(new PanopticodeArgument("sayTo", "java.lang.String", "String"));
		anotherDummyMethod = panopticodeClass.createAndAddMethod(dummyMethod.getName(),
																	arguments,
																	dummyMethod.getPositionLine() + 10,
																	dummyMethod.getPositionColumn());

		assertEquals(2, panopticodeClass.getMethods().size());
		assertEquals(2, panopticodeClass.getChildren().size());
		assertEquals(dummyMethod.getName(), anotherDummyMethod.getName());
		assertEquals(0, dummyMethod.getArguments().size());
		assertEquals(1, anotherDummyMethod.getArguments().size());
	}

	private void assertIllegalArgumentExceptionThrownWhenConstructedWithName(String name) {
		try {
			new PanopticodeClass(panopticodeFile, name, line, column);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Argument 'className' may not be null or empty.", e.getMessage());
		}
	}

	public void testGetSetEnum() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertFalse(panopticodeClass.isEnum());

		panopticodeClass.setEnum(true);
		assertTrue(panopticodeClass.isEnum());

		panopticodeClass.setEnum(false);
		assertFalse(panopticodeClass.isEnum());
	}

	public void testGetSetInterface() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertFalse(panopticodeClass.isInterface());

		panopticodeClass.setInterface(true);
		assertTrue(panopticodeClass.isInterface());

		panopticodeClass.setInterface(false);
		assertFalse(panopticodeClass.isInterface());
	}

	public void testGetSetStatic() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertFalse(panopticodeClass.isStatic());

		panopticodeClass.setStatic(true);
		assertTrue(panopticodeClass.isStatic());

		panopticodeClass.setStatic(false);
		assertFalse(panopticodeClass.isStatic());
	}

	public void testGetSetAbstract() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertFalse(panopticodeClass.isAbstract());

		panopticodeClass.setAbstract(true);
		assertTrue(panopticodeClass.isAbstract());

		panopticodeClass.setAbstract(false);
		assertFalse(panopticodeClass.isAbstract());
	}

	public void testIsAnonymousInnerClass() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertFalse(panopticodeClass.isAnonymousInnerClass());

		panopticodeClass = new PanopticodeClass(panopticodeFile, name + "$Foo", line, column);
		assertTrue(panopticodeClass.isAnonymousInnerClass());
	}

	public void testIsInnerClass() {
		PanopticodeClass panopticodeClass;

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertFalse(panopticodeClass.isInnerClass());

		panopticodeClass = new PanopticodeClass(panopticodeFile, name + ".Foo", line, column);
		assertTrue(panopticodeClass.isInnerClass());
	}

	public void testGetFullyQualifiedName() {
		PanopticodePackage panopticodePackage;
		PanopticodeClass panopticodeClass;

		panopticodePackage = new PanopticodePackage(createDummyProject(), "");
		panopticodeFile = new PanopticodeFile(panopticodePackage, "src/HelloWorld.java", "HelloWorld.java");
		panopticodeClass = new PanopticodeClass(panopticodeFile, "HelloWorld", line, column);
		assertEquals("HelloWorld", panopticodeClass.getFullyQualifiedName());

		panopticodePackage = new PanopticodePackage(createDummyProject(), "blah");
		panopticodeFile = new PanopticodeFile(panopticodePackage, "src/HelloWorld.java", "HelloWorld.java");
		panopticodeClass = new PanopticodeClass(panopticodeFile, "HelloWorld", line, column);
		assertEquals("blah.HelloWorld", panopticodeClass.getFullyQualifiedName());
	}

	public void testGetMetricByName() {
		PanopticodeClass panopticodeClass;
		Metric metric;

		metric = createDummyMetric();

		panopticodeClass = new PanopticodeClass(panopticodeFile, name, line, column);
		assertNull(panopticodeClass.getMetricByName(metric.getName()));

		panopticodeClass.addMetric(metric);
		assertSame(metric, panopticodeClass.getMetricByName(metric.getName()));
	}
}
