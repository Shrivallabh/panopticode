package org.panopticode.supplement.jdepend;

import org.panopticode.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.util.Map;
import java.util.HashMap;

public class JDependSupplement implements Supplement {
    private DecimalMetricDeclaration abstractnessDeclaration;
    private DecimalMetricDeclaration distanceDeclaration;
    private DecimalMetricDeclaration instabilityDeclaration;
    private IntegerMetricDeclaration abstractClassesDeclaration;

    private IntegerMetricDeclaration afferentCouplingDeclaration;
    private IntegerMetricDeclaration concreteClassesDeclaration;
    private IntegerMetricDeclaration efferentCouplingDeclaration;
    private IntegerMetricDeclaration totalClassesDeclaration;
    private IntegerMetricDeclaration volatilityDeclaration;

    private SupplementDeclaration declaration;

    public SupplementDeclaration getDeclaration() {
        if (declaration == null) {
            totalClassesDeclaration = new IntegerMetricDeclaration(this, "Total Class Count");
            totalClassesDeclaration.addLevel(Level.PACKAGE);
            totalClassesDeclaration.setDescription("The number of concrete and abstract classes (and interfaces) in the package.");

            concreteClassesDeclaration = new IntegerMetricDeclaration(this, "Concrete Class Count");
            concreteClassesDeclaration.addLevel(Level.PACKAGE);
            concreteClassesDeclaration.setDescription("The number of concrete (not abstract or an interface) classes in the package.");

            abstractClassesDeclaration = new IntegerMetricDeclaration(this, "Abstract Class Count");
            abstractClassesDeclaration.addLevel(Level.PACKAGE);
            abstractClassesDeclaration.setDescription("The number of abstract (declared abstract or an interface) classes in the package.");

            afferentCouplingDeclaration = new IntegerMetricDeclaration(this, "Afferent Coupling Count (Ca)");
            afferentCouplingDeclaration.addLevel(Level.PACKAGE);
            afferentCouplingDeclaration.setDescription("The number of other packages that depend upon classes within this package.  This is an indicator of the package's responsibility.");

            efferentCouplingDeclaration = new IntegerMetricDeclaration(this, "Efferent Coupling Count (Ce)");
            efferentCouplingDeclaration.addLevel(Level.PACKAGE);
            efferentCouplingDeclaration.setDescription("The number of other packages that the classes in the package depend upon.  This is an indicator of the package's independence.");

            abstractnessDeclaration = new DecimalMetricDeclaration(this, "Abstractness (A)");
            abstractnessDeclaration.addLevel(Level.PACKAGE);
            abstractnessDeclaration.setDescription("The ratio of the number of abstract classes (and interfaces) in the analyzed package to the total number of classes in the analyzed package.  The range for this metric is 0 to 1, with A=0 indicating a completely concrete package and A=1 indicating a completely abstract package.");

            instabilityDeclaration = new DecimalMetricDeclaration(this, "Instability (I)");
            instabilityDeclaration.addLevel(Level.PACKAGE);
            instabilityDeclaration.setDescription("The ratio of efferent coupling (Ce) to total coupling (Ce / (Ce + Ca)). This metric is an indicator of the package's resilience to change.  The range for this metric is 0 to 1, with I=0 indicating a completely stable package and I=1 indicating a completely instable package.");

            distanceDeclaration = new DecimalMetricDeclaration(this, "Distance (D)");
            distanceDeclaration.addLevel(Level.PACKAGE);
            distanceDeclaration.setDescription("Distance from the main sequence is the perpendicular distance of a package from the idealized line A + I = 1. This metric is an indicator of the package's balance between abstractness and stability.  A package squarely on the main sequence is optimally balanced with respect to its abstractness and stability. Ideal packages are either completely abstract and stable (x=0, y=1) or completely concrete and instable (x=1, y=0).  The range for this metric is 0 to 1, with D=0 indicating a package that is coincident with the main sequence and D=1 indicating a package that is as far from the main sequence as possible.");

            volatilityDeclaration = new IntegerMetricDeclaration(this, "Volatility (V)");
            volatilityDeclaration.addLevel(Level.PACKAGE);
            volatilityDeclaration.setDescription("Packages that are not expected to change can be specifically configured with a volatility (V) value in the jdepend.properties file. V can either be 0 or 1. If V=0, meaning the package is not at all subject to change, then the package will automatically fall directly on the main sequence (D=0). If V=1, meaning that the package is subject to change, then the distance from the main sequence is not affected. By default, all packages are configured with V=1.");

            declaration = new SupplementDeclaration(this.getClass().getName());
            declaration.addMetricDeclaration(totalClassesDeclaration);
            declaration.addMetricDeclaration(concreteClassesDeclaration);
            declaration.addMetricDeclaration(abstractClassesDeclaration);
            declaration.addMetricDeclaration(afferentCouplingDeclaration);
            declaration.addMetricDeclaration(efferentCouplingDeclaration);
            declaration.addMetricDeclaration(abstractnessDeclaration);
            declaration.addMetricDeclaration(instabilityDeclaration);
            declaration.addMetricDeclaration(distanceDeclaration);
            declaration.addMetricDeclaration(volatilityDeclaration);
        }

        return declaration;
    }

    public void loadData(PanopticodeProject project, String[] arguments) {
        project.addSupplementDeclaration(getDeclaration());

        SAXReader saxReader;
        Document document;

        saxReader = new SAXReader();
        try {
            document = saxReader.read(arguments[0]);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        loadPackageData(project, buildElementMap(document));
    }

    void loadPackageData(PanopticodeProject project, Map <String, Element> elementMap) {
        for (PanopticodePackage panopticodePackage : project.getPackages()) {
            String packageName = panopticodePackage.getName();

            if(elementMap.containsKey(packageName)) {
                Element packageElement = elementMap.remove(packageName);
                Element statsElement = packageElement.element("Stats");

                int totalClasses = Integer.parseInt(statsElement.elementText("TotalClasses"));
                int concreteClasses = Integer.parseInt(statsElement.elementText("ConcreteClasses"));
                int abstractClasses = Integer.parseInt(statsElement.elementText("AbstractClasses"));
                int afferentCouplings = Integer.parseInt(statsElement.elementText("Ca"));
                int efferentCouplings = Integer.parseInt(statsElement.elementText("Ce"));
                int volatility = Integer.parseInt(statsElement.elementText("V"));
                double abstractness = Double.parseDouble(statsElement.elementText("A"));
                double instability = Double.parseDouble(statsElement.elementText("I"));
                double distance = Double.parseDouble(statsElement.elementText("D"));

                panopticodePackage.addMetric(totalClassesDeclaration.createMetric(totalClasses));
                panopticodePackage.addMetric(concreteClassesDeclaration.createMetric(concreteClasses));
                panopticodePackage.addMetric(abstractClassesDeclaration.createMetric(abstractClasses));
                panopticodePackage.addMetric(afferentCouplingDeclaration.createMetric(afferentCouplings));
                panopticodePackage.addMetric(efferentCouplingDeclaration.createMetric(efferentCouplings));
                panopticodePackage.addMetric(abstractnessDeclaration.createMetric(abstractness));
                panopticodePackage.addMetric(instabilityDeclaration.createMetric(instability));
                panopticodePackage.addMetric(distanceDeclaration.createMetric(distance));
                panopticodePackage.addMetric(volatilityDeclaration.createMetric(volatility));
            } else {
                declaration.addError("WARNING - JDependSupplement - Could not find match for package '"
                        + panopticodePackage.getName() + "'");
            }
        }

        for (String packageName : elementMap.keySet()) {
            declaration.addError("WARNING - JDependSupplement - Package '"
                    + packageName + "' found by JDepend, but not Panopticode" );
        }
    }

    Map <String, Element> buildElementMap(Document document) {
        Map <String, Element> map;

        map = new HashMap<String, Element>();

        for (Object objPackageElement : document.getRootElement().element("Packages").elements("Package")) {
            Element packageElement;
            Element statsElement;

            packageElement = (Element) objPackageElement;
            statsElement = packageElement.element("Stats");


            if (statsElement != null) {
                String jDependPackageName;

                jDependPackageName = packageElement.attributeValue("name");

                if ("Default".equals(jDependPackageName)) {
                    jDependPackageName = "";
                }

                map.put(jDependPackageName, packageElement);
            }
        }

        return map;
    }

    public String formatName(String name) {
        if ("".equals(name)) {
            return "Default";
        }

        return name;
    }
}
