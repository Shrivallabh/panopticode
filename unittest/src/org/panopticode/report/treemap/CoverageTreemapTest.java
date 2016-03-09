package org.panopticode.report.treemap;

import junit.framework.TestCase;

import java.util.List;

import org.panopticode.*;

import static org.panopticode.TestHelpers.*;

public class CoverageTreemapTest extends TestCase {
    CoverageTreemap treemap = new CoverageTreemap();

    public void testCategorizerThesholds() {
        Category noCoverage;
        Category lowCoverage;
        Category mediumCoverage;
        Category highCoverage;
        Category unknownCoverage;
        Categorizer categorizer;
        List<Category> categoryList;

        categorizer = treemap.getCategorizer();

        categoryList = categorizer.getCategories();

        highCoverage = categoryList.get(0);
        mediumCoverage = categoryList.get(1);
        lowCoverage = categoryList.get(2);
        noCoverage = categoryList.get(3);

        unknownCoverage = categorizer.getDefaultCategory();

        assertEquals(noCoverage,     categoryForCoverageOf(categorizer,  0.0));
        assertEquals(lowCoverage,    categoryForCoverageOf(categorizer,  0.001));
        assertEquals(lowCoverage,    categoryForCoverageOf(categorizer, 0.499));
        assertEquals(mediumCoverage, categoryForCoverageOf(categorizer, 0.500));
        assertEquals(mediumCoverage, categoryForCoverageOf(categorizer, 0.749));
        assertEquals(highCoverage,   categoryForCoverageOf(categorizer, 0.750));
        assertEquals(highCoverage,   categoryForCoverageOf(categorizer,1.000));

        assertEquals(unknownCoverage, categorizer.getCategory(createDummyMethod()));
    }

    public void testTitle() {
        PanopticodeProject project;

        project = createDummyProject();

        assertEquals("Hello World Code Coverage", treemap.getTitle(project));
    }

    private Category categoryForCoverageOf(Categorizer categorizer, final double lineCoverage) {
        PanopticodeMethod target;
        DecimalMetricDeclaration metricDeclaration;


        metricDeclaration = new DecimalMetricDeclaration(createDummySupplement(), "Line Coverage");

        target = createDummyMethod();
        target.addMetric(metricDeclaration.createMetric(lineCoverage));

        return categorizer.getCategory(target);
    }
}
