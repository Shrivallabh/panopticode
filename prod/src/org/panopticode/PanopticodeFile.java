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

public class PanopticodeFile implements MetricTarget {
    private List<PanopticodeClass> classList = new LinkedList<PanopticodeClass>();
    private Map<String, PanopticodeClass> classesByName = new HashMap<String,  PanopticodeClass>();
    private Map<String, Metric> metricsByName = new HashMap<String, Metric>();
    private PanopticodePackage parentPackage;
    private String path;
    private String name;

    PanopticodeFile(PanopticodePackage parentPackage, String path, String name) {
        failIfNull(parentPackage, "parentPackage");
        failIfNull(path, "path");
        failIfNull(name, "name");

        this.parentPackage = parentPackage;
        String basePath = parentPackage.getParentProject().getBasePath();

        if (basePath.length() > 0) {
            this.path = path.substring(parentPackage.getParentProject().getBasePath().length() + 1);
        } else {
            this.path = path;
        }

        this.name = name;
    }

    public PanopticodeClass createAndAddClass(String className, int positionLine, int positionColumn) {
        PanopticodeClass panopticodeClass;

        panopticodeClass = new PanopticodeClass(this, className, positionLine, positionColumn);

        if (!hasClassNamed(panopticodeClass.getName())) {
            addClass(panopticodeClass);
        }

        return classesByName.get(className);
    }

    private void addClass(PanopticodeClass panopticodeClass) {
        classesByName.put(panopticodeClass.getName(), panopticodeClass);
        classList.add(panopticodeClass);
    }

    public List<PanopticodeClass> getClasses() {
        return Collections.unmodifiableList(classList);
    }

    public PanopticodePackage getParentPackage() {
        return this.parentPackage;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean hasClassNamed(String className) {
        return classesByName.containsKey(className);
    }

    public void toXML(Element parentElement) {
        Element fileElement = null;

        fileElement = parentElement.addElement("file");

        fileElement.addAttribute("projectPath", path);
        fileElement.addAttribute("name", name);

        for (PanopticodeClass panopticodeClass : classList) {
            panopticodeClass.toXML(fileElement);
        }
    }

    public static PanopticodeFile fromXML(Element fileElement,
                                          PanopticodePackage parentPackage,
                                          PanopticodeProject project) {
        PanopticodeFile panopticodeFile;
        String theName;
        String thePath;

        theName = fileElement.attributeValue("name");
        thePath = fileElement.attributeValue("projectPath");

        panopticodeFile = new PanopticodeFile(parentPackage, thePath, theName);

        for (Object classElement : fileElement.elements("class")) {
            PanopticodeClass panopticodeClass = PanopticodeClass.fromXML((Element) classElement, 
                                                                         panopticodeFile,
                                                                         project);
            panopticodeFile.addClass(panopticodeClass);
        }

        MetricDeclaration.loadAllMetricsFromXML(fileElement, panopticodeFile, project);

        return panopticodeFile;
    }

    public List<PanopticodeMethod> getMethods() {
        List<PanopticodeMethod> methods = new LinkedList<PanopticodeMethod>();

        for (PanopticodeClass panopticodeClass : classList) {
            methods.addAll(panopticodeClass.getMethods());
        }

        return methods;
    }

    public void addMetric(Metric metric) {
        if (!metricsByName.containsKey(metric.getName())) {
            metricsByName.put(metric.getName(), metric);
        }
    }

    public Collection<MetricTarget> getChildren() {
        return new LinkedList<MetricTarget>(classList);
    }

    public Level getLevel() {
        return Level.FILE;
    }

    public Metric getMetricByName(String name) {
        return metricsByName.get(name);
    }

    public Collection <Metric> getMetrics() {
        return metricsByName.values();
    }

    public MetricTarget getParent() {
        return parentPackage;
    }

    public boolean isLevel(Level toCheck) {
        return getLevel().equals(toCheck);
    }
}
