package org.panopticode.report.treemap;

import junit.framework.TestCase;

import java.util.List;
import java.util.LinkedList;

import org.panopticode.MetricTarget;
import org.dom4j.Element;
import edu.umd.cs.treemap.Rect;

import static org.panopticode.TestHelpers.*;

public class CategorizerTest extends TestCase {
    public void testRenderHorizontalLegendDoesntExplodeHorrifically() {
        Categorizer cat;
        DefaultCategory defaultCategory;
        Element parentElement;
        Rect bounds;

        defaultCategory = new DefaultCategory("Default", "blue", "black");

        List<Category> categories = new LinkedList<Category>();
        categories.add(new DummyCategory("Good", "green", "black"));
        categories.add(new DummyCategory("Bad",  "black", "red"));

        cat = new Categorizer(categories, defaultCategory);

        bounds = new Rect(0, 0, 500, 500);
        parentElement = createDummyElement();

        cat.renderHorizontalLegend(bounds, parentElement);

        // TODO: write decent assertions for this test
        assertNotNull(parentElement.element("rect"));
    }

    class DummyCategory extends Category {
        public DummyCategory(String legendLabel, String fill, String border) {
            super(legendLabel, fill, border);
        }

        public boolean inCategory(MetricTarget toCheck) {
            return false;
        }
    }
}
