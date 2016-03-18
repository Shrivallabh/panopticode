package org.panopticode.report.treemap;

import java.util.LinkedList;
import java.util.List;

import org.panopticode.DecimalMetric;
import org.panopticode.IntegerMetric;
import org.panopticode.MetricTarget;
import org.panopticode.PanopticodeFile;
import org.panopticode.PanopticodeMethod;
import org.panopticode.PanopticodeProject;
import org.panopticode.RatioMetric;
import static org.panopticode.util.IndicatorUtil.*;

public class C3Treemap extends BaseFileTreemap  {
	private Categorizer categorizer;
	
	public C3Treemap() {
        DefaultCategory defaultCategory = new DefaultCategory("N/A", "blue", "black");

        List<Category> categories = new LinkedList<Category>();
        categories.add(new C3Category(0.3, "C3 [0-0.3)",   "green",  "black"));
        categories.add(new C3Category(0.6, "C3 [0.3-0.6)", "yellow", "black"));
        categories.add(new C3Category(0.9, "C3 [0.6-0.9)", "red",    "black"));
        categories.add(new C3Category(Double.MAX_VALUE, "C3 [0.9-1]", "black", "red"));

        categorizer = new Categorizer(categories, defaultCategory);

	}
	
	@Override
	public Categorizer getCategorizer() {
		return categorizer;
	}

	@Override
	public String getTitle(PanopticodeProject project) {
        return project.getName() + " C3";
	}

	
	public class C3Category extends Category  {
        private double lessThanExclusive;
        public C3Category(double lessThanExclusive,
                                  String legendLabel,
                                  String fill,
                                  String border) {
            super(legendLabel, fill, border);
            this.lessThanExclusive = lessThanExclusive;
        }

        public boolean inCategory(MetricTarget toCheck) {
        	if(toCheck instanceof PanopticodeFile) {
        		PanopticodeFile file = (PanopticodeFile) toCheck;
        		PanopticodeProject project = file.getParentPackage().getParentProject();
        		DecimalMetric linesChangedIndicator=(DecimalMetric) file.getMetricByName("Lines Changed Indicator");
        		DecimalMetric changeFrequencyInficator=(DecimalMetric) file.getMetricByName("Change Frequency Indicator");
        		RatioMetric coverage = (RatioMetric) file.getMetricByName("Line Coverage");
        		IntegerMetric maxComplexity = (IntegerMetric) file.getMetricByName("MAX-CCN");
        		if(linesChangedIndicator==null || changeFrequencyInficator==null || coverage == null || maxComplexity == null) {
        			return false;
        		}
        		double c3Indicator = computeC3Indicator(linesChangedIndicator.getValue(), changeFrequencyInficator.getValue(), coverage.getPercentValue()/100, computeMaxCCNIndicator(maxComplexity.getValue()));
        		return c3Indicator < lessThanExclusive;
        	}
            return false;
        }
	}
}
