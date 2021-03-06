package org.panopticode.report.treemap;

import junit.framework.TestCase;

import java.util.List;

import org.panopticode.*;
import org.panopticode.util.IndicatorUtil;

import static org.panopticode.TestHelpers.*;

public class ChurnTreemapTest extends TestCase {
    ChurnTreemap treemap = new ChurnTreemap();

    public void testCategorizerThesholds() {
        Category veryLowChurn;
        Category lowChurn;
        Category mediumChurn;
        Category highChurn;
        Category unknownChurn;
        Categorizer categorizer;
        List<Category> categoryList;

        PanopticodeProject project;

        categorizer = treemap.getCategorizer();

        categoryList = categorizer.getCategories();

        veryLowChurn = categoryList.get(0);
        lowChurn = categoryList.get(1);
        mediumChurn = categoryList.get(2);
        highChurn = categoryList.get(3);

        unknownChurn = categorizer.getDefaultCategory();


        assertEquals(veryLowChurn, categoryForChurnOf(categorizer,  0,   0,0));
        assertEquals(veryLowChurn, categoryForChurnOf(categorizer,  0,   1,1));
        assertEquals(lowChurn,     categoryForChurnOf(categorizer, 95,   6,2));
        assertEquals(mediumChurn,  categoryForChurnOf(categorizer, 95, 106,3));
        assertEquals(highChurn,    categoryForChurnOf(categorizer, 195,206,3));

        assertEquals(unknownChurn, categorizer.getCategory(createDummyFile()));
    }

    public void testTitle() {
        PanopticodeProject project;

        project = createDummyProject();

        assertEquals("Hello World churn", treemap.getTitle(project));
    }


    private Category categoryForChurnOf(Categorizer categorizer, final int linesAdded, final int linesRemoved, final int timesChanged) {
        PanopticodePackage pkg;
    	PanopticodeFile target;
        PanopticodeProject project;

        IntegerMetricDeclaration integerMetricDeclaration;
        DecimalMetricDeclaration decimalMetricDeclaration;
 
        project = createDummyProject();
        integerMetricDeclaration = new IntegerMetricDeclaration(createDummySupplement(), "Churn Duration");
        project.addMetric(integerMetricDeclaration.createMetric(10));

        pkg=project.createAndAddPackage("org.norg");
        target = pkg.createAndAddFile("prod\\src\\test.java", "test.java");


        integerMetricDeclaration = new IntegerMetricDeclaration(createDummySupplement(), "Lines Added");
        target.addMetric(integerMetricDeclaration.createMetric(linesAdded));

        integerMetricDeclaration = new IntegerMetricDeclaration(createDummySupplement(), "Lines Removed");
        target.addMetric(integerMetricDeclaration.createMetric(linesRemoved));

        integerMetricDeclaration = new IntegerMetricDeclaration(createDummySupplement(), "Times Changed");
        target.addMetric(integerMetricDeclaration.createMetric(timesChanged));

        decimalMetricDeclaration = new DecimalMetricDeclaration(createDummySupplement(), "Lines Changed Indicator");
        target.addMetric(decimalMetricDeclaration.createMetric(IndicatorUtil.computeLinesChangedIndicator(linesAdded+linesRemoved, 10)));

        decimalMetricDeclaration = new DecimalMetricDeclaration(createDummySupplement(), "Times Changed");
        target.addMetric(decimalMetricDeclaration.createMetric(IndicatorUtil.computeChangeFrequencyIndicator(timesChanged, 10)));

        return categorizer.getCategory(target);
    }
}
