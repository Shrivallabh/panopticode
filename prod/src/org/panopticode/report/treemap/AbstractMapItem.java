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

import edu.umd.cs.treemap.Mappable;
import edu.umd.cs.treemap.Rect;

import java.util.List;
import java.util.LinkedList;

import org.dom4j.Element;
import org.panopticode.PanopticodeProject;

abstract class AbstractMapItem implements Mappable {
    double size;
    Rect bounds;
    int order = 0;
    int depth = 0;
    protected String name;
    List<LeafMapItem> children = new LinkedList<LeafMapItem>();
    protected int ccn;

    public AbstractMapItem(String name) {
        this.name = escape(name);
    }

    private String escape(String text) {
        String foo = text.replaceAll("<", "&lt;");
        foo = foo.replaceAll(">", "&gt;");
        return foo;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public abstract void renderSVG(Rect bounds, Element parent, PanopticodeProject project);

    public String getName() {
        return name;
    }

    public abstract double getSize();

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public synchronized void setBounds(double x, double y, double w, double h) {
        if (bounds == null) {
            bounds = new Rect();
        }

        bounds.setRect(x, y, w, h);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
