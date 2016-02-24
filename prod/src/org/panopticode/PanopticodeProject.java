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
package org.panopticode;

import static org.panopticode.util.ArgumentValidation.failIfEmpty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class PanopticodeProject implements MetricTarget {
    private List<SupplementDeclaration> supplementDeclarations = new LinkedList<SupplementDeclaration>();
    private List<PanopticodePackage> packages = new LinkedList<PanopticodePackage>();
    private Map<String, PanopticodePackage> packagesByName = new HashMap<String,  PanopticodePackage>();
    private Map<String, Metric> metricMap = new HashMap<String, Metric>();
    private String basePath;
    private String name;
    private String version;

    public PanopticodeProject(String name, String basePath) {
        failIfEmpty(name, "name");

        this.basePath = basePath;
        this.name = name;
    }

    public List<SupplementDeclaration> getSupplementsDeclared() {
        return Collections.unmodifiableList(supplementDeclarations);
    }

    public PanopticodePackage createAndAddPackage(String packageName) {
        PanopticodePackage panopticodePackage;

        panopticodePackage = new PanopticodePackage(this, packageName);

        if (!hasPackage(panopticodePackage.getName())) {
            addPackage(panopticodePackage);
        }

        return packagesByName.get(packageName);
    }

    private void addPackage(PanopticodePackage panopticodePackage) {
        packagesByName.put(panopticodePackage.getName(), panopticodePackage);
        packages.add(panopticodePackage);
    }

    public Document generateXMLDocument() {
        Document document;
        Element root;

        document = DocumentHelper.createDocument();
        root = document.addElement("panopticode");
        root.addAttribute("version", "0.1");
        toXML(root);

        return document;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getName() {
        return name;
    }

    public PanopticodePackage getPackageByName(String packageName) {
        return packagesByName.get(packageName);
    }

    public List<PanopticodePackage> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    public String getVersion() {
        return version;
    }

    public boolean hasPackage(String packageName) {
        return packagesByName.containsKey(packageName);
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    public void toFile(File file) throws IOException {
    	OutputFormat format = OutputFormat.createPrettyPrint();
    	XMLWriter writer = new XMLWriter(new FileWriter(file), format );
    	writer.write(generateXMLDocument());
    	writer.flush();
    }

    public void toXML(Element parentElement) {
        Element projectElement = null;

        projectElement = parentElement.addElement("project");

        projectElement.addAttribute("name", name);

        if (version != null) {
            projectElement.addAttribute("version", version);
        }

        for (SupplementDeclaration supplementDeclaration : supplementDeclarations) {
            supplementDeclaration.toXML(projectElement);
        }

        for (Metric metric : metricMap.values()) {
            metric.toXML(projectElement);
        }

        for (PanopticodePackage panopticodePackage : packages) {
            panopticodePackage.toXML(projectElement);
        }
    }
    
    public static PanopticodeProject fromFile(File file) throws DocumentException {
    	Document document = new SAXReader().read(file);
    	return fromXML(document.getRootElement().element("project"));
    }

    public static PanopticodeProject fromXML(Element projectElement) {
        PanopticodeProject panopticodeProject;
        String theName;
        String theVersion;

        theName = projectElement.attributeValue("name");
        theVersion = projectElement.attributeValue("version");

        panopticodeProject = new PanopticodeProject(theName, "");
        panopticodeProject.setVersion(theVersion);

        for (Object declarationElement : projectElement.elements("supplement")) {
            SupplementDeclaration supplementDeclaration = SupplementDeclaration.fromXML((Element) declarationElement);
            panopticodeProject.addSupplementDeclaration(supplementDeclaration);
        }

        for (Object packageElement : projectElement.elements("package")) {
            PanopticodePackage panopticodePackage = PanopticodePackage.fromXML((Element) packageElement, panopticodeProject);
            panopticodeProject.addPackage(panopticodePackage);
        }

        MetricDeclaration.loadAllMetricsFromXML(projectElement, panopticodeProject, panopticodeProject);

        return panopticodeProject;
    }

    public void addSupplementDeclaration(SupplementDeclaration supplementDeclaration) {
        supplementDeclarations.add(supplementDeclaration);
    }

    public List<PanopticodeClass> getClasses() {
        List<PanopticodeClass> classes = new LinkedList<PanopticodeClass>();

        for (PanopticodePackage panopticodePackage : packages) {
            classes.addAll(panopticodePackage.getClasses());
        }

        return classes;
    }

    public List<PanopticodeFile> getFiles() {
        List<PanopticodeFile> files = new LinkedList<PanopticodeFile>();

        for (PanopticodePackage panopticodePackage : packages) {
            files.addAll(panopticodePackage.getFiles());
        }

        return files;
    }

    public List<PanopticodeMethod> getMethods() {
        List<PanopticodeMethod> methods = new LinkedList<PanopticodeMethod>();

        for (PanopticodePackage panopticodePackage : packages) {
            methods.addAll(panopticodePackage.getMethods());
        }

        return methods;
    }

    public Collection <MetricDeclaration> getMetricDeclarations() {
        Collection <MetricDeclaration> metricDeclarations = new LinkedList <MetricDeclaration>();

        for (SupplementDeclaration supplementDeclaration : supplementDeclarations) {
            for (MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
                metricDeclarations.add(metricDeclaration);
            }
        }

        return metricDeclarations;
    }

    public Collection <MetricDeclaration> getMetricDeclarations(Level level) {
        Collection <MetricDeclaration> metricDeclarations = new LinkedList <MetricDeclaration>();

        for (SupplementDeclaration supplementDeclaration : supplementDeclarations) {
            for (MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
                if (metricDeclaration.isLevel(level)) {
                    metricDeclarations.add(metricDeclaration);
                }
            }
        }

        return metricDeclarations;
    }

    public void addMetric(Metric metric) {
        if (!metricMap.containsKey(metric.getName())) {
            metricMap.put(metric.getMetricDeclaration().getName(), metric);
        }
    }

    public Collection<MetricTarget> getChildren() {
        return new LinkedList<MetricTarget>(packages);
    }

    public Level getLevel() {
        return Level.PROJECT;
    }

    public Metric getMetricByName(String name) {
        return metricMap.get(name);
    }

    public Collection <Metric> getMetrics() {
        return metricMap.values();
    }

    public MetricTarget getParent() {
        return null;
    }

    public boolean isLevel(Level toCheck) {
        return getLevel().equals(toCheck);
    }
}
