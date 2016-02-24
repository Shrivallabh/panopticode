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

import static org.panopticode.TestHelpers.createDummyElement;
import static org.panopticode.TestHelpers.createDummyMetric;
import static org.panopticode.TestHelpers.createDummyMetricDeclaration;
import static org.panopticode.TestHelpers.createDummyPackage;
import static org.panopticode.TestHelpers.createDummyProject;
import static org.panopticode.TestHelpers.createDummySupplementDeclaration;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class PanopticodeProjectTest extends TestCase {
	private String basePath;
	private String name;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		name = "Web 2.0 Killer App";
		basePath = "/home/killerApp";
	}

	public void testGetMetricByName() {
		PanopticodeProject panopticodeProject;

		Metric metric = createDummyMetric();

		panopticodeProject = new PanopticodeProject(name, basePath);
		assertNull(panopticodeProject.getMetricByName(metric.getName()));

		panopticodeProject.addMetric(metric);
		assertSame(metric, panopticodeProject.getMetricByName(metric.getName()));
	}

	public void testAddingSameMetricTwiceOnlyStoresOnce() {
		PanopticodeProject panopticodeProject = createDummyProject();
		assertEquals(0, panopticodeProject.getMetrics().size());

		panopticodeProject.addMetric(createDummyMetric());
		assertEquals(1, panopticodeProject.getMetrics().size());

		panopticodeProject.addMetric(createDummyMetric());
		assertEquals(1, panopticodeProject.getMetrics().size());
	}

	public void testParent() {
		assertNull(createDummyProject().getParent());
	}

	public void testLevel() {
		PanopticodeProject panopticodeProject = createDummyProject();
		assertEquals(Level.PROJECT, panopticodeProject.getLevel());
		assertTrue(panopticodeProject.isLevel(Level.PROJECT));
	}

	public void testConstructorSetPropertiesToMatchArgumentsPassed() {
		PanopticodeProject panopticodeProject = new PanopticodeProject(name, basePath);
		assertEquals(name, panopticodeProject.getName());
		assertEquals(basePath, panopticodeProject.getBasePath());
	}

	public void testVersion() {
		PanopticodeProject panopticodeProject = createDummyProject();
		String version = "beta 1";

		assertNull(panopticodeProject.getVersion());

		panopticodeProject.setVersion(version);
		assertEquals(version, panopticodeProject.getVersion());
	}

	public void testPackageAddingAndRetrieval() {
		PanopticodePackage anotherDummyPackage;
		PanopticodePackage createdPackage;
		PanopticodePackage dummyPackage;
		PanopticodeProject panopticodeProject;

		panopticodeProject = new PanopticodeProject(name, basePath);

		assertEquals(0, panopticodeProject.getPackages().size());
		assertEquals(0, panopticodeProject.getChildren().size());

		dummyPackage = createDummyPackage();

		createdPackage = panopticodeProject.createAndAddPackage(dummyPackage.getName());

		assertEquals(1, panopticodeProject.getPackages().size());
		assertEquals(1, panopticodeProject.getChildren().size());
		assertEquals(dummyPackage.getName(), createdPackage.getName());

		assertSame(createdPackage, panopticodeProject.createAndAddPackage(dummyPackage.getName()));

		assertEquals(1, panopticodeProject.getPackages().size());
		assertEquals(1, panopticodeProject.getChildren().size());

		anotherDummyPackage = panopticodeProject.createAndAddPackage("foo.bar");

		assertEquals(2, panopticodeProject.getPackages().size());
		assertEquals(2, panopticodeProject.getChildren().size());
		assertEquals("foo.bar", anotherDummyPackage.getName());

		assertSame(createdPackage, panopticodeProject.getPackageByName(createdPackage.getName()));
	}

	public void testgetMetricDeclarations() {
		PanopticodeProject panopticodeProject;
		SupplementDeclaration supplementDeclaration;

		panopticodeProject = createDummyProject();
		assertEquals(0, panopticodeProject.getMetricDeclarations().size());
		assertEquals(0, panopticodeProject.getMetricDeclarations(Level.PROJECT).size());

		supplementDeclaration = createDummySupplementDeclaration();
		supplementDeclaration.addMetricDeclaration(createDummyMetricDeclaration());
		panopticodeProject.addSupplementDeclaration(supplementDeclaration);
		assertEquals(1, panopticodeProject.getMetricDeclarations().size());
		assertEquals(1, panopticodeProject.getMetricDeclarations(Level.PROJECT).size());
		assertEquals(0, panopticodeProject.getMetricDeclarations(Level.PACKAGE).size());
	}

	public void testToXML() {
		Element packageElement;
		Element projectElement;
		Element parentElement;
		PanopticodeProject panopticodeProject;
		String packageName;
		String version;

		parentElement = createDummyElement();
		panopticodeProject = new PanopticodeProject(name, basePath);
		panopticodeProject.toXML(parentElement);

		projectElement = parentElement.element("project");
		assertEquals(name, projectElement.attributeValue("name"));
		assertEquals(null, projectElement.attributeValue("version"));
		assertEquals(0, projectElement.elements("package").size());
		assertEquals(0, projectElement.elements("metric").size());
		assertEquals(0, projectElement.elements("supplement").size());

		packageName = "foo";
		version = "Beta 17";
		panopticodeProject.createAndAddPackage(packageName);
		panopticodeProject.setVersion(version);
		panopticodeProject.addSupplementDeclaration(createDummySupplementDeclaration());
		panopticodeProject.addMetric(createDummyMetric());
		parentElement = createDummyElement();
		panopticodeProject.toXML(parentElement);

		projectElement = parentElement.element("project");
		assertEquals(name, projectElement.attributeValue("name"));
		assertEquals(version, projectElement.attributeValue("version"));
		assertEquals(1, projectElement.elements("supplement").size());
		assertEquals(1, projectElement.elements("package").size());
		packageElement = (Element) projectElement.elements("package").get(0);
		assertEquals(1, projectElement.elements("metric").size());
		assertEquals(packageName, packageElement.attributeValue("name"));
	}

	public void testFromXML() throws DocumentException {
		Element element;
		PanopticodeProject panopticodeProject;
		StringBuffer sb;

		sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<project name='Panopticode' version='1.0'>");
		sb.append("  <supplement class='org.panopticode.supplement.javancss.JavaNCSSSupplement' />");
		sb.append("  <package name='org.panopticode'>");
		sb.append("    <file name='Fubar.java' projectPath='src/Fubar.java'>");
		sb.append("      <class name='Fubar'><filePosition line='1' column='2' />");
		sb.append("        <method name='bar'><filePosition line='5' column='3' /></method>");
		sb.append("      </class>");
		sb.append("    </file>");
		sb.append("  </package>");
		sb.append("</project>");

		element = DocumentHelper.parseText(sb.toString()).getRootElement();

		panopticodeProject = PanopticodeProject.fromXML(element);
		assertEquals("Panopticode", panopticodeProject.getName());
		assertEquals("1.0", panopticodeProject.getVersion());
		assertEquals(1, panopticodeProject.getPackages().size());
		assertEquals("org.panopticode", panopticodeProject.getPackages().get(0).getName());
		assertEquals(1, panopticodeProject.getPackages().get(0).getFiles().size());
		assertEquals("Fubar.java", panopticodeProject.getPackages().get(0).getFiles().get(0).getName());
		assertEquals(1, panopticodeProject.getPackages().get(0).getFiles().get(0).getClasses().size());
		assertEquals("Fubar", panopticodeProject.getPackages().get(0).getFiles().get(0).getClasses().get(0).getName());
		assertEquals(1, panopticodeProject.getPackages().get(0).getFiles().get(0).getClasses().get(0).getMethods().size());
		assertEquals("bar", panopticodeProject.getPackages().get(0).getFiles().get(0).getClasses().get(0).getMethods().get(0).getName());
	}

	public void testGenerateXMLDocument() {
		Document document;
		Element projectElement;
		Element parentElement;
		PanopticodeProject panopticodeProject;

		panopticodeProject = new PanopticodeProject(name, basePath);
		document = panopticodeProject.generateXMLDocument();
		parentElement = document.getRootElement();

		projectElement = parentElement.element("project");
		assertEquals(name, projectElement.attributeValue("name"));
		assertEquals(null, projectElement.attributeValue("version"));
		assertEquals(0, projectElement.elements("package").size());
	}

	public void testGetMethods() {
		PanopticodeProject panopticodeProject;
		PanopticodePackage panopticodePackage;
		PanopticodeFile panopticodeFile;
		PanopticodeClass panopticodeClass;
		PanopticodeMethod panopticodeMethod1;
		PanopticodeMethod panopticodeMethod2;

		panopticodeProject = createDummyProject();
		panopticodePackage = panopticodeProject.createAndAddPackage("foo");
		panopticodeFile = panopticodePackage.createAndAddFile("foo", "foo");
		panopticodeClass = panopticodeFile.createAndAddClass("Foo", 1, 1);
		panopticodeMethod1 = panopticodeClass.createAndAddMethod("one", new LinkedList<PanopticodeArgument>(), 2, 2);
		panopticodeMethod2 = panopticodeClass.createAndAddMethod("two", new LinkedList<PanopticodeArgument>(), 3, 3);

		List<PanopticodeMethod> methods = panopticodeProject.getMethods();
		assertEquals(2, methods.size());
		for (PanopticodeMethod panopticodeMethod : methods)
			if ("one".equals(panopticodeMethod.getName()))
				assertSame(panopticodeMethod1, panopticodeMethod);
			else if ("two".equals(panopticodeMethod.getName()))
				assertSame(panopticodeMethod2, panopticodeMethod);
			else
				fail();
	}

	public void testGetClasses() {
		PanopticodeProject panopticodeProject;
		PanopticodePackage panopticodePackage;
		PanopticodeFile panopticodeFile;
		PanopticodeClass panopticodeClass1;
		PanopticodeClass panopticodeClass2;

		panopticodeProject = createDummyProject();
		panopticodePackage = panopticodeProject.createAndAddPackage("foo");
		panopticodeFile = panopticodePackage.createAndAddFile("foo", "One");
		panopticodeClass1 = panopticodeFile.createAndAddClass("One", 1, 1);
		panopticodeFile = panopticodePackage.createAndAddFile("foo", "Two");
		panopticodeClass2 = panopticodeFile.createAndAddClass("Two", 1, 1);

		List<PanopticodeClass> classes = panopticodeProject.getClasses();
		assertEquals(2, classes.size());
		for (PanopticodeClass panopticodeClass : classes)
			if ("One".equals(panopticodeClass.getName()))
				assertSame(panopticodeClass1, panopticodeClass);
			else if ("Two".equals(panopticodeClass.getName()))
				assertSame(panopticodeClass2, panopticodeClass);
			else
				fail();
	}

	public void testGetFiles() {
		PanopticodeProject panopticodeProject;
		PanopticodePackage panopticodePackage;
		PanopticodeFile panopticodeFile1;
		PanopticodeFile panopticodeFile2;

		panopticodeProject = createDummyProject();
		panopticodePackage = panopticodeProject.createAndAddPackage("foo");
		panopticodeFile1 = panopticodePackage.createAndAddFile("foo", "One");
		panopticodePackage = panopticodeProject.createAndAddPackage("bar");
		panopticodeFile2 = panopticodePackage.createAndAddFile("bar", "Two");

		List<PanopticodeFile> files = panopticodeProject.getFiles();
		assertEquals(2, files.size());
		for (PanopticodeFile panopticodeFile : files)
			if ("One".equals(panopticodeFile.getName()))
				assertSame(panopticodeFile1, panopticodeFile);
			else if ("Two".equals(panopticodeFile.getName()))
				assertSame(panopticodeFile2, panopticodeFile);
			else
				fail();
	}

	public void testGetSupplementsDeclared() {
		PanopticodeProject panopticodeProject;
		SupplementDeclaration supplementDeclaration;

		panopticodeProject = createDummyProject();
		assertEquals(0, panopticodeProject.getSupplementsDeclared().size());

		supplementDeclaration = createDummySupplementDeclaration();
		panopticodeProject.addSupplementDeclaration(supplementDeclaration);
		assertEquals(1, panopticodeProject.getSupplementsDeclared().size());
		assertSame(supplementDeclaration, panopticodeProject.getSupplementsDeclared().get(0));
	}
}
