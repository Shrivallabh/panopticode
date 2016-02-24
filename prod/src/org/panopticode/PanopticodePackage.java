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

import java.util.*;
import org.dom4j.*;

import static org.panopticode.util.ArgumentValidation.*;

public class PanopticodePackage implements MetricTarget {
    private List<PanopticodeFile> panopticodeFileList = new LinkedList<PanopticodeFile>();
    private Map<String, Metric> metricMap = new HashMap<String, Metric>();
    private Map<String, PanopticodeFile> filesByPath = new HashMap<String,  PanopticodeFile>();
    private PanopticodeProject parentProject;
    private String name;

    PanopticodePackage(PanopticodeProject project, String name) {
        failIfNull(name, "name");

        this.parentProject = project;
        this.name = name;
    }

    public void addMetric(Metric metric) {
        if (!metricMap.containsKey(metric.getName())) {
            metricMap.put(metric.getMetricDeclaration().getName(), metric);
        }
    }

    public Collection<MetricTarget> getChildren() {
        return new LinkedList<MetricTarget>(panopticodeFileList);
    }

    public Level getLevel() {
        return Level.PACKAGE;
    }

    public PanopticodeFile createAndAddFile(String canonicalPath, String name) {
        PanopticodeFile panopticodeFile;

        panopticodeFile = new PanopticodeFile(this, canonicalPath, name);

        if (!hasFileWithPath(panopticodeFile.getPath())) {
            addFile(panopticodeFile);
        }

        return filesByPath.get(panopticodeFile.getPath());
    }

    private void addFile(PanopticodeFile panopticodeFile) {
        filesByPath.put(panopticodeFile.getPath(), panopticodeFile);
        panopticodeFileList.add(panopticodeFile);
    }

    public List<PanopticodeFile> getFiles() {
        return Collections.unmodifiableList(panopticodeFileList);
    }

    public String getName() {
        return name;
    }

    public PanopticodeProject getParentProject() {
        return parentProject;
    }

    public boolean hasFileWithPath(String canonicalPath) {
        return filesByPath.containsKey(canonicalPath);
    }

    public void toXML(Element parentElement) {
        Element packageElement = null;

        packageElement = parentElement.addElement("package");

        packageElement.addAttribute("name", name);

        for (Metric metric : metricMap.values()) {
            metric.toXML(packageElement);
        }

        for (PanopticodeFile panopticodeFile : panopticodeFileList) {
            panopticodeFile.toXML(packageElement);
        }
    }

    public static PanopticodePackage fromXML(Element packageElement, PanopticodeProject parentProject) {
        PanopticodePackage panopticodePackage;
        String theName;

        theName = packageElement.attributeValue("name");

        panopticodePackage = new PanopticodePackage(parentProject, theName);

        for (Object fileElement : packageElement.elements("file")) {
            PanopticodeFile panopticodeFile = PanopticodeFile.fromXML((Element) fileElement, panopticodePackage, parentProject);
            panopticodePackage.addFile(panopticodeFile);
        }

        MetricDeclaration.loadAllMetricsFromXML(packageElement, panopticodePackage, parentProject);

        return panopticodePackage;
    }

    public List<PanopticodeClass> getClasses() {
        List<PanopticodeClass> classes = new LinkedList<PanopticodeClass>();

        for (PanopticodeFile panopticodeFile : panopticodeFileList) {
            classes.addAll(panopticodeFile.getClasses());
        }

        return classes;
    }

    public List<PanopticodeMethod> getMethods() {
        List<PanopticodeMethod> methods = new LinkedList<PanopticodeMethod>();

        for (PanopticodeFile panopticodeFile : panopticodeFileList) {
            methods.addAll(panopticodeFile.getMethods());
        }

        return methods;
    }

    public Metric getMetricByName(String name) {
        return metricMap.get(name);
    }

    public boolean isDefaultPackage() {
        return "".equals(name);
    }

    public Collection <Metric> getMetrics() {
        return metricMap.values();
    }

    public MetricTarget getParent() {
        return parentProject;
    }

    public boolean isLevel(Level toCheck) {
        return getLevel().equals(toCheck);
    }
}
