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

import org.dom4j.Element;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public abstract class MetricDeclaration {
    private Supplement source;
    private String name;
    private String description;
    private Collection<Level> levels = new LinkedList<Level>();

    public MetricDeclaration(Supplement source, String name) {
        this.source = source;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Supplement getSource() {
        return source;
    }

    public void addLevel(Level level) {
        levels.add(level);
    }

    public boolean isLevel(Level level) {
        return levels.contains(level);
    }
    
    public abstract String getType();

    public void setDescription(String description) {
        this.description = description;
    }

    public void toXML(Element parentElement) {
        Element metricElement = null;

        metricElement = parentElement.addElement("metricDeclaration");

        new XMLRenderer(metricElement).render();
    }

    public abstract void metricFromXML(Element metricElement, MetricTarget metricTarget);

    public static void loadAllMetricsFromXML(Element parentElement, MetricTarget target, PanopticodeProject project) {
        Map<String, MetricDeclaration> metricsMap = new HashMap<String, MetricDeclaration>();

        for (MetricDeclaration metricDeclaration : project.getMetricDeclarations(target.getLevel())) {
            metricsMap.put(metricDeclaration.getName(), metricDeclaration);
        }

        for (Object objMetricElement : parentElement.elements("metric")) {
            Element metricElement = (Element) objMetricElement;
            String name = metricElement.attributeValue("name");
            MetricDeclaration metricDeclaration = metricsMap.get(name);
            if (metricDeclaration != null) {
                metricDeclaration.metricFromXML(metricElement, target);
            }
        }
    }

    class XMLRenderer {
        Element parentElement;

        XMLRenderer (Element parentElement) {
            this.parentElement = parentElement;
        }

        private void addDescription() {
            if (description != null && description.trim().length() > 0) {
                parentElement.addAttribute("description", description);
            }
        }

        private void addName() {
            parentElement.addAttribute("name", name);
        }

        private void addType() {
            parentElement.addAttribute("type", getType());
        }

        private void addScoping() {
            for (Level level : levels) {
                parentElement.addAttribute(level.getName(), "true");
            }
        }

        void render() {
            addName();
            addType();
            addScoping();
            addDescription();
        }
    }
}
