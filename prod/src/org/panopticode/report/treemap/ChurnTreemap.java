package org.panopticode.report.treemap;

import java.util.LinkedList;
import java.util.List;

import org.panopticode.DecimalMetric;
import org.panopticode.IntegerMetric;
import org.panopticode.MetricTarget;
import org.panopticode.PanopticodeFile;
import org.panopticode.PanopticodeMethod;
import org.panopticode.PanopticodeProject;

public class ChurnTreemap extends BaseFileTreemap  {
	private Categorizer categorizer;
	
	public ChurnTreemap() {
        DefaultCategory defaultCategory = new DefaultCategory("N/A", "blue", "black");

        List<Category> categories = new LinkedList<Category>();
        categories.add(new ChurnCategory(0.3, "LCI [0.0-0.3)",  "green",  "black"));
        categories.add(new ChurnCategory(0.6,  "LCI [0.3-0.6)", "yellow", "black"));
        categories.add(new ChurnCategory(0.9, "LCI [0.6-0.9)",  "red",    "black"));
        categories.add(new ChurnCategory(Double.MAX_VALUE, "CI [0.9-1.0]", "black", "red"));

        categorizer = new Categorizer(categories, defaultCategory);

	}
	
	@Override
	public Categorizer getCategorizer() {
		return categorizer;
	}

	@Override
	public String getTitle(PanopticodeProject project) {
        return project.getName() + " churn";
	}

	
	public class ChurnCategory extends Category  {
        private double lessThanExclusive;
        public ChurnCategory(double lessThanExclusive,
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
        		DecimalMetric metric=(DecimalMetric) file.getMetricByName("Lines Changed Indicator");
        		if(metric==null) {
        			return false;
        		}
        		return metric.getValue() < lessThanExclusive;
        	}
            return false;
        }
	}
	
}
