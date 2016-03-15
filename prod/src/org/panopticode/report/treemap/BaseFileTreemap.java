package org.panopticode.report.treemap;

import static org.panopticode.util.SVGHelper.addGrouping;

import java.util.List;

import org.dom4j.Element;
import org.panopticode.IntegerMetric;
import org.panopticode.Level;
import org.panopticode.MetricDeclaration;
import org.panopticode.PanopticodeClass;
import org.panopticode.PanopticodeFile;
import org.panopticode.PanopticodeMethod;
import org.panopticode.PanopticodePackage;
import org.panopticode.PanopticodeProject;

import edu.umd.cs.treemap.Rect;


public abstract class BaseFileTreemap extends BaseTreemap {

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

	}

	protected void renderJavascriptFunctions(Element parentElement, List<MetricDeclaration> projectMetrics, List<MetricDeclaration> packageMetrics,
			List<MetricDeclaration> fileMetrics, List<MetricDeclaration> classMetrics, List<MetricDeclaration> methodMetrics) {
		StringBuffer javascript;

		javascript = new StringBuffer();

		javascript.append("\n");
		javascript
				.append("function showDetails(projectName, projectMetrics, packageName, packageMetrics, fileName, fileMetrics) {\n");
		javascript.append("  replaceText('projectName', 'Project: ' + projectName);\n");
		javascript.append(buildReplaceTextForMetrics("ProjectMetric", "projectMetrics", projectMetrics));
		javascript.append("  replaceText('packageName', 'Package: ' + packageName);\n");
		javascript.append(buildReplaceTextForMetrics("PackageMetric", "packageMetrics", packageMetrics));
		javascript.append("  replaceText('fileName', 'File: ' + fileName);\n");
		javascript.append(buildReplaceTextForMetrics("FileMetric", "fileMetrics", fileMetrics));
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
			for (PanopticodeFile panopticodeFile : panopticodePackage.getFiles()) {
				try {
					IntegerMetric ncss = (IntegerMetric) panopticodeFile.getMetricByName("NCSS");
					if (ncss != null) {
						double size = ncss.getValue();

						String name = panopticodeFile.getName();
						Category cat = getCategorizer().getCategory(panopticodeFile);
						String fill = cat.getFill();
						String border = cat.getBorder();

						packageContainer.addChild(new FileLeafMapItem(size, fill, border, name, panopticodeFile, interactive));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return parent;
	}


}
