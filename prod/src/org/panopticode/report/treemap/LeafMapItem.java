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
package org.panopticode.report.treemap;

import static org.panopticode.util.SVGHelper.*;

import edu.umd.cs.treemap.Rect;
import org.dom4j.Element;
import org.panopticode.PanopticodeMethod;
import org.panopticode.PanopticodeProject;
import org.panopticode.Metric;
import org.panopticode.MetricTarget;

public class LeafMapItem extends AbstractMapItem {
    private boolean interactive;
    private double size;
    private String border;
    private String fill;
    private PanopticodeMethod panopticodeMethod;

    public LeafMapItem(double size, String fill, String border, String name, PanopticodeMethod panopticodeMethod, boolean interactive) {
        super(name);

        this.size = size;
        this.border = border;
        this.fill = fill;
        this.panopticodeMethod = panopticodeMethod;

        bounds = new Rect();

        this.interactive = interactive;
    }

    public void renderSVG(Rect bounds, Element parent, PanopticodeProject project) {
        Element rectElement;

        if(panopticodeMethod.getMetricByName("NCSS") == null) {
            return;
        }

        try {
            rectElement = addRectangle(parent, bounds.x, bounds.y, bounds.w, bounds.h, fill, border);
        } catch(IllegalArgumentException e) {
            // This is caused by the TreemapAlgorithms library creating invalid boundaries.
            return;
        }

        if(interactive) {
            MetricTarget panopticodePackage;
            MetricTarget panopticodeFile;
            MetricTarget panopticodeClass;

            panopticodeClass = panopticodeMethod.getParent();
            panopticodeFile = panopticodeClass.getParent();
            panopticodePackage = panopticodeFile.getParent();

            StringBuffer onclick = new StringBuffer();
            onclick.append("showDetails('");
            onclick.append(project.getName());
            onclick.append("', [");
            addMetricMap(onclick, project);
            onclick.append("], '");
            onclick.append(panopticodePackage.getName());
            onclick.append("', [");
            addMetricMap(onclick, panopticodePackage);
            onclick.append("], '");
            onclick.append(panopticodeFile.getName());
            onclick.append("', [");
            addMetricMap(onclick, panopticodeFile);
            onclick.append("], '");
            onclick.append(panopticodeClass.getName());
            onclick.append("', [");
            addMetricMap(onclick, panopticodeClass);
            onclick.append("], '");
            onclick.append(panopticodeMethod.getShortSignature());
            onclick.append("', [");
            addMetricMap(onclick, panopticodeMethod);
            onclick.append("]);");
            rectElement.addAttribute("onclick", onclick.toString());
        }
    }

    private void addMetricMap(StringBuffer sb, MetricTarget source) {
        boolean first = true;

        for (Metric metric : source.getMetrics()) {
            if(!first) {
                sb.append(", ");
            }

            sb.append("['");
            sb.append(metric.getName());
            sb.append("', '");
            sb.append(metric.getStringValue());
            sb.append("']");

            first = false;
        }
    }
    
    public double getSize() {
        return size;
    }

    // required by Treemap library
    public void setSize(double v) {
    }

    public String getFill() {
        return this.fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }
}
