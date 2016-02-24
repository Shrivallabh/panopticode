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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class TestHelpers {
	public static void assertEqualsAndHashcodeFollowJavaContract(Object object,
																	Object equalObject,
																	Object notEqualObject) {
		assertTrue("object must be equal to itself", object.equals(object));
		assertEquals("object.hashCode did not equal object.hashCode", object.hashCode(), object.hashCode());

		assertTrue("object did not equal equalObject", object.equals(equalObject));
		assertEquals("object.hashCode did not equal equalObject.hashCode", object.hashCode(), equalObject.hashCode());

		// noinspection ObjectEqualsNull
		assertFalse("object should never equal null", object.equals(null));
		assertFalse("object should never equal an object of another type", object.equals(new Comparable<String>() {
			public int compareTo(String s) {
				return 0;
			}
		}));

		assertFalse("object should not equal notEqualObject", object.equals(notEqualObject));
	}

	public static PanopticodePackage createPackage(PanopticodeProject project, String name) {
		return new PanopticodePackage(project, name);
	}

	public static PanopticodeFile createFile(PanopticodePackage parentPackage, String path, String name) {
		return new PanopticodeFile(parentPackage, path, name);
	}

	public static PanopticodeClass createClass(PanopticodeFile panopticodefile, String className, int line, int column) {
		return new PanopticodeClass(panopticodefile, className, line, column);
	}

	public static PanopticodeMethod createMethod(String name,
													List<PanopticodeArgument> arguments,
													int line,
													int column) {
		return new PanopticodeMethod(name, arguments, line, column);
	}

	public static PanopticodeArgument createArgument(String name, String fullyQualifiedType, String simpleType) {
		return new PanopticodeArgument(name, fullyQualifiedType, simpleType);
	}

	public static PanopticodeArgument createDummyArgument() {
		return new PanopticodeArgument("to", "java.lang.String", "String");
	}

	public static List<PanopticodeArgument> createDummyArgumentList() {
		List<PanopticodeArgument> arguments;

		arguments = new LinkedList<PanopticodeArgument>();
		arguments.add(createDummyArgument());

		return arguments;
	}

	public static PanopticodeClass createDummyClass() {
		return new PanopticodeClass(createDummyFile(), "HelloWorld", 7, 3);
	}

	public static PanopticodeFile createDummyFile() {
		return new PanopticodeFile(createDummyPackage(), "src/HelloWorld.java", "HelloWorld.java");
	}

	public static PanopticodeMethod createDummyMethod() {
		return createDummyMethod("sayHello");
	}

	public static PanopticodeMethod createDummyMethod(String methodName) {
		return new PanopticodeMethod(methodName, new LinkedList<PanopticodeArgument>(), 30, 3);
	}

	public static PanopticodePackage createDummyPackage() {
		return new PanopticodePackage(createDummyProject(), "");
	}

	public static Supplement createDummySupplement() {
		return new Supplement() {
			public int optionLength() {
				return 0;
			}

			public void loadData(PanopticodeProject project, String[] arguments) {
			}

			public SupplementDeclaration getDeclaration() {
				return null;
			}
		};
	}

	public static SupplementDeclaration createDummySupplementDeclaration() {
		return new SupplementDeclaration("foo.Supplement");
	}

	public static MetricDeclaration createDummyMetricDeclaration() {
		IntegerMetricDeclaration metricDeclaration = new IntegerMetricDeclaration(createDummySupplement(), "whatever");
		metricDeclaration.addLevel(Level.PROJECT);

		return metricDeclaration;
	}

	public static PanopticodeProject createDummyProject() {
		return new PanopticodeProject("Hello World", "");
	}

	public static Document createDummyDocument() {
		return DocumentHelper.createDocument();
	}

	public static Element createDummyElement() {
		Document document = createDummyDocument();
		return document.addElement("dummy");
	}

	public static Metric createDummyMetric() {
		String name = "CCN";
		MetricDeclaration metricDeclaration = new IntegerMetricDeclaration(null, name);
		return new DummyMetric(metricDeclaration);
	}

	public static void dumpXMLReport(Element element) {
		dumpXMLReport(element.getDocument());
	}

	public static void dumpXMLReport(Document document) {
		PrintStream output = System.out;
		XMLWriter xmlWriter;

		try {
			xmlWriter = new XMLWriter(output, OutputFormat.createPrettyPrint());
			xmlWriter.write(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class DummyMetric extends Metric {
		private boolean addValueCalled = false;

		public DummyMetric(MetricDeclaration metricDeclaration) {
			super(metricDeclaration);
		}

		@Override
		public void addValue(Element metricElement) {
			addValueCalled = true;
		}

		@Override
		public String getStringValue() {
			return null;
		}

		public boolean addValueCalled() {
			return addValueCalled;
		}
	}
}
