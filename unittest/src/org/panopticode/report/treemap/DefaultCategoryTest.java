package org.panopticode.report.treemap;

import junit.framework.TestCase;

public class DefaultCategoryTest extends TestCase {
    private DefaultCategory cat;
    private String defaultBorder;
    private String defaultFill;
    private String defaultLabel;

    protected void setUp() throws Exception {
        super.setUp();

        defaultLabel = "N/A";
        defaultFill = "blue";
        defaultBorder = "black";

        cat = new DefaultCategory(defaultLabel, defaultFill, defaultBorder);
    }

    public void testConstructorSetsAttributesCorrectly() {
        assertEquals(defaultLabel, cat.getLegendLabel());
        assertEquals(defaultFill, cat.getFill());
        assertEquals(defaultBorder, cat.getBorder());
    }

    public void testInCategory() {
        assertTrue(cat.inCategory(null));
    }
}
