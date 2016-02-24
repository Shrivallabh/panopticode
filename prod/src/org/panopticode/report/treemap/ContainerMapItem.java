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
import edu.umd.cs.treemap.SimpleMapModel;
import edu.umd.cs.treemap.StripTreemap;

import java.util.List;
import java.util.LinkedList;

import org.dom4j.Element;
import org.panopticode.PanopticodeProject;

class ContainerMapItem extends AbstractMapItem {
    List<AbstractMapItem> children = new LinkedList<AbstractMapItem>();
    private int childInset;

    public ContainerMapItem(String name) {
        this(name, 3);
    }

    public ContainerMapItem(String name, int borderSize) {
        super(name);
        bounds = new Rect();
        this.childInset = borderSize;
    }

    public int getChildInset() {
        return childInset;
    }
    
    public void addChild(AbstractMapItem child) {
        child.setOrder(children.size());
        children.add(child);
    }

    public AbstractMapItem[] getChildren() {
        return children.toArray(new AbstractMapItem[]{});
    }

    public void renderSVG(Rect bounds, Element parent, PanopticodeProject project) {
        Element grouping;

        grouping = addGrouping(parent, null, null, name);

        AbstractMapItem[] items = getChildren();
        SimpleMapModel map = new SimpleMapModel(items, bounds);

        StripTreemap algorithm = new StripTreemap();

        Rect childBounds = new Rect(round(bounds.x) + childInset,
                round(bounds.y) + childInset,
                round(bounds.w) - (childInset * 2),
                round(bounds.h) - (childInset * 2));

        algorithm.layout(map, childBounds);

        for (AbstractMapItem item : items) {
            item.renderSVG(item.bounds, grouping, project);
        }
    }

    private double round(double toRound) {
        return toRound;
    }


    public double getSize() {
        double size = 0.0;

        AbstractMapItem[] items = getChildren();
        for (AbstractMapItem item : items) {
            size += item.getSize();
        }

        return size;
    }

    public void setSize(double size) {
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public void setBounds(double x, double y, double w, double h) {
        bounds = new Rect(x, y, w, h);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
