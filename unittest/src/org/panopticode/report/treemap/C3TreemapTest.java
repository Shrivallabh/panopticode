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

        categorizer = treemap.getCategorizer();

        categoryList = categorizer.getCategories();

        veryLowC3 = categoryList.get(0);
        lowC3 = categoryList.get(1);
        mediumC3 = categoryList.get(2);
        highC3 = categoryList.get(3);

        unknownC3 = categorizer.getDefaultCategory();


        assertEquals(veryLowC3, categoryForC3Of(categorizer, 0.0));
        assertEquals(veryLowC3, categoryForC3Of(categorizer, 0.25));
        assertEquals(lowC3,     categoryForC3Of(categorizer, 0.35));
        assertEquals(mediumC3,  categoryForC3Of(categorizer, 0.65));
        assertEquals(highC3,    categoryForC3Of(categorizer, 0.95));

        assertEquals(unknownC3, categorizer.getCategory(createDummyFile()));
    }

    public void testTitle() {
        PanopticodeProject project;

        project = createDummyProject();

        assertEquals("Hello World C3", treemap.getTitle(project));
    }


    private Category categoryForC3Of(Categorizer categorizer, final double c3Indicator) {
        PanopticodePackage pkg;
    	PanopticodeFile target;
        PanopticodeProject project;

        DecimalMetricDeclaration decimalMetricDeclaration;
 
        project = createDummyProject();

        pkg=project.createAndAddPackage("org.norg");
        target = pkg.createAndAddFile("prod\\src\\test.java", "test.java");


        decimalMetricDeclaration = new DecimalMetricDeclaration(createDummySupplement(), "C3 Indicator");
        target.addMetric(decimalMetricDeclaration.createMetric(c3Indicator));

        
        return categorizer.getCategory(target);
    }
}
