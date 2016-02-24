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
import static org.panopticode.util.ArgumentValidation.failIfNotPositive;
import static org.panopticode.util.ArgumentValidation.failIfNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class PanopticodeMethod implements MetricTarget {
	private static Collection<MetricTarget> noChildren = Collections.unmodifiableList(new LinkedList<MetricTarget>());

	private boolean isConstructor = false;
	private int positionColumn;
	private int positionLine;
	private Map<String, Metric> metricMap = new HashMap<String, Metric>();
	private List<PanopticodeArgument> arguments;
	private String name;
	private PanopticodeClass panopticodeClass;
	private boolean isAbstract = false;

	PanopticodeMethod(String name,
						List<PanopticodeArgument> arguments,
						int line,
						int column) {
		failIfEmpty(name, "name");
		failIfNull(arguments, "arguments");
		failIfNotPositive(line, "line");
		failIfNotPositive(column, "column");

		this.name = name;
		this.arguments = new LinkedList<PanopticodeArgument>(arguments);
		for (PanopticodeArgument argument : this.arguments)
			argument.setParentMethod(this);
		positionColumn = column;
		positionLine = line;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public void setParentClass(PanopticodeClass panopticodeClass) {
		this.panopticodeClass = panopticodeClass;
	}

	public PanopticodeClass getParentClass() {
		return panopticodeClass;
	}

	public List<PanopticodeArgument> getArguments() {
		return Collections.unmodifiableList(arguments);
	}

	public String getName() {
		return name;
	}

	public String getFullyQualifiedName() {
		return panopticodeClass.getFullyQualifiedName() + "." + name;
	}

	public int getPositionColumn() {
		return positionColumn;
	}

	public int getPositionLine() {
		return positionLine;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public void toXML(Element parentElement) {
		Element methodElement = null;

		methodElement = parentElement.addElement("method");

		methodElement.addAttribute("name", name);
		if (isConstructor())
			methodElement.addAttribute("constructor", "true");
		if (isAbstract())
			methodElement.addAttribute("abstract", "true");

		for (PanopticodeArgument panopticodeArgument : arguments)
			panopticodeArgument.toXML(methodElement);

		Element positionElement = methodElement.addElement("filePosition");
		positionElement.addAttribute("line", String.valueOf(positionLine));
		positionElement.addAttribute("column", String.valueOf(positionColumn));

		for (Metric metric : metricMap.values())
			metric.toXML(methodElement);
	}

	public static PanopticodeMethod fromXML(Element methodElement, PanopticodeProject project) {
		int theColumn;
		int theLine;
		Element positionElement;
		List<PanopticodeArgument> theArguments;
		PanopticodeMethod panopticodeMethod;
		String theName;

		theName = methodElement.attributeValue("name");
		theArguments = new LinkedList<PanopticodeArgument>();
		for (Object argumentElement : methodElement.elements("argument"))
			theArguments.add(PanopticodeArgument.fromXML((Element) argumentElement));
		positionElement = methodElement.element("filePosition");
		theLine = Integer.valueOf(positionElement.attributeValue("line"));
		theColumn = Integer.valueOf(positionElement.attributeValue("column"));

		panopticodeMethod = new PanopticodeMethod(theName, theArguments, theLine, theColumn);
		panopticodeMethod.setAbstract("true".equals(methodElement.attributeValue("abstract")));
		panopticodeMethod.setConstructor("true".equals(methodElement.attributeValue("constructor")));

		MetricDeclaration.loadAllMetricsFromXML(methodElement, panopticodeMethod, project);

		return panopticodeMethod;
	}

	public void addMetric(Metric metric) {
		if (!metricMap.containsKey(metric.getName()))
			metricMap.put(metric.getMetricDeclaration().getName(), metric);
	}

	public Collection<MetricTarget> getChildren() {
		return noChildren;
	}

	public Level getLevel() {
		return Level.METHOD;
	}

	public Metric getMetricByName(String name) {
		return metricMap.get(name);
	}

	public String getSignature() {
		StringBuffer sb = new StringBuffer();
		if (panopticodeClass != null) {
			String packageName = panopticodeClass.getParentFile().getParentPackage().getName();
			if (packageName.length() > 0) {
				sb.append(packageName);
				sb.append(".");
			}
			sb.append(panopticodeClass.getName());
			sb.append(".");
		}
		sb.append(name);
		sb.append("(");
		boolean firstTime = true;
		for (PanopticodeArgument panopticodeArgument : arguments) {
			if (!firstTime)
				sb.append(", ");
			firstTime = false;
			sb.append(panopticodeArgument.getFullyQualifiedType());
		}
		sb.append(")");
		return sb.toString();
	}

	public String getShortSignature() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("(");
		boolean firstTime = true;
		for (PanopticodeArgument panopticodeArgument : arguments) {
			if (!firstTime)
				sb.append(", ");
			firstTime = false;
			sb.append(panopticodeArgument.getSimpleType());
		}
		sb.append(")");
		return sb.toString();
	}

	public Collection<Metric> getMetrics() {
		return metricMap.values();
	}

	public MetricTarget getParent() {
		return getParentClass();
	}

	public boolean isLevel(Level toCheck) {
		return getLevel().equals(toCheck);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!this.getClass().getName().equals(other.getClass().getName()))
			return false;
		return equals((PanopticodeMethod) other);
	}

	private boolean equals(PanopticodeMethod other) {
		return getShortSignature().equals(other.getShortSignature());
	}
}
