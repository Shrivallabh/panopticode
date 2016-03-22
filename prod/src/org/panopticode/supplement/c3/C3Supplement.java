package org.panopticode.supplement.c3;

import static org.panopticode.util.IndicatorUtil.computeC3Indicator;
import static org.panopticode.util.IndicatorUtil.computeMaxCCNIndicator;

import org.panopticode.DecimalMetric;
import org.panopticode.DecimalMetricDeclaration;
import org.panopticode.IntegerMetric;
import org.panopticode.Level;
import org.panopticode.PanopticodeFile;
import org.panopticode.PanopticodeProject;
import org.panopticode.RatioMetric;
import org.panopticode.RatioMetricDeclaration;
import org.panopticode.Supplement;
import org.panopticode.SupplementDeclaration;

public class C3Supplement implements Supplement {

	private DecimalMetricDeclaration c3Declaration;
	private SupplementDeclaration declaration;

	@Override
	public void loadData(PanopticodeProject project, String[] arguments) {
	        project.addSupplementDeclaration(getDeclaration());

			for(PanopticodeFile file : project.getFiles()) {
				DecimalMetric linesChangedIndicator=(DecimalMetric) file.getMetricByName("Lines Changed Indicator");
				DecimalMetric changeFrequencyIndicator=(DecimalMetric) file.getMetricByName("Change Frequency Indicator");
				RatioMetric coverage = (RatioMetric) file.getMetricByName("Line Coverage");
				IntegerMetric maxComplexity = (IntegerMetric) file.getMetricByName("MAX-CCN");
				if(linesChangedIndicator==null || changeFrequencyIndicator==null || coverage == null || maxComplexity == null) {
					continue;
				}
				double c3Indicator = computeC3Indicator(linesChangedIndicator.getValue(), changeFrequencyIndicator.getValue(), coverage.getPercentValue()/100, computeMaxCCNIndicator(maxComplexity.getValue()));
				file.addMetric(c3Declaration.createMetric(c3Indicator));
			}
	}

    public SupplementDeclaration getDeclaration() {
        if (declaration == null) {
            c3Declaration = new DecimalMetricDeclaration(this, "C3 Indicator");
            c3Declaration.addLevel(Level.FILE);

            declaration = new SupplementDeclaration(this.getClass().getName());
            declaration.addMetricDeclaration(c3Declaration);
        }

        return declaration;
    }

}
