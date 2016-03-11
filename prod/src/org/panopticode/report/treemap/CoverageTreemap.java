/*
 * Copyright (c) 2006-2007 Julias R. Shaw Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and
 * this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED
 * "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.panopticode.report.treemap;

import java.util.LinkedList;
import java.util.List;

import org.panopticode.MetricTarget;
import org.panopticode.PanopticodeProject;
import org.panopticode.RatioMetric;

public class CoverageTreemap extends BaseTreemap {
	private Categorizer categorizer;

	public CoverageTreemap() {
		super();

		DefaultCategory defaultCategory = new DefaultCategory("N/A", "blue", "black");

		List<Category> categories = new LinkedList<Category>();
		categories.add(new CoverageCategory(75.0, "Coverage >= 75%", "green", "black"));
		categories.add(new CoverageCategory(50.0, "Coverage >= 50%", "yellow", "black"));
		categories.add(new CoverageCategory(0.0 + Double.MIN_VALUE, "Coverage > 0%", "red", "black"));
		categories.add(new CoverageCategory(0.0, "Coverage = 0%", "black", "red"));

		categorizer = new Categorizer(categories, defaultCategory);
	}

	@Override
	public String getTitle(PanopticodeProject project) {
		return project.getName() + " Code Coverage";
	}

	@Override
	public Categorizer getCategorizer() {
		return categorizer;
	}

	class CoverageCategory extends Category {
		private double greaterThanInclusive;

		public CoverageCategory(double greaterThanInclusive,
								String legendLabel,
								String fill,
								String border) {
			super(legendLabel, fill, border);

			this.greaterThanInclusive = greaterThanInclusive;
		}

		@Override
		public boolean inCategory(MetricTarget toCheck) {
			RatioMetric metric = (RatioMetric) toCheck.getMetricByName("Line Coverage");

			if (metric == null)
				return false;

			return metric.getPercentValue() >= greaterThanInclusive;
		}
	}
}
