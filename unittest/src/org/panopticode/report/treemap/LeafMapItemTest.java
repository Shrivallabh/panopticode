package org.panopticode.report.treemap;

import junit.framework.TestCase;
import org.panopticode.*;
import static org.panopticode.TestHelpers.*;
import org.dom4j.Element;
import edu.umd.cs.treemap.Rect;


public class LeafMapItemTest extends TestCase {
    private boolean defaultInteractive;
    private double defaultSize;
    private PanopticodeMethod defaultMethod;
    private String defaultBorder;
    private String defaultFill;
    private String defaultName;

    protected void setUp() throws Exception {
        super.setUp();

        defaultSize = 23.3;
        defaultFill = "green";
        defaultBorder = "black";
        defaultName = "foo";
        defaultMethod = createDummyMethod();
        defaultInteractive = false;
    }

    public void testBorder() {
        LeafMapItem leaf;

        leaf = new LeafMapItem(defaultSize, defaultFill, defaultBorder, defaultName, defaultMethod, defaultInteractive);
        assertEquals(defaultBorder, leaf.getBorder());

        leaf.setBorder("orange");
        assertEquals("orange", leaf.getBorder());

        leaf.setBorder("red");
        assertEquals("red", leaf.getBorder());
    }

    public void testConstructorSetsAttributesProperly() {
        LeafMapItem leaf;

        leaf = new LeafMapItem(defaultSize, defaultFill, defaultBorder, defaultName, defaultMethod, defaultInteractive);

        assertEquals(defaultSize, leaf.getSize());
        assertEquals(defaultFill, leaf.getFill());
        assertEquals(defaultBorder, leaf.getBorder());
    }

    public void testFill() {
        LeafMapItem leaf;

        leaf = new LeafMapItem(defaultSize, defaultFill, defaultBorder, defaultName, defaultMethod, defaultInteractive);
        assertEquals(defaultFill, leaf.getFill());

        leaf.setFill("orange");
        assertEquals("orange", leaf.getFill());

        leaf.setFill("red");
        assertEquals("red", leaf.getFill());
    }

    public void testRenderSVGWithNoNCSSDoesNothing() {
        Element parentElement;
        LeafMapItem leaf;
        PanopticodeProject project;
        Rect bounds;

        leaf = new LeafMapItem(defaultSize, defaultFill, defaultBorder, defaultName, defaultMethod, defaultInteractive);

        bounds = new Rect(10, 10, 50, 50);
        parentElement = createDummyElement();
        project = createDummyProject();

        leaf.renderSVG(bounds, parentElement, project);

        assertNull(parentElement.element("rect"));
    }

    public void testRenderInteractiveSVG() {
        Element parentElement;
        IntegerMetricDeclaration ccnDeclaration;
        IntegerMetricDeclaration ncssDeclaration;
        LeafMapItem leaf;
        PanopticodeProject panopticodeProject;
        PanopticodePackage panopticodePackage;
        PanopticodeFile panopticodeFile;
        PanopticodeClass panopticodeClass;
        Rect bounds;
        int ccn;
        int size;
        double x, y, width, height;

        ncssDeclaration = new IntegerMetricDeclaration(null, "NCSS");
        ncssDeclaration.addLevel(Level.METHOD);

        ccnDeclaration = new IntegerMetricDeclaration(null, "CCN");
        ccnDeclaration.addLevel(Level.METHOD);

        size = 34;
        defaultMethod.addMetric(ncssDeclaration.createMetric(size));
        ccn = 1;
        defaultMethod.addMetric(ccnDeclaration.createMetric(ccn));

        leaf = new LeafMapItem(size, defaultFill, defaultBorder, defaultName, defaultMethod, true);

        x = 15;
        y = 20;
        width = 90;
        height = 70;

        bounds = new Rect(x, y, width, height);

        panopticodeProject = createDummyProject();
        panopticodePackage = panopticodeProject.createAndAddPackage("org.panopticode");
        panopticodeFile = panopticodePackage.createAndAddFile("src/org/panopticode/Foo.java", "Foo.java");
        panopticodeClass = panopticodeFile.createAndAddClass("foo", 1, 1);
        defaultMethod.setParentClass(panopticodeClass);

        parentElement = createDummyElement();

        leaf.renderSVG(bounds, parentElement, panopticodeProject);

        Element rectElement = parentElement.element("rect");

        assertNotNull(rectElement);
        assertEquals(String.valueOf(x), rectElement.attributeValue("x"));
        assertEquals(String.valueOf(y), rectElement.attributeValue("y"));
        assertEquals(String.valueOf(width), rectElement.attributeValue("width"));
        assertEquals(String.valueOf(height), rectElement.attributeValue("height"));
        assertEquals(defaultFill, rectElement.attributeValue("fill"));
        assertEquals(defaultBorder, rectElement.attributeValue("stroke"));
        assertEquals("1", rectElement.attributeValue("stroke-width"));

        StringBuilder sb = new StringBuilder();
        sb.append("showDetails('");
        sb.append(panopticodeProject.getName());
        sb.append("', [], '");
        sb.append(panopticodePackage.getName());
        sb.append("', [], '");
        sb.append(panopticodeFile.getName());
        sb.append("', [], '");
        sb.append(panopticodeClass.getName());
        sb.append("', [], '");
        sb.append(defaultMethod.getShortSignature());
        sb.append("', [['CCN', '");
        sb.append(ccn);
        sb.append("'], ['NCSS', '");
        sb.append(size);
        sb.append("']]);");

        assertEquals(sb.toString(), rectElement.attributeValue("onclick"));
    }

    public void testRenderStaticSVG() {
        Element parentElement;
        IntegerMetricDeclaration ncssDeclaration;
        LeafMapItem leaf;
        PanopticodeProject project;
        Rect bounds;
        int size;
        double x, y, width, height;

        ncssDeclaration = new IntegerMetricDeclaration(null, "NCSS");
        ncssDeclaration.addLevel(Level.CLASS);
        ncssDeclaration.addLevel(Level.METHOD);

        size = 23;
        defaultMethod.addMetric(ncssDeclaration.createMetric(size));

        leaf = new LeafMapItem(size, defaultFill, defaultBorder, defaultName, defaultMethod, false);

        x = 0;
        y = 0;
        width = 100;
        height = 100;

        bounds = new Rect(x, y, width, height);
        project = createDummyProject();
        parentElement = createDummyElement();

        leaf.renderSVG(bounds, parentElement, project);

        Element rectElement = parentElement.element("rect");

        assertNotNull(rectElement);
        assertEquals(String.valueOf(x), rectElement.attributeValue("x"));
        assertEquals(String.valueOf(y), rectElement.attributeValue("y"));
        assertEquals(String.valueOf(width), rectElement.attributeValue("width"));
        assertEquals(String.valueOf(height), rectElement.attributeValue("height"));
        assertEquals(defaultFill, rectElement.attributeValue("fill"));
        assertEquals(defaultBorder, rectElement.attributeValue("stroke"));
        assertEquals("1", rectElement.attributeValue("stroke-width"));
    }

    public void testSizeIsNotChangedBySetter() {
        LeafMapItem leaf;

        leaf = new LeafMapItem(defaultSize, defaultFill, defaultBorder, defaultName, defaultMethod, defaultInteractive);
        assertEquals(defaultSize, leaf.getSize());

        leaf.setSize(3445);
        assertEquals(defaultSize, leaf.getSize());

        leaf.setSize(73.34);
        assertEquals(defaultSize, leaf.getSize());
    }
}
