package org.panopticode.report.treemap;

import junit.framework.TestCase;
import edu.umd.cs.treemap.Rect;
import org.dom4j.Element;
import org.panopticode.PanopticodeProject;
import static org.panopticode.TestHelpers.*;

public class ContainerMapItemTest extends TestCase {
    public void testConstructorSetsPropertiesCorrectly() {
        ContainerMapItem item;

        item = new ContainerMapItem("foo");
        assertEquals("foo", item.getName());
        assertEquals(3, item.getChildInset());

        item = new ContainerMapItem("bar", 5);
        assertEquals("bar", item.getName());
        assertEquals(5, item.getChildInset());
    }

    public void testBounds() {
        ContainerMapItem item;

        item = new ContainerMapItem("foo");
        assertEquals(0.0, item.getBounds().x);
        assertEquals(0.0, item.getBounds().y);
        assertEquals(1.0, item.getBounds().w);
        assertEquals(1.0, item.getBounds().h);

        item.setBounds(new Rect(1, 2, 3, 4));
        assertEquals(1.0, item.getBounds().x);
        assertEquals(2.0, item.getBounds().y);
        assertEquals(3.0, item.getBounds().w);
        assertEquals(4.0, item.getBounds().h);

        item.setBounds(5, 6, 7, 8);
        assertEquals(5.0, item.getBounds().x);
        assertEquals(6.0, item.getBounds().y);
        assertEquals(7.0, item.getBounds().w);
        assertEquals(8.0, item.getBounds().h);

        item.setBounds(9, 10, 11, 12);
        assertEquals( 9.0, item.getBounds().x);
        assertEquals(10.0, item.getBounds().y);
        assertEquals(11.0, item.getBounds().w);
        assertEquals(12.0, item.getBounds().h);
    }

    public void testOrder() {
        ContainerMapItem item;

        item = new ContainerMapItem("foo");
        assertEquals(0, item.getOrder());

        item.setOrder(9);
        assertEquals(9, item.getOrder());
    }

    public void testSize() {
        ContainerMapItem item;
        SizedMapItem sizedItem;

        item = new ContainerMapItem("foo");
        assertEquals(0.0, item.getSize());

        item.setSize(23.4);
        assertEquals(0.0, item.getSize());

        sizedItem = new SizedMapItem("sdf");
        sizedItem.setSize(2384.5);
        item.addChild(sizedItem);
        assertEquals(2384.5, item.getSize());
    }

    public void testRenderSVG() {
        ContainerMapItem item;
        Element parentElement;
        PanopticodeProject project;
        Rect bounds;

        bounds = new Rect(0, 0, 100, 100);
        parentElement = createDummyElement();
        project = createDummyProject();

        item = new ContainerMapItem("foo");
        item.addChild(new SizedMapItem("blah"));
        item.renderSVG(bounds, parentElement, project);

        Element gElement = parentElement.element("g");
        assertNotNull(gElement);
        assertEquals("foo", gElement.attributeValue("desc"));

        assertNotNull(gElement.element("sizedMapItem"));

        // now with an empty name and no children
        bounds = new Rect(0, 0, 100, 100);
        parentElement = createDummyElement();
        project = createDummyProject();

        item = new ContainerMapItem("");
        item.renderSVG(bounds, parentElement, project);

        gElement = parentElement.element("g");
        assertNotNull(gElement);
        assertNull(gElement.attributeValue("desc"));

        assertNull(gElement.element("sizedMapItem"));
    }

    class SizedMapItem extends AbstractMapItem {
        double size = 0.0;

        public SizedMapItem(String name) {
            super(name);
        }

        public void renderSVG(Rect bounds, Element parent, PanopticodeProject project) {
            parent.addElement("sizedMapItem");
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }
    }
}
