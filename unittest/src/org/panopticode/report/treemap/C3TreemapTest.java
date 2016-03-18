package org.panopticode.report.treemap;

import junit.framework.TestCase;

import java.util.List;

import org.panopticode.*;
import org.panopticode.util.IndicatorUtil;

import static org.panopticode.TestHelpers.*;

public class C3TreemapTest extends TestCase {
    C3Treemap treemap = new C3Treemap();

    public void testCategorizerThesholds() {
        Category veryLowC3;
        Category lowC3;
        Category mediumC3;
        Category highC3;
        Category unknownC3;
        Categorizer categorizer;
        List<Category> categoryList;

        PanopticodeProject project;

        categorizer = treemap.getCategorizer();

        categoryList = categorizer.getCategories();

        veryLowC3 = categoryList.get(0);
        lowC3 = categoryList.get(1);
        mediumC3 = categoryList.get(2);
        highC3 = categoryList.get(3);

        unknownC3 = categorizer.getDefaultCategory();


        assertEquals(veryLowC3, categoryForC3Of(categorizer,   0,  0,0, 0,1));
        assertEquals(veryLowC3, categoryForC3Of(categorizer,   0,  1,1, 1,1));
        assertEquals(lowC3,     categoryForC3Of(categorizer,  95,  6,2,10,0.75));
        assertEquals(mediumC3,  categoryForC3Of(categorizer, 195,206,3,26,0.25));
        assertEquals(highC3,    categoryForC3Of(categorizer, 195,256,10,26,0.1));

        assertEquals(unknownC3, categorizer.getCategory(createDummyFile()));
    }

    public void testTitle() {
        PanopticodeProject project;

        project = createDummyProject();

        assertEquals("Hello World C3", treemap.getTitle(project));
    }


    private Category categoryForC3Of(Categorizer categorizer, final int linesAdded, final int linesRemoved, final int timesChanged, int maxCCN, double coverage) {
        PanopticodePackage pkg;
    	PanopticodeFile target;
        PanopticodeProject project;

        IntegerMetricDeclaration integerMetricDeclaration;
        DecimalMetricDeclaration decimalMetricDeclaration;
        RatioMetricDeclaration ratioMetricDeclation;
 
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

        integerMetricDeclaration = new IntegerMetricDeclaration(createDummySupplement(), "MAX-CCN");
        target.addMetric(integerMetricDeclaration.createMetric(maxCCN));

        decimalMetricDeclaration = new DecimalMetricDeclaration(createDummySupplement(), "Lines Changed Indicator");
        target.addMetric(decimalMetricDeclaration.createMetric(IndicatorUtil.computeLinesChangedIndicator(linesAdded+linesRemoved, 10)));

        decimalMetricDeclaration = new DecimalMetricDeclaration(createDummySupplement(), "Change Frequency Indicator");
        target.addMetric(decimalMetricDeclaration.createMetric(IndicatorUtil.computeChangeFrequencyIndicator(timesChanged, 10)));

        ratioMetricDeclation = new RatioMetricDeclaration(createDummySupplement(), "Line Coverage");
        target.addMetric(ratioMetricDeclation.createMetric(coverage, 1.0));
        
        return categorizer.getCategory(target);
    }
}
