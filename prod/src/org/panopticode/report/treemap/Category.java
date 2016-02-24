package org.panopticode.report.treemap;

import org.panopticode.MetricTarget;

public abstract class Category {
    String fill;
    String border;
    String legendLabel;

    public Category(String legendLabel, String fill, String border) {
        this.legendLabel = legendLabel;
        this.fill = fill;
        this.border = border;
    }

    public String getFill() {
        return fill;
    }

    public String getBorder() {
        return border;
    }

    public String getLegendLabel() {
        return legendLabel;
    }

    public abstract boolean inCategory(MetricTarget toCheck);
}
