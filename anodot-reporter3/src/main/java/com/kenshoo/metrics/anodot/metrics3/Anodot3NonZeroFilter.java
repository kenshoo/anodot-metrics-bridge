package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.AnodotMetricFilter;
import com.anodot.metrics.spec.MetricName;
import com.codahale.metrics.Counting;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot3NonZeroFilter implements AnodotMetricFilter {

    @Override
    public boolean matches(MetricName metricName, Metric metric) {
        if (metric instanceof Gauge) {
            final Object value = ((Gauge) metric).getValue();
            // Java, I hate you :(
            if (value instanceof Long) return (Long) value != 0;
            if (value instanceof Integer) return (Integer) value != 0;
            if (value instanceof Float) return (Float) value != 0;
            if (value instanceof Double) return (Double) value != 0;
            else return true;
        } else if (metric instanceof Counting) {
            return ((Counting)metric).getCount() != 0;
        }
        return false;
    }
}