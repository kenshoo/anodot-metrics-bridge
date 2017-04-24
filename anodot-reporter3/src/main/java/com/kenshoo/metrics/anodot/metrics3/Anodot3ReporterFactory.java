package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.Anodot;
import com.anodot.metrics.AnodotMetricFilter;
import com.anodot.metrics.AnodotMetricRegistry;
import com.anodot.metrics.AnodotReporter;
import com.codahale.metrics.MetricRegistry;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.AnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.AnodotReporterWrapper;

import java.util.concurrent.TimeUnit;

/**
 * Created by tzachz on 2/21/17
 */
class Anodot3ReporterFactory {

    private final AnodotReporterConfiguration conf;
    private final AnodotGlobalProperties globalProperties;
    private final AnodotMetricFilter filter;

    Anodot3ReporterFactory(AnodotReporterConfiguration conf, AnodotGlobalProperties globalProperties, AnodotMetricFilter filter) {
        this.conf = conf;
        this.globalProperties = globalProperties;
        this.filter = filter;
    }

    AnodotReporterWrapper anodot3Reporter(MetricRegistry metricRegistry) {
        final Anodot3MetricNameConverter converter = new Anodot3MetricNameConverter(globalProperties);
        final Anodot3RegistryFactory registryFactory = new Anodot3RegistryFactory(converter);
        final AnodotMetricRegistry anodotMetricRegistry = registryFactory.anodot3Registry(metricRegistry);
        return anodot3Reporter(anodotMetricRegistry);
    }

    private AnodotReporterWrapper anodot3Reporter(AnodotMetricRegistry anodotRegistry) {
        final AnodotReporter reporter = AnodotReporter
                .forRegistry(anodotRegistry)
                .filter(filter)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .convertRatesTo(TimeUnit.SECONDS)
                .build(new Anodot(conf.getUri(), conf.getToken()));

        return new AnodotReporterWrapper() {
            @Override
            public void start() {
                reporter.start(conf.getIntervalSeconds(), TimeUnit.SECONDS);
            }

            @Override
            public void stop() {
                reporter.stop();
            }
        };
    }
}
