package org.panopticode.report.treemap;

import org.panopticode.MetricTarget;

public class DefaultCategory extends Category {
    public DefaultCategory(String legendLabel, String fill, String border) {
        super(legendLabel, fill, border);
    }

    public final boolean inCategory(MetricTarget toCheck) {
        return true;
    }
}
