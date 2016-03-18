package org.panopticode.supplement.git;

import static org.panopticode.TestHelpers.createDummyProject;

import java.util.List;

import org.panopticode.DecimalMetricDeclaration;
import org.panopticode.Level;
import org.panopticode.MetricDeclaration;
import org.panopticode.PanopticodeProject;
import org.panopticode.IntegerMetricDeclaration;
import org.panopticode.SupplementDeclaration;

import static org.panopticode.TestHelpers.*;
import junit.framework.TestCase;

public class GitChurnSupplementTest extends TestCase {

	GitChurnSupplement supplement = new GitChurnSupplement();

	public void testSupplementDeclarationAddedToProjectOnLoad() {
        List<SupplementDeclaration> declared;
        PanopticodeProject project;

        project = createDummyProject();
        supplement.loadData(project, new String[] { "26/01/2016", "26/02/2016", "sample_data/git-log.log" });

        declared = project.getSupplementsDeclared();
        assertEquals(1, declared.size());
        assertEquals(supplement.getClass().getName(), declared.get(0).getSupplementClass());
    }

	public void testMetricsDeclaredOnlyOnce() {
        assertSame(supplement.getDeclaration(), supplement.getDeclaration());
    }

	public void testMetricsDeclaredProperly() {
        SupplementDeclaration supplementDeclaration = null;
        IntegerMetricDeclaration linesAddedMetricDeclaration = null;
        IntegerMetricDeclaration linesRemovedMetricDeclaration = null;
        IntegerMetricDeclaration timesChangedMetricDeclaration = null;
        DecimalMetricDeclaration linesChangedIndicatorMetricDeclaration = null;
        DecimalMetricDeclaration changeFrequencyIndicatorMetricDeclaration = null;
        IntegerMetricDeclaration churnDurationMetricDeclaration = null;

        supplementDeclaration = supplement.getDeclaration();

        for(MetricDeclaration metricDeclaration : supplementDeclaration.getMetricsDeclared()) {
            if("Lines Added".equals(metricDeclaration.getName())) {
                linesAddedMetricDeclaration = (IntegerMetricDeclaration) metricDeclaration;
            } else if ("Lines Removed".equals(metricDeclaration.getName())) {
                linesRemovedMetricDeclaration = (IntegerMetricDeclaration) metricDeclaration;
            } else if ("Times Changed".equals(metricDeclaration.getName())) {
                timesChangedMetricDeclaration = (IntegerMetricDeclaration) metricDeclaration;
            } else if ("Churn Duration".equals(metricDeclaration.getName())) {
            	churnDurationMetricDeclaration = (IntegerMetricDeclaration) metricDeclaration;
            } else if ("Lines Changed Indicator".equals(metricDeclaration.getName())) {
            	linesChangedIndicatorMetricDeclaration = (DecimalMetricDeclaration) metricDeclaration;
            } else if ("Change Frequency Indicator".equals(metricDeclaration.getName())) {
            	changeFrequencyIndicatorMetricDeclaration = (DecimalMetricDeclaration) metricDeclaration;
            } else {
                fail("Improper declaration found: " + metricDeclaration.getName());
            }
        }

        assertFalse(linesAddedMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(linesAddedMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(linesAddedMetricDeclaration.isLevel(Level.FILE));
        assertFalse(linesAddedMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(linesAddedMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(linesRemovedMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(linesRemovedMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(linesRemovedMetricDeclaration.isLevel(Level.FILE));
        assertFalse(linesRemovedMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(linesRemovedMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(timesChangedMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(timesChangedMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(timesChangedMetricDeclaration.isLevel(Level.FILE));
        assertFalse(timesChangedMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(timesChangedMetricDeclaration.isLevel(Level.METHOD));

        assertTrue(churnDurationMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(churnDurationMetricDeclaration.isLevel(Level.PACKAGE));
        assertFalse(churnDurationMetricDeclaration.isLevel(Level.FILE));
        assertFalse(churnDurationMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(churnDurationMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(linesChangedIndicatorMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(linesChangedIndicatorMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(linesChangedIndicatorMetricDeclaration.isLevel(Level.FILE));
        assertFalse(linesChangedIndicatorMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(linesChangedIndicatorMetricDeclaration.isLevel(Level.METHOD));

        assertFalse(changeFrequencyIndicatorMetricDeclaration.isLevel(Level.PROJECT));
        assertFalse(changeFrequencyIndicatorMetricDeclaration.isLevel(Level.PACKAGE));
        assertTrue(changeFrequencyIndicatorMetricDeclaration.isLevel(Level.FILE));
        assertFalse(changeFrequencyIndicatorMetricDeclaration.isLevel(Level.CLASS));
        assertFalse(changeFrequencyIndicatorMetricDeclaration.isLevel(Level.METHOD));

	}
}
