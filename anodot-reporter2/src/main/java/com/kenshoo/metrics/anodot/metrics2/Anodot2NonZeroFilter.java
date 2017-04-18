package com.kenshoo.metrics.anodot.metrics2;

import com.yammer.metrics.core.*;
import com.yammer.metrics.core.spec.MetricName;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot2NonZeroFilter implements AnodotMetricFilter {

    @Override
    public boolean matches(MetricName metricName, Metric metric) {
        if (metric instanceof Gauge) {
            final Object value = ((Gauge) metric).value();
            if (value instanceof Long) return (Long) value != 0;
            if (value instanceof Integer) return (Integer) value != 0;
            if (value instanceof Float) return (Float) value != 0;
            if (value instanceof Double) return (Double) value != 0;
            else return true;
        } else if (metric instanceof Metered) {
            return ((Metered)metric).count() != 0;
        } else if (metric instanceof Histogram) {
            return ((Histogram)metric).count() != 0;
        }
        return false;
    }
}
