package org.panopticode.supplement.jdepend;

import junit.framework.TestCase;
import org.panopticode.*;
import static org.panopticode.TestHelpers.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;

import java.util.Map;
import java.util.List;

public class JDependSupplementTest extends TestCase {
    private JDependSupplement supplement = new JDependSupplement();

    public void testAbstractClassesMetricDeclaration() {
        assertMetricDeclaredProperly("Abstract Class Count",
                                     "The number of abstract (declared abstract or an interface) classes in the package.");
    }

    public void testAbstractnessMetricDeclaration() {
        assertMetricDeclaredProperly("Abstractness (A)",
                                     "The ratio of the number of abstract classes (and interfaces) in the analyzed package to the total number of classes in the analyzed package.  The range for this metric is 0 to 1, with A=0 indicating a completely concrete package and A=1 indicating a completely abstract package.");
    }

    public void testAfferentCouplingMetricDeclaration() {
        assertMetricDeclaredProperly("Afferent Coupling Count (Ca)",
                                     "The number of other packages that depend upon classes within this package.  This is an indicator of the package's responsibility.");
    }

    public void testBuildElementMap() throws DocumentException {
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<JDepend>");
        sb.append("    <Packages>");
        sb.append("        <Package name='org.panopticode.foo'>");
        sb.append("            <error>No stats available: package referenced, but not analyzed.</error>");
        sb.append("        </Package>");
        sb.append("        <Package name='Default'>");
        sb.append("            <Stats />");
        sb.append("        </Package>");
        sb.append("        <Package name='org.panopticode'>");
        sb.append("            <Stats />");
        sb.append("        </Package>");
        sb.append("    </Packages>");
        sb.append("</JDepend>");

        Document document = DocumentHelper.parseText(sb.toString());

        Map<String, Element> map = supplement.buildElementMap(document);

        assertEquals(2, map.keySet().size());

        assertEquals("Default", map.get("").attributeValue("name"));
        assertEquals("org.panopticode", map.get("org.panopticode").attributeValue("name"));

        assertFalse(map.containsKey("org.panopticode.foo"));
    }
    
    public void testConcreteClassesMetricDeclaration() {
        assertMetricDeclaredProperly("Concrete Class Count",
                                     "The number of concrete (not abstract or an interface) classes in the package.");
    }

    public void testCorrectMetricsDeclared() {
        SupplementDeclaration supplementDeclaration = null;

        supplementDeclaration = supplement.getDeclaration();

        for(MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
            String name = metricDeclaration.getName();

            if("Total Class Count".equals(name) ||
               "Concrete Class Count".equals(name) ||
               "Abstract Class Count".equals(name) ||
               "Afferent Coupling Count (Ca)".equals(name) ||
               "Efferent Coupling Count (Ce)".equals(name) ||
               "Abstractness (A)".equals(name) ||
               "Instability (I)".equals(name) ||
               "Distance (D)".equals(name) ||
               "Volatility (V)".equals(name) ||
               "Efferent Coupling".equals(name) ||
               "Afferent Coupling".equals(name)) {
                // All is well
            } else {
                fail("Improper declaration found: " + metricDeclaration.getName());
            }
        }
    }

    public void testDistanceMetricDeclaration() {
        assertMetricDeclaredProperly("Distance (D)",
                                     "Distance from the main sequence is the perpendicular distance of a package from the idealized line A + I = 1. This metric is an indicator of the package's balance between abstractness and stability.  A package squarely on the main sequence is optimally balanced with respect to its abstractness and stability. Ideal packages are either completely abstract and stable (x=0, y=1) or completely concrete and instable (x=1, y=0).  The range for this metric is 0 to 1, with D=0 indicating a package that is coincident with the main sequence and D=1 indicating a package that is as far from the main sequence as possible.");
    }

    public void testEfferentCouplingMetricDeclaration() {
        assertMetricDeclaredProperly("Efferent Coupling Count (Ce)",
                                     "The number of other packages that the classes in the package depend upon.  This is an indicator of the package's independence.");
    }

    public void testFormatPackageName() {
        assertEquals("org.panopticode.supplement", supplement.formatName("org.panopticode.supplement"));
        assertEquals("Default", supplement.formatName(""));
    }

    public void testInstabilityMetricDeclaration() {
        assertMetricDeclaredProperly("Instability (I)",
                                     "The ratio of efferent coupling (Ce) to total coupling (Ce / (Ce + Ca)). This metric is an indicator of the package's resilience to change.  The range for this metric is 0 to 1, with I=0 indicating a completely stable package and I=1 indicating a completely instable package.");
    }

    public void testIsSupplement() {
        //noinspection ConstantConditions
        assertTrue(supplement instanceof Supplement);
    }

    public void testLoadPackageData() throws DocumentException {
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<JDepend>");
        sb.append("    <Packages>");
        sb.append("        <Package name='Default'>");
        sb.append("            <Stats>");
        sb.append("                <TotalClasses>22</TotalClasses>");
        sb.append("                <ConcreteClasses>21</ConcreteClasses>");
        sb.append("                <AbstractClasses>1</AbstractClasses>");
        sb.append("                <Ca>38</Ca>");
        sb.append("                <Ce>8</Ce>");
        sb.append("                <A>0.05</A>");
        sb.append("                <I>0.17</I>");
        sb.append("                <D>0.78</D>");
        sb.append("                <V>1</V>");
        sb.append("            </Stats>");
        sb.append("        </Package>");
        sb.append("    </Packages>");
        sb.append("</JDepend>");

        Document document = DocumentHelper.parseText(sb.toString());

        PanopticodeProject project = createDummyProject();
        project.createAndAddPackage("nope");
        PanopticodePackage panopticodePackage = project.createAndAddPackage("");

        SupplementDeclaration declaration = supplement.getDeclaration();
        Map<String, Element> map = supplement.buildElementMap(document);

        supplement.loadPackageData(project, map);

        assertIntegerMetricValue(22, panopticodePackage, "Total Class Count");
        assertIntegerMetricValue(21, panopticodePackage, "Concrete Class Count");
        assertIntegerMetricValue(1, panopticodePackage, "Abstract Class Count");
        assertIntegerMetricValue(38, panopticodePackage, "Afferent Coupling Count (Ca)");
        assertIntegerMetricValue(8, panopticodePackage, "Efferent Coupling Count (Ce)");
        assertDecimalMetricValue(0.05, panopticodePackage, "Abstractness (A)");
        assertDecimalMetricValue(0.17, panopticodePackage, "Instability (I)");
        assertDecimalMetricValue(0.78, panopticodePackage, "Distance (D)");
        assertIntegerMetricValue(1, panopticodePackage, "Volatility (V)");

        assertEquals(1, declaration.getErrors().size());
        assertEquals("WARNING - JDependSupplement - Could not find match for package 'nope'", declaration.getErrors().get(0));
    }

    private void assertDecimalMetricValue(Double value, PanopticodePackage panopticodePackage, String metricName) {
        DecimalMetric metric = (DecimalMetric) panopticodePackage.getMetricByName(metricName);

        assertEquals(value, metric.getValue(), 0.001);
    }

    private void assertIntegerMetricValue(Integer value, PanopticodePackage panopticodePackage, String metricName) {
        IntegerMetric metric = (IntegerMetric) panopticodePackage.getMetricByName(metricName);

        assertEquals(value, metric.getValue());
    }

    public void testMetricsDeclaredOnlyOnce() {
        assertSame(supplement.getDeclaration(), supplement.getDeclaration());
    }

    public void testSupplementDeclarationAddedToProjectOnLoad() {
        List<SupplementDeclaration> declared;
        PanopticodeProject project;

        project = createDummyProject();

        supplement.loadData(project, new String[] { "sample_data/jdepend.xml" });

        declared = project.getSupplementsDeclared();

        assertEquals(1, declared.size());
        assertEquals(supplement.getClass().getName(), declared.get(0).getSupplementClass());
    }

    public void testThrowsExceptionWhenFileNotFound() {
        PanopticodeProject project;

        project = createDummyProject();

        try {
            supplement.loadData(project, new String[] { "sample_data/jdepend-nope.xml" });
            fail();
        } catch(RuntimeException e) {
            // Desired behaviour
        }

    }

    public void testTotalClassesMetricDeclaration() {
        assertMetricDeclaredProperly("Total Class Count",
                                     "The number of concrete and abstract classes (and interfaces) in the package.");
    }

    public void testVolatilityMetricDeclaration() {
        assertMetricDeclaredProperly("Volatility (V)",
                                     "Packages that are not expected to change can be specifically configured with a volatility (V) value in the jdepend.properties file. V can either be 0 or 1. If V=0, meaning the package is not at all subject to change, then the package will automatically fall directly on the main sequence (D=0). If V=1, meaning that the package is subject to change, then the distance from the main sequence is not affected. By default, all packages are configured with V=1.");
    }

    private void assertMetricDeclaredProperly(String name, String description) {
        boolean metricFound = false;
        SupplementDeclaration supplementDeclaration = null;

        supplementDeclaration = supplement.getDeclaration();

        for(MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
            if(name.equals(metricDeclaration.getName())) {
                assertEquals(description, metricDeclaration.getDescription());
                assertOnlyDeclaredAtPackageLevel(metricDeclaration);

                metricFound = true;
            }
        }

        assertTrue(metricFound);
    }

    private void assertOnlyDeclaredAtPackageLevel(MetricDeclaration metricDeclaration) {
        assertFalse(metricDeclaration.isLevel(Level.PROJECT));
        assertTrue(metricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(metricDeclaration.isLevel(Level.FILE));
        assertFalse(metricDeclaration.isLevel(Level.CLASS));
        assertFalse(metricDeclaration.isLevel(Level.METHOD));
    }
}

