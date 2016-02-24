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

import static org.panopticode.util.ArgumentValidation.failIfEmpty;
import static org.panopticode.util.ArgumentValidation.failIfNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class PanopticodeClass implements MetricTarget {
	private int positionColumn;
	private int positionLine;
	private Map<String, Metric> metricsByName = new HashMap<String, Metric>();
	private List<PanopticodeMethod> methodList = new LinkedList<PanopticodeMethod>();
	private Map<String, List<PanopticodeMethod>> methodListByName = new HashMap<String, List<PanopticodeMethod>>();
	private PanopticodeFile parentFile;
	private String name;
	private boolean isAbstract = false;
	private boolean isEnum = false;
	private boolean isInterface = false;
	private boolean isStatic = false;

	PanopticodeClass(PanopticodeFile panopticodefile, String className, int line, int column) {
		failIfNull(panopticodefile, "panopticodefile");
		failIfEmpty(className, "className");

		parentFile = panopticodefile;
		name = className;
		positionLine = line;
		positionColumn = column;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public PanopticodeFile getParentFile() {
		return parentFile;
	}

	public List<PanopticodeMethod> getMethods() {
		return Collections.unmodifiableList(methodList);
	}

	public String getName() {
		return name;
	}

	public String getFullyQualifiedName() {
		if (getPackage().isDefaultPackage())
			return name;
		else
			return getPackage().getName() + "." + name;
	}

	public int getPositionColumn() {
		return positionColumn;
	}

	public int getPositionLine() {
		return positionLine;
	}

	public void toXML(Element parentElement) {
		Element classElement = null;

		classElement = parentElement.addElement("class");

		classElement.addAttribute("name", name);

		if (isEnum)
			classElement.addAttribute("enum", "true");
		if (isInterface)
			classElement.addAttribute("interface", "true");
		if (isStatic)
			classElement.addAttribute("static", "true");
		if (isAbstract)
			classElement.addAttribute("abstract", "true");

		Element positionElement = classElement.addElement("filePosition");
		positionElement.addAttribute("line", String.valueOf(positionLine));
		positionElement.addAttribute("column", String.valueOf(positionColumn));

		for (Metric metric : getMetrics())
			metric.toXML(classElement);

		for (PanopticodeMethod panopticodeMethod : methodList)
			panopticodeMethod.toXML(classElement);
	}

	public static PanopticodeClass fromXML(Element classElement,
											PanopticodeFile parentFile,
											PanopticodeProject project) {
		int theColumn;
		int theLine;
		Element positionElement;
		PanopticodeClass panopticodeClass;
		String theName;

		theName = classElement.attributeValue("name");
		positionElement = classElement.element("filePosition");
		theLine = Integer.valueOf(positionElement.attributeValue("line"));
		theColumn = Integer.valueOf(positionElement.attributeValue("column"));

		panopticodeClass = new PanopticodeClass(parentFile, theName, theLine, theColumn);
		panopticodeClass.setEnum("true".equals(classElement.attributeValue("enum")));
		panopticodeClass.setInterface("true".equals(classElement.attributeValue("interface")));
		panopticodeClass.setStatic("true".equals(classElement.attributeValue("static")));
		panopticodeClass.setAbstract("true".equals(classElement.attributeValue("abstract")));

		for (Object methodElement : classElement.elements("method")) {
			PanopticodeMethod panopticodeMethod = PanopticodeMethod.fromXML((Element) methodElement, project);
			panopticodeClass.addMethod(panopticodeMethod);
		}

		MetricDeclaration.loadAllMetricsFromXML(classElement, panopticodeClass, project);

		return panopticodeClass;
	}

	public PanopticodeMethod createAndAddMethod(String methodName,
												List<PanopticodeArgument> arguments,
												int line,
												int column) {
		PanopticodeMethod panopticodeMethod;

		panopticodeMethod = getMethodByNameAndArguments(methodName, arguments);

		if (panopticodeMethod == null) {
			panopticodeMethod = new PanopticodeMethod(methodName, arguments, line, column);
			addMethod(panopticodeMethod);
		}

		return panopticodeMethod;
	}

	private void addMethod(PanopticodeMethod panopticodeMethod) {
		if (methodList.contains(panopticodeMethod))
			return;
		List<PanopticodeMethod> methodListWithName = getOrCreateMethodListByName(panopticodeMethod.getName());
		methodList.add(panopticodeMethod);
		methodListWithName.add(panopticodeMethod);
		panopticodeMethod.setParentClass(this);
	}

	private List<PanopticodeMethod> getOrCreateMethodListByName(String methodName) {
		if (methodListByName.containsKey(methodName))
			return methodListByName.get(methodName);

		List<PanopticodeMethod> panopticodeMethodList = new LinkedList<PanopticodeMethod>();
		methodListByName.put(methodName, panopticodeMethodList);
		return panopticodeMethodList;
	}

	private PanopticodeMethod getMethodByNameAndArguments(String methodName,
															List<PanopticodeArgument> arguments) {
		List<PanopticodeMethod> methodsWithName;

		methodsWithName = methodListByName.get(methodName);

		if (methodsWithName == null)
			return null;

		for (PanopticodeMethod panopticodeMethod : methodsWithName)
			if (arguments.equals(panopticodeMethod.getArguments()))
				return panopticodeMethod;

		return null;
	}

	public void addMetric(Metric metric) {
		if (!metricsByName.containsKey(metric.getName()))
			metricsByName.put(metric.getName(), metric);
	}

	public Collection<MetricTarget> getChildren() {
		return new LinkedList<MetricTarget>(methodList);
	}

	public Level getLevel() {
		return Level.CLASS;
	}

	public boolean isAnonymousInnerClass() {
		return name.indexOf("$") != -1;
	}

	public boolean isInnerClass() {
		return name.indexOf(".") != -1 || name.indexOf("$") != -1;
	}

	public PanopticodePackage getPackage() {
		return parentFile.getParentPackage();
	}

	public Metric getMetricByName(String metricName) {
		return metricsByName.get(metricName);
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public Collection<Metric> getMetrics() {
		return metricsByName.values();
	}

	public MetricTarget getParent() {
		return parentFile;
	}

	public boolean isLevel(Level toCheck) {
		return getLevel().equals(toCheck);
	}
}
