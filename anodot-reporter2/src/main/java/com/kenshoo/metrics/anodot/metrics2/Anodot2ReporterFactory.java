package com.kenshoo.metrics.anodot.metrics2;

import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.AnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.AnodotReporterWrapper;
import com.yammer.metrics.core.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by tzachz on 2/21/17
 */
class Anodot2ReporterFactory {

    private final AnodotReporterConfiguration conf;
    private final AnodotGlobalProperties globalProperties;
    private final AnodotMetricFilter filter;

    Anodot2ReporterFactory(AnodotReporterConfiguration conf, AnodotGlobalProperties globalProperties, AnodotMetricFilter filter) {
        this.conf = conf;
        this.globalProperties = globalProperties;
        this.filter = filter;
    }

    AnodotReporterWrapper anodot2Reporter(MetricsRegistry metricRegistry) {
        final Anodot2MetricNameConverter converter = new Anodot2MetricNameConverter(globalProperties);
        final Anodot2RegistryFactory registryFactory = new Anodot2RegistryFactory(converter);
        final AnodotMetricRegistry anodotMetricRegistry = registryFactory.anodot2Registry(metricRegistry);
        return anodot2Reporter(anodotMetricRegistry);
    }

    private AnodotReporterWrapper anodot2Reporter(AnodotMetricRegistry anodot2Registry) {
        final AnodotReporter reporter = AnodotReporter.forRegistry(anodot2Registry)
                .filter(filter)
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
