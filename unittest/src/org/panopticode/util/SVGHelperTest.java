package org.panopticode.util;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.dom4j.Element;

public class SVGHelperTest extends MockObjectTestCase {
    public void testAddGroupingWithAllOptionalArguments() {
        Mock mockChild = mock(Element.class);
        Mock mockParent = mock(Element.class);
        String id = null;
        String name = null;
        String description = null;

        mockParent.expects(once())
                  .method("addElement")
                  .with(eq("g"), eq("http://www.w3.org/2000/svg"))
                  .will(returnValue(mockChild.proxy()));

        id = "foo";
        name = "fooName";
        description = "fooDescription";

        addAttributeExpectation(mockChild, "id", id);
        addAttributeExpectation(mockChild, "name", name);
        addAttributeExpectation(mockChild, "desc", description);

        SVGHelper.addGrouping((Element)mockParent.proxy(), id, name, description);
    }

    public void testAddGroupingWithNoOptionalArguments() {
        Mock mockChild = mock(Element.class);
        Mock mockParent = mock(Element.class);
        String id = null;
        String name = null;
        String description = null;

        mockParent.expects(once())
                  .method("addElement")
                  .with(eq("g"), eq("http://www.w3.org/2000/svg"))
                  .will(returnValue(mockChild.proxy()));

        SVGHelper.addGrouping((Element)mockParent.proxy(), id, name, description);
    }

    public void testAddRectangle() {
        Mock mockChild = mock(Element.class);
        Mock mockParent = mock(Element.class);
        double x, y, width, height;
        String fill, border;

        mockParent.expects(once())
                  .method("addElement")
                  .with(eq("rect"), eq("http://www.w3.org/2000/svg"))
                  .will(returnValue(mockChild.proxy()));

        x = 12.3;
        y = 3.5;
        width = 345.3;
        height = 7834.39;
        fill = "none";
        border = "black";

        addAttributeExpectation(mockChild, "x", x);
        addAttributeExpectation(mockChild, "y", y);
        addAttributeExpectation(mockChild, "width", width);
        addAttributeExpectation(mockChild, "height", height);
        addAttributeExpectation(mockChild, "fill", fill);
        addAttributeExpectation(mockChild, "stroke", border);
        addAttributeExpectation(mockChild, "stroke-width", "1");

        SVGHelper.addRectangle((Element)mockParent.proxy(), x, y, width, height, fill, border);
    }

    public void testAddText() {
        Mock mockChild = mock(Element.class);
        Mock mockParent = mock(Element.class);
        double x, y;
        String text;

        mockParent.expects(once())
                  .method("addElement")
                  .with(eq("text"), eq("http://www.w3.org/2000/svg"))
                  .will(returnValue(mockChild.proxy()));

        x = 12.3;
        y = 3.5;
        text = "yadda yadda yadda";

        addAttributeExpectation(mockChild, "x", x);
        addAttributeExpectation(mockChild, "y", y);

        mockChild.expects(once())
                 .method("addText")
                 .with(eq(text));

        SVGHelper.addText((Element)mockParent.proxy(), x, y, text);
    }

    public void testCleanId() {
        // first character must be a letter
        assertEquals("a", SVGHelper.cleanId("a"));

        assertEquals("x_", SVGHelper.cleanId("1"));
        assertEquals("x_", SVGHelper.cleanId("_"));
        assertEquals("x_", SVGHelper.cleanId("-"));
        assertEquals("x_", SVGHelper.cleanId(":"));
        assertEquals("x_", SVGHelper.cleanId("."));
        assertEquals("x_", SVGHelper.cleanId(" "));
        assertEquals("x_", SVGHelper.cleanId("/"));
        assertEquals("x_", SVGHelper.cleanId("\\"));
        assertEquals("x_", SVGHelper.cleanId("["));
        assertEquals("x_", SVGHelper.cleanId("]"));
        assertEquals("x_", SVGHelper.cleanId("|"));

        // other characters may be a number
        assertEquals("a1", SVGHelper.cleanId("a1"));
        assertEquals("x_1", SVGHelper.cleanId("11"));

        // other characters may be an underscore
        assertEquals("a_", SVGHelper.cleanId("a_"));
        assertEquals("x__", SVGHelper.cleanId("1_"));

        // other characters may be a hyphen
        assertEquals("a-", SVGHelper.cleanId("a-"));
        assertEquals("x_-", SVGHelper.cleanId("1-"));

        // other characters may be a period
        assertEquals("a.", SVGHelper.cleanId("a."));
        assertEquals("x_.", SVGHelper.cleanId("1."));

        // other characters may be a colon
        assertEquals("a:", SVGHelper.cleanId("a:"));
        assertEquals("x_:", SVGHelper.cleanId("1:"));

        // check bad characters
        assertEquals("a_", SVGHelper.cleanId("a "));
        assertEquals("a_", SVGHelper.cleanId("a/"));
        assertEquals("a_", SVGHelper.cleanId("a\\"));
        assertEquals("a_", SVGHelper.cleanId("a["));
        assertEquals("a_", SVGHelper.cleanId("a]"));
        assertEquals("a_", SVGHelper.cleanId("a|"));
    }

    private void addAttributeExpectation(Mock mock, String name, double value) {
        addAttributeExpectation(mock, name, String.valueOf(value));
    }

    private void addAttributeExpectation(Mock mock, String name, String value) {
        mock.expects(once())
            .method("addAttribute")
            .with(eq(name), eq(value));
    }
}
