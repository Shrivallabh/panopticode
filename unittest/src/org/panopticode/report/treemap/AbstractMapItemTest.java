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

import junit.framework.TestCase;
import edu.umd.cs.treemap.Rect;
import org.dom4j.Element;
import org.panopticode.PanopticodeProject;

public class AbstractMapItemTest extends TestCase {
    private AbstractMapItem item = new StubMapItem("blah");

    public void testBounds() {
        Rect expected;

        assertNull(item.getBounds());

        expected = new Rect(1, 2, 3, 4);
        item.setBounds(expected);
        assertEquals(expected, item.getBounds());

        expected = new Rect(8,9,10,11);
        item.setBounds(8,9,10,11);
        assertEquals(expected, item.getBounds());
    }

    private void assertEquals(Rect expected, Rect actual) {
        assertEquals(expected.x, actual.x, 0.1);
        assertEquals(expected.y, actual.y, 0.1);
        assertEquals(expected.w, actual.w, 0.1);
        assertEquals(expected.h, actual.h, 0.1);
    }

    public void testDepth() {
        assertEquals(0, item.getDepth());
        item.setDepth(1);
        assertEquals(1, item.getDepth());
        item.setDepth(0);
        assertEquals(0, item.getDepth());
    }

    public void testName() {
        item = new StubMapItem("foo");
        assertEquals("foo", item.getName());

        item = new StubMapItem("<foo>");
        assertEquals("&lt;foo&gt;", item.getName());
    }

    public void testOrder() {
        assertEquals(0, item.getOrder());
        item.setOrder(1);
        assertEquals(1, item.getOrder());
        item.setOrder(0);
        assertEquals(0, item.getOrder());
    }

    class StubMapItem extends AbstractMapItem {
        public StubMapItem(String name) {
            super(name);
        }

        public String getTitle() {
            return null;
        }

        public String renderSVG(Rect bounds) {
            return null;
        }

        public void renderSVG(Rect bounds, Element parent, PanopticodeProject project) {
        }

        public double getSize() {
            return 0;
        }

        public void setSize(double size) {
        }
    }
}
