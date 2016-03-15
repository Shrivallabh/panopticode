package org.panopticode.report.treemap;

import java.util.LinkedList;
import java.util.List;

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
        categories.add(new ChurnCategory(10, "CPD 0-9", "green", "black"));
        categories.add(new ChurnCategory(20, "CPD 10-19", "yellow", "black"));
        categories.add(new ChurnCategory(30, "CPD 20-29", "red", "black"));
        categories.add(new ChurnCategory(Integer.MAX_VALUE, "CCN 30+", "black", "red"));

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
        public ChurnCategory(int lessThanExclusive,
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
        		IntegerMetric lineAdded = (IntegerMetric)file.getMetricByName("Lines Added");
        		IntegerMetric linesRemoved = (IntegerMetric)file.getMetricByName("Lines Removed");
        		IntegerMetric duration = (IntegerMetric)project.getMetricByName("Churn Duration");
        		if(lineAdded==null || linesRemoved==null|| duration==null) {
        			return false;
        		}
        		int churnPerDay = (linesRemoved.getValue()+lineAdded.getValue())/duration.getValue();
        		return churnPerDay < lessThanExclusive;
        	}
            return false;
        }
	}
	
}
