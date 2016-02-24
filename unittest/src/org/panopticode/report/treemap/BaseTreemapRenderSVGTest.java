package org.panopticode.report.treemap;

import org.panopticode.*;
import static org.panopticode.TestHelpers.createDummyProject;
import static org.panopticode.TestHelpers.createDummyElement;
import org.dom4j.Element;
import edu.umd.cs.treemap.Rect;

import java.util.LinkedList;
import java.io.IOException;

import junit.framework.TestCase;

public class BaseTreemapRenderSVGTest extends TestCase {
    public void testRenderSVG() throws IOException {
        BaseTreemap treemap;
        Element parentElement;
        PanopticodeProject project;
        Rect bounds;

        treemap = new ManualBaseTreemapStub();
        project = createDummyProject();
        parentElement = createDummyElement();
        bounds = new Rect(0, 0, 1000, 1000);

        treemap.renderSVG(project, parentElement, false, bounds);

        assertNotNull(parentElement.element("renderTitle"));
        assertNotNull(parentElement.element("renderDetails"));
        assertNotNull(parentElement.element("renderLegend"));
    }
}

class ManualBaseTreemapStub extends BaseTreemap {
    public Categorizer categorizer = new CategorizerStub();
    public String title;

    public Categorizer getCategorizer() {
        return categorizer;
    }

    public String getTitle(PanopticodeProject project) {
        return title;
    }

    void renderTitle(Element parent, Rect bounds, PanopticodeProject project) {
        parent.addElement("renderTitle");
    }

    void renderDetails(Rect bounds, Element parent, PanopticodeProject project, boolean interactive) {
        parent.addElement("renderDetails");
    }

    ContainerMapItem getData(PanopticodeProject project, boolean interactive) {
        return new ContainerMapItemStub();
    }
}

class ContainerMapItemStub extends ContainerMapItem {
    public ContainerMapItemStub() {
        super("foo");
    }

    public void renderSVG(Rect bounds, Element parent, PanopticodeProject project) {
        parent.addElement("renderContents");
    }
}

class CategorizerStub extends Categorizer {
    public CategorizerStub() {
        super(new LinkedList<Category>(), null);
    }

    public void renderHorizontalLegend(Rect bounds, Element parent) {
        parent.addElement("renderLegend");
    }
}
