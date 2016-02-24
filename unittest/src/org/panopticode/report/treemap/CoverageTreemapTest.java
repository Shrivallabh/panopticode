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
        assertEquals(lowCoverage,    categoryForCoverageOf(categorizer,  0.1));
        assertEquals(lowCoverage,    categoryForCoverageOf(categorizer, 49.9));
        assertEquals(mediumCoverage, categoryForCoverageOf(categorizer, 50.0));
        assertEquals(mediumCoverage, categoryForCoverageOf(categorizer, 74.9));
        assertEquals(highCoverage,   categoryForCoverageOf(categorizer, 75.0));
        assertEquals(highCoverage,   categoryForCoverageOf(categorizer,100.0));

        assertEquals(unknownCoverage, categorizer.getCategory(createDummyMethod()));
    }

    public void testTitle() {
        PanopticodeProject project;

        project = createDummyProject();

        assertEquals("Hello World Code Coverage", treemap.getTitle(project));
    }

    private Category categoryForCoverageOf(Categorizer categorizer, final double lineCoverage) {
        PanopticodeMethod target;
        RatioMetricDeclaration metricDeclaration;


        metricDeclaration = new RatioMetricDeclaration(createDummySupplement(), "Line Coverage");

        target = createDummyMethod();
        target.addMetric(metricDeclaration.createMetric(lineCoverage, 100.0));

        return categorizer.getCategory(target);
    }
}
