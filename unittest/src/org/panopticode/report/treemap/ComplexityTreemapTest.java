package org.panopticode.report.treemap;

import static org.panopticode.TestHelpers.createDummyMethod;
import static org.panopticode.TestHelpers.createDummyProject;
import static org.panopticode.TestHelpers.createDummySupplement;
import org.panopticode.PanopticodeProject;
import org.panopticode.PanopticodeMethod;
import org.panopticode.IntegerMetricDeclaration;

import java.util.List;

import junit.framework.TestCase;

public class ComplexityTreemapTest extends TestCase {
    ComplexityTreemap treemap = new ComplexityTreemap();

    public void testCategorizerThesholds() {
        Category lowComplexity;
        Category mediumComplexity;
        Category highComplexity;
        Category veryHighComplexity;
        Category unknownComplexity;
        Categorizer categorizer;
        List<Category> categoryList;

        categorizer = treemap.getCategorizer();

        categoryList = categorizer.getCategories();

        lowComplexity = categoryList.get(0);
        mediumComplexity = categoryList.get(1);
        highComplexity = categoryList.get(2);
        veryHighComplexity = categoryList.get(3);

        unknownComplexity = categorizer.getDefaultCategory();

        assertEquals(lowComplexity,      categoryForComplexityOf(categorizer,  1));
        assertEquals(lowComplexity,      categoryForComplexityOf(categorizer,  5));
        assertEquals(mediumComplexity,   categoryForComplexityOf(categorizer,  6));
        assertEquals(mediumComplexity,   categoryForComplexityOf(categorizer,  9));
        assertEquals(highComplexity,     categoryForComplexityOf(categorizer, 10));
        assertEquals(highComplexity,     categoryForComplexityOf(categorizer, 24));
        assertEquals(veryHighComplexity, categoryForComplexityOf(categorizer, 25));
        assertEquals(veryHighComplexity, categoryForComplexityOf(categorizer, Integer.MAX_VALUE - 1));

        assertEquals(unknownComplexity, categorizer.getCategory(createDummyMethod()));
    }

    public void testTitle() {
        PanopticodeProject project;

        project = createDummyProject();

        assertEquals("Hello World Complexity", treemap.getTitle(project));
    }

    private Category categoryForComplexityOf(Categorizer categorizer, final int ccn) {
        PanopticodeMethod target;
        IntegerMetricDeclaration metricDeclaration;

        metricDeclaration = new IntegerMetricDeclaration(createDummySupplement(), "CCN");

        target = createDummyMethod();
        target.addMetric(metricDeclaration.createMetric(ccn));

        return categorizer.getCategory(target);
    }
}
