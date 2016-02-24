package org.panopticode;

import java.util.Collection;

public interface MetricTarget {
    void addMetric(Metric metric);

    /**
     * @return The children or an empty Collection if there are no children.  Must NEVER return null.
     */
    Collection<MetricTarget> getChildren();

    String getName();

    /**
     * @return The Level of the MetricTarget.  Must NEVER return null;
     */
    Level getLevel();

    /**
     * @param name The name of the Metric desired.
     *
     * @return The Metric with the given name or null if no metric with that name exists.
     */
    Metric getMetricByName(String name);

    /**
     * @return All Metrics on this target or an empty Collection.  Must NEVER return null.
     */
    Collection<Metric> getMetrics();

    /**
     * @return The parent MetricTarget or null if there is no parent.
     */
    MetricTarget getParent();

    boolean isLevel(Level toCheck);
}
