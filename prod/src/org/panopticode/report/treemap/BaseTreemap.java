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
package org.panopticode.report.treemap;

import static org.panopticode.util.SVGHelper.addGrouping;
import static org.panopticode.util.SVGHelper.addText;
import static org.panopticode.util.SVGHelper.cleanId;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.panopticode.IntegerMetric;
import org.panopticode.Level;
import org.panopticode.MetricDeclaration;
import org.panopticode.PanopticodeClass;
import org.panopticode.PanopticodeMethod;
import org.panopticode.PanopticodePackage;
import org.panopticode.PanopticodeProject;
import org.panopticode.Report;

import edu.umd.cs.treemap.Rect;

public abstract class BaseTreemap implements Report {
	private final static double DETAIL_WIDTH = 300.0;
	private final static double FOOTER_HEIGHT = 25.0;
	private final static double HEADER_HEIGHT = 25.0;
	private final static double REGION_BORDER = 3.0;

	public void runReport(PanopticodeProject project, String[] arguments) {
		String outputFile = arguments[0];

		PrintStream output;
		XMLWriter xmlWriter;

		boolean interactive = false;

		if (arguments.length == 2 && "-interactive".equals(arguments[1]))
			interactive = true;

		try {
			if (outputFile == null)
				output = System.out;
			else
				output = new PrintStream(outputFile);

			xmlWriter = new XMLWriter(output, OutputFormat.createPrettyPrint());
			xmlWriter.write(generateXMLDocument(project, interactive));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Document generateXMLDocument(PanopticodeProject project, boolean interactive) {
		Rect bounds = new Rect(0, 0, 1024, 768);

		Document document;
		Element root;

		document = DocumentHelper.createDocument();

		root = document.addElement("svg");
		renderSVG(project, root, interactive, bounds);

		return document;
	}

	private static List<MetricDeclaration> getMetricDeclarations(PanopticodeProject project, Level level) {
		List<MetricDeclaration> metrics = new LinkedList<MetricDeclaration>();

		for (MetricDeclaration metricDeclaration : project.getMetricDeclarations())
			if (metricDeclaration.isLevel(level))
				metrics.add(metricDeclaration);

		return metrics;
	}

	Map<String, Rect> getInteractiveLayout(Rect bounds) {
		Map<String, Rect> layout = new HashMap<String, Rect>();

		layout.put("title", new Rect(bounds.x + REGION_BORDER,
										bounds.y + REGION_BORDER,
										bounds.w - 2.0 * REGION_BORDER,
										HEADER_HEIGHT));
		layout.put("legend", new Rect(bounds.x + REGION_BORDER,
										bounds.h + bounds.y - FOOTER_HEIGHT - REGION_BORDER,
										bounds.w - 2.0 * REGION_BORDER,
										FOOTER_HEIGHT));
		layout.put("contents", new Rect(bounds.x + REGION_BORDER,
										bounds.y + HEADER_HEIGHT + 2.0 * REGION_BORDER,
										bounds.w - DETAIL_WIDTH - 3.0 * REGION_BORDER,
										bounds.h - HEADER_HEIGHT - FOOTER_HEIGHT - 4.0 * REGION_BORDER));
		layout.put("details", new Rect(bounds.x + bounds.w - DETAIL_WIDTH - REGION_BORDER,
										bounds.y + HEADER_HEIGHT + 2.0 * REGION_BORDER,
										DETAIL_WIDTH,
										bounds.h - HEADER_HEIGHT - FOOTER_HEIGHT - 4.0 * REGION_BORDER));

		return layout;
	}

	Map<String, Rect> getStaticLayout(Rect bounds) {
		Map<String, Rect> layout = new HashMap<String, Rect>();

		layout.put("title", new Rect(bounds.x + REGION_BORDER,
										bounds.y + REGION_BORDER,
										bounds.w - 2.0 * REGION_BORDER,
										HEADER_HEIGHT));
		layout.put("legend", new Rect(bounds.x + REGION_BORDER,
										bounds.h + bounds.y - FOOTER_HEIGHT - REGION_BORDER,
										bounds.w - 2.0 * REGION_BORDER,
										FOOTER_HEIGHT));
		layout.put("contents", new Rect(bounds.x + REGION_BORDER,
										bounds.y + HEADER_HEIGHT + 2.0 * REGION_BORDER,
										bounds.w - 2.0 * REGION_BORDER,
										bounds.h - HEADER_HEIGHT - FOOTER_HEIGHT - 4.0 * REGION_BORDER));

		return layout;
	}

	Map<String, Rect> getLayout(Rect bounds, boolean interactive) {
		if (interactive)
			return getInteractiveLayout(bounds);
		return getStaticLayout(bounds);
	}

	void renderSVG(PanopticodeProject project, Element svgElement, boolean interactive, Rect bounds) {
		Map<String, Rect> layout;

		StringBuffer viewBox = new StringBuffer();
		viewBox.append(bounds.x);
		viewBox.append(" ");
		viewBox.append(bounds.y);
		viewBox.append(" ");
		viewBox.append(bounds.w);
		viewBox.append(" ");
		viewBox.append(bounds.h);

		svgElement.add(new Namespace("", "http://www.w3.org/2000/svg"));
		svgElement.add(new Namespace("xlink", "http://www.w3.org/1999/xlink"));
		svgElement.addAttribute("viewBox", viewBox.toString());

		layout = getLayout(bounds, interactive);

		renderTitle(svgElement, layout.get("title"), project);

		renderDetails(layout.get("details"), svgElement, project, interactive);

		getData(project, interactive).renderSVG(layout.get("contents"), svgElement, project);

		getCategorizer().renderHorizontalLegend(layout.get("legend"), svgElement);
	}

	void renderTitle(Element parent, Rect bounds, PanopticodeProject project) {
		Element titleElement;

		titleElement = addText(parent, bounds.w / 2.0 + bounds.x, 17.0 + bounds.y, getTitle(project));
		titleElement.addAttribute("style", "text-anchor: middle;");
	}

	private Element addDetailLine(Element detailElement, String id, double x, double y, String fontSize, String text) {
		Element textElement;

		textElement = addText(detailElement, x, y, text);
		textElement.addAttribute("id", id);
		textElement.addAttribute("font-size", fontSize);

		return textElement;
	}

	private double addMetricDetails(Element detailElement, String idSuffix, double x, double y, List<MetricDeclaration> metrics) {
		double yPosition = y;

		for (MetricDeclaration metricDeclaration : metrics) {
			yPosition += 20;
			addDetailLine(detailElement, getAsId(metricDeclaration) + idSuffix, x, yPosition, "0.8em", metricDeclaration.getName() + ": ");
		}

		return yPosition;
	}

	void renderDetails(Rect bounds, Element parentElement, PanopticodeProject project, boolean interactive) {
		if (!interactive)
			return;
		double textY;

		List<MetricDeclaration> projectMetrics = getMetricDeclarations(project, Level.PROJECT);
		List<MetricDeclaration> packageMetrics = getMetricDeclarations(project, Level.PACKAGE);
		List<MetricDeclaration> fileMetrics = getMetricDeclarations(project, Level.FILE);
		List<MetricDeclaration> classMetrics = getMetricDeclarations(project, Level.CLASS);
		List<MetricDeclaration> methodMetrics = getMetricDeclarations(project, Level.METHOD);

		renderJavascriptFunctions(parentElement, projectMetrics, packageMetrics, fileMetrics, classMetrics, methodMetrics);

		Element detailElement;
		Element textElement;
		double textX = bounds.x + 5;
		double indentedTextX = bounds.x + 15;

		detailElement = addGrouping(parentElement, "details", null, null);

		textY = bounds.y + 20;
		textElement = addDetailLine(detailElement, "detailsTitle", textX, textY, "1.0em", "Details");
		textElement.addAttribute("style", "text-decoration: underline;");

		textY += 20;
		textElement = addDetailLine(detailElement, "detailsHint", textX, textY, "0.8em", "( Click on a rectangle to view details )");
		textElement.addAttribute("stroke", "gray");
		textElement.addAttribute("fill", "gray");

		textY += 35;
		addDetailLine(detailElement, "projectName", textX, textY, "0.8em", "Project: ");
		textY = addMetricDetails(detailElement, "ProjectMetric", indentedTextX, textY, projectMetrics);

		textY += 25;
		addDetailLine(detailElement, "packageName", textX, textY, "0.8em", "Package: ");
		textY = addMetricDetails(detailElement, "PackageMetric", indentedTextX, textY, packageMetrics);

		textY += 25;
		addDetailLine(detailElement, "fileName", textX, textY, "0.8em", "File: ");
		textY = addMetricDetails(detailElement, "FileMetric", indentedTextX, textY, fileMetrics);

		textY += 25;
		addDetailLine(detailElement, "className", textX, textY, "0.8em", "Class: ");
		textY = addMetricDetails(detailElement, "ClassMetric", indentedTextX, textY, classMetrics);

		textY += 25;
		addDetailLine(detailElement, "methodName", textX, textY, "0.8em", "Method: ");
		textY = addMetricDetails(detailElement, "MethodMetric", indentedTextX, textY, methodMetrics);
	}

	private String getAsId(MetricDeclaration metricDeclaration) {
		return cleanId(metricDeclaration.getName());
	}

	private String buildReplaceTextForMetrics(String idSuffix, String mapName, List<MetricDeclaration> metrics) {
		StringBuilder sb = new StringBuilder();

		for (MetricDeclaration metricDeclaration : metrics) {
			String id = getAsId(metricDeclaration) + idSuffix;
			String name = metricDeclaration.getName();
			sb.append("  replaceText('");
			sb.append(id);
			sb.append("', '");
			sb.append(name);
			sb.append(": ' + getMetric(");
			sb.append(mapName);
			sb.append(", '");
			sb.append(name);
			sb.append("'));\n");
		}
		return sb.toString();
	}

	private void renderJavascriptFunctions(Element parentElement, List<MetricDeclaration> projectMetrics, List<MetricDeclaration> packageMetrics,
			List<MetricDeclaration> fileMetrics, List<MetricDeclaration> classMetrics, List<MetricDeclaration> methodMetrics) {
		StringBuffer javascript;

		javascript = new StringBuffer();

		javascript.append("\n");
		javascript
				.append("function showDetails(projectName, projectMetrics, packageName, packageMetrics, fileName, fileMetrics, className, classMetrics, methodName, methodMetrics) {\n");
		javascript.append("  replaceText('projectName', 'Project: ' + projectName);\n");
		javascript.append(buildReplaceTextForMetrics("ProjectMetric", "projectMetrics", projectMetrics));
		javascript.append("  replaceText('packageName', 'Package: ' + packageName);\n");
		javascript.append(buildReplaceTextForMetrics("PackageMetric", "packageMetrics", packageMetrics));
		javascript.append("  replaceText('fileName', 'File: ' + fileName);\n");
		javascript.append(buildReplaceTextForMetrics("FileMetric", "fileMetrics", fileMetrics));
		javascript.append("  replaceText('className', 'Class: ' + className);\n");
		javascript.append(buildReplaceTextForMetrics("ClassMetric", "classMetrics", classMetrics));
		javascript.append("  replaceText('methodName', 'Method: ' + methodName);\n");
		javascript.append(buildReplaceTextForMetrics("MethodMetric", "methodMetrics", methodMetrics));
		javascript.append("}\n");
		javascript.append("\n");
		javascript.append("function getMetric(metricsMap, key) {\n");
		javascript.append("  var index;\n");
		javascript.append("  for (index = 0; index < metricsMap.length; index++) {\n");
		javascript.append("    if (metricsMap[index][0] == key) {\n");
		javascript.append("      return metricsMap[index][1];\n");
		javascript.append("    }\n");
		javascript.append("  }\n");
		javascript.append("  return 'Unknown';\n");
		javascript.append("}\n");
		javascript.append("\n");
		javascript.append("function replaceText(id, newText) {\n");
		javascript.append("  var parentElement;\n");
		javascript.append("  var newTextNode;\n");
		javascript.append("\n");
		javascript.append("  newTextNode = document.createTextNode(newText);\n");
		javascript.append("\n");
		javascript.append("  parentElement = document.getElementById(id);\n");
		javascript.append("  parentElement.replaceChild(newTextNode, parentElement.firstChild);\n");
		javascript.append("}\n");

		Element scriptElement = parentElement.addElement("script", "http://www.w3.org/2000/svg");
		scriptElement.addAttribute("type", "text/ecmascript");
		scriptElement.addCDATA(javascript.toString());
	}

	ContainerMapItem getData(PanopticodeProject project, boolean interactive) {
		ContainerMapItem parent = new ContainerMapItem("Project - " + project.getName(), 0);

		for (PanopticodePackage panopticodePackage : project.getPackages()) {
			ContainerMapItem packageContainer = new ContainerMapItem("Package - " + panopticodePackage.getName(), 3);
			parent.addChild(packageContainer);

			for (PanopticodeClass panopticodeClass : panopticodePackage.getClasses()) {
				ContainerMapItem classContainer = new ContainerMapItem("Class - " + panopticodeClass.getName(), 1);
				packageContainer.addChild(classContainer);

				for (PanopticodeMethod panopticodeMethod : panopticodeClass.getMethods())
					try {
						IntegerMetric ncss = (IntegerMetric) panopticodeMethod.getMetricByName("NCSS");
						if (ncss != null) {
							double size = ncss.getValue();

							String name = panopticodeMethod.getName();
							Category cat = getCategorizer().getCategory(panopticodeMethod);
							String fill = cat.getFill();
							String border = cat.getBorder();

							classContainer.addChild(new LeafMapItem(size, fill, border, name, panopticodeMethod, interactive));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}

		return parent;
	}

	public abstract Categorizer getCategorizer();

	public abstract String getTitle(PanopticodeProject project);
}
