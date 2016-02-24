/*
 * Copyright (c) 2006-2007 Julias R. Shaw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.panopticode.report.treemap;

import org.panopticode.*;

import java.util.List;
import java.util.LinkedList;

public class ComplexityTreemap extends BaseTreemap {
    private Categorizer categorizer;

    public ComplexityTreemap() {
        super();

        DefaultCategory defaultCategory = new DefaultCategory("N/A", "blue", "black");

        List<Category> categories = new LinkedList<Category>();
        categories.add(new ComplexityCategory(6, "CCN 1-5", "green", "black"));
        categories.add(new ComplexityCategory(10, "CCN 6-9", "yellow", "black"));
        categories.add(new ComplexityCategory(25, "CCN 10-24", "red", "black"));
        categories.add(new ComplexityCategory(Integer.MAX_VALUE, "CCN 25+", "black", "red"));

        categorizer = new Categorizer(categories, defaultCategory);
    }

    public String getTitle(PanopticodeProject project) {
        return project.getName() + " Complexity";
    }

    public Categorizer getCategorizer() {
        return categorizer;
    }

    class ComplexityCategory extends Category {
        private int lessThanExclusive;

        public ComplexityCategory(int lessThanExclusive,
                                  String legendLabel,
                                  String fill,
                                  String border) {
            super(legendLabel, fill, border);

            this.lessThanExclusive = lessThanExclusive;
        }

        public boolean inCategory(MetricTarget toCheck) {
            IntegerMetric metric = (IntegerMetric)toCheck.getMetricByName("CCN");

            if (metric == null) {
                return false;
            }

            return metric.getValue() < lessThanExclusive;
        }
    }
}
