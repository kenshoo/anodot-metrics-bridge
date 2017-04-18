package com.kenshoo.metrics.anodot.metrics2;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.yammer.metrics.core.AnodotMetricRegistry;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.MetricsRegistryListener;
import com.yammer.metrics.core.spec.MetricName;

import java.util.Map;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot2RegistryFactory {

    private Anodot2MetricNameConverter anodotMetricNameConverter;

    public Anodot2RegistryFactory(Anodot2MetricNameConverter anodotMetricNameConverter) {
        this.anodotMetricNameConverter = anodotMetricNameConverter;
    }

    public AnodotMetricRegistry anodot2Registry(MetricsRegistry metricsRegistry) {
        final AnodotMetricRegistry anodotRegistry = new AnodotMetricRegistry();

        // 1. Add all existing metrics
        final Map<MetricName, Metric> anodotMetrics = Maps.newHashMap();
        for (Map.Entry<com.yammer.metrics.core.MetricName, Metric> e : metricsRegistry.allMetrics().entrySet()) {
            anodotMetrics.put(toAnodotName(e.getKey()), e.getValue());
        }
        anodotRegistry.registerAll(anodotMetrics);

        // 2. Add listener to metricsRegistry, to keep anodot registry up to date
        metricsRegistry.addListener(new MetricsRegistryListener() {
            @Override
            public void onMetricAdded(com.yammer.metrics.core.MetricName metricName, Metric metric) {
                anodotRegistry.register(toAnodotName(metricName), metric);
            }

            @Override
            public void onMetricRemoved(com.yammer.metrics.core.MetricName metricName) {
                anodotRegistry.remove(toAnodotName(metricName));
            }
        });
        return anodotRegistry;
    }

    private MetricName toAnodotName(com.yammer.metrics.core.MetricName metricName) {
        final String graphiteName = toGraphiteName(metricName);
        return anodotMetricNameConverter.toAnodot2Name(graphiteName);
    }

    /**
     * See {@link com.yammer.metrics.reporting.GraphiteReporter.sanitizeName}
     */
    private String toGraphiteName(com.yammer.metrics.core.MetricName metricName) {
        return Joiner.on(".").skipNulls().join(
                metricName.getGroup(),
                metricName.getType(),
                metricName.getScope(),
                metricName.getName()
        );
    }

}
