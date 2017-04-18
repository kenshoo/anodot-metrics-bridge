package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.AnodotMetricRegistry;
import com.anodot.metrics.spec.MetricName;
import com.codahale.metrics.*;

import java.util.Map;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot3RegistryFactory {

    private final Anodot3MetricNameConverter anodotMetricNameConverter;

    public Anodot3RegistryFactory(Anodot3MetricNameConverter anodotMetricNameConverter) {
        this.anodotMetricNameConverter = anodotMetricNameConverter;
    }

    public AnodotMetricRegistry anodot3Registry(MetricRegistry metricRegistry) {
        final AnodotMetricRegistry anodotRegistry = new AnodotMetricRegistry();
        // 1. Add all existing metrics
        for (Map.Entry<String, Metric> e : metricRegistry.getMetrics().entrySet()) {
            registerInAnodot(anodotRegistry, e.getKey(), e.getValue());
        }

        // 2. Add listener to metricsRegistry, to keep anodot registry up to date
        metricRegistry.addListener(getListener(anodotRegistry));
        return anodotRegistry;
    }

    private <T extends Metric> T registerInAnodot(AnodotMetricRegistry anodotRegistry, String name, T metric) {
        final MetricName anodotName = anodotMetricNameConverter.toAnodot3Name(name);
        if (!anodotRegistry.getNames().contains(anodotName)) try {
            anodotRegistry.register(anodotName, metric);
        } catch (IllegalArgumentException ignored) {
            // already exists due to race condition - just ignore, it's the same metric
        }
        return metric;
    }

    private MetricRegistryListener getListener(final AnodotMetricRegistry anodotRegistry) {
        return new MetricRegistryListener() {

            @Override
            public void onGaugeAdded(String s, Gauge<?> gauge) {
                registerInAnodot(anodotRegistry, s, gauge);
            }

            @Override
            public void onGaugeRemoved(String s) {
                remove(s);
            }

            @Override
            public void onCounterAdded(String s, Counter counter) {
                registerInAnodot(anodotRegistry, s, counter);
            }

            @Override
            public void onCounterRemoved(String s) {
                remove(s);
            }

            @Override
            public void onHistogramAdded(String s, Histogram histogram) {
                registerInAnodot(anodotRegistry, s, histogram);
            }

            @Override
            public void onHistogramRemoved(String s) {
                remove(s);
            }

            @Override
            public void onMeterAdded(String s, Meter meter) {
                registerInAnodot(anodotRegistry, s, meter);
            }

            @Override
            public void onMeterRemoved(String s) {
                remove(s);
            }

            @Override
            public void onTimerAdded(String s, Timer timer) {
                registerInAnodot(anodotRegistry, s, timer);
            }

            @Override
            public void onTimerRemoved(String s) {
                remove(s);
            }

            private void remove(String s) {
                anodotRegistry.remove(anodotMetricNameConverter.toAnodot3Name(s));
            }
        };
    }

}
