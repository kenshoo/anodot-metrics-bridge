package com.kenshoo.metrics.anodot.example;

import com.codahale.metrics.MetricRegistry;
import com.kenshoo.metrics.anodot.*;
import com.kenshoo.metrics.anodot.metrics3.Anodot3ReporterBuilder;

/**
 * Created by tzachz on 4/21/17
 *
 * This is a usage example for sending Metrics3 data to Anodot.
 * You can create an equivalent version for Metrics3 (just switch all 3'th to 2'th ;))
 */
public class AnodotReporterFactoryExample {

    public static void main(String[] args) {
        // We assume our app has an active MetricRegistry (e.g. within a Dropwizard app)
        final MetricRegistry metricRegistry = new MetricRegistry();

        // It might already contain some metrics:
        metricRegistry.counter("some.counter").inc();

        // Create Anodot configuration with Token, Interval (seconds) and URI
        final AnodotReporterConfiguration anodotConf = new DefaultAnodotReporterConfiguration(
                /* token      */ "your-token",
                /* interval   */ 60,
                /* Anodot URI */ "https://api.anodot.com/api/v1/metrics");

        // Create the reporter "wrapper" which provides a simple API for starting / stopping the reporting
        // All metrics added to metricsRegistry (before or after reporter was created) will be reported to Anodot
        // Use builder optional methods to customize your reporter
        final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(anodotConf)
             // .withGlobalProperties(/* any AnodotGlobalProperties */ ) // you can add global properties to be added to
             // .addFilter(/* any AnodotMetricFilter */)                 // you can add your custom filters
             // .turnZeroFilterOff()                                     // by default, a filter prevents always-zero metrics from being sent; This disables it
                .build(metricRegistry);

        reporter.start(); // will now start reporting

        // Metrics added after reporting starting will also be reported!
        metricRegistry.meter("some.meter").mark();

        reporter.stop(); // you can stop reporting any time (and resume it later by calling start() again)
    }

}
