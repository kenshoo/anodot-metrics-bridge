package com.kenshoo.metrics.anodot.example;

import com.codahale.metrics.MetricRegistry;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.AnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.AnodotReporterWrapper;
import com.kenshoo.metrics.anodot.DefaultAnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.metrics3.Anodot3ReporterFactory;

/**
 * Created by tzachz on 4/21/17
 *
 * This is a usage example for sending Metrics3 data to Anodot.
 * You can create an equivalent version for Metrics3 (just switch all 3'th to 2'th ;))
 */
public class AnodotReporterFactoryExample {

    public static void main(String[] args) {
        // We assume your app has an active MetricRegistry (e.g. within a Dropwizard app)
        final MetricRegistry metricRegistry = new MetricRegistry();

        // It might already contain some metrics:
        metricRegistry.counter("some.counter").inc();

        // Create AnodotGlobalProperties object with your global properties;
        // These properties will be added to each reported metric - so in Anodot you'll be able to slice by version / build / server
        // Overloads for constructor with fewer arguments exist -
        // For example, if you want the server name to be inferred automatically
        final AnodotGlobalProperties globalProperties = new AnodotGlobalProperties(
                /* version */ "1.0.1",
                /* build   */ "b2232",
                /* server  */ "server1.prod");

        // Create Anodot configuration with Token, Interval (seconds) and URI
        final AnodotReporterConfiguration anodotConf = new DefaultAnodotReporterConfiguration(
                /* token      */ "fake-token",
                /* interval   */ 60,
                /* Anodot URI */ "https://api.anodot.com/api/v1/metrics");

        // Create the reporter factory based on the given configuration.
        final Anodot3ReporterFactory reporterFactory = new Anodot3ReporterFactory(anodotConf);

        // Create the reporter "wrapper" which provides a simple API for starting / stopping the reporting
        // All metrics added to metricsRegistry (before or after reporter was created) will be reported to Anodot
        final AnodotReporterWrapper reporter = reporterFactory.anodot3Reporter(metricRegistry, globalProperties);

        reporter.start(); // will now start reporting

        // Metrics added after reporting starting will also be reported!
        metricRegistry.meter("some.meter").mark();

        reporter.stop(); // you can stop reporting any time (and resume it later by calling start() again)
    }

}
