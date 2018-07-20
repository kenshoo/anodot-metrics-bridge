package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.AnodotMetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.kenshoo.metrics.anodot.AnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.AnodotReporterWrapper;
import com.kenshoo.metrics.anodot.DefaultAnodotReporterConfiguration;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by tzachz on 4/24/17
 */
public class Anodot3ReporterBuilderTest {

    private final AnodotReporterConfiguration conf = new DefaultAnodotReporterConfiguration("t", 1, "http://localhost:8080/anodot");

    private final MetricRegistry registry = new MetricRegistry();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Test
    public void zeroesNotReportedByDefault() throws Exception {
        registry.counter("counter"); // create zero counter
        final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(conf).build(registry);
        runReportCycle(reporter);
        verify(exactly(0), postRequestedFor(urlEqualTo("/anodot?token=t")));
    }

    @Test
    public void zeroFilterTurnedOffMeansNoFilters() throws Exception {
        registry.counter("counter"); // create zero counter
        final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(conf).turnZeroFilterOff().build(registry);
        runReportCycle(reporter);
        verify(postRequestedFor(urlEqualTo("/anodot?token=t")));
    }

    @Test
    public void mustPassAllFilters() throws Exception {
        registry.counter("counter1111").inc();
        registry.counter("counter2222").inc();
        registry.counter("meter1111").inc();

        final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(conf)
                .addFilter(whatPropertyContains("count")) // both counters pass this one
                .addFilter(whatPropertyContains("1111"))  // but only the first passes this one
                .build(registry);

        runReportCycle(reporter);
        verify(postRequestedFor(urlEqualTo("/anodot?token=t"))
                .withRequestBody(containing("counter1111"))
                .withRequestBody(notMatching(".*counter2222.*"))
                .withRequestBody(notMatching(".*meter1111.*"))
        );
    }

    private AnodotMetricFilter whatPropertyContains(final String substring) {
        return (metricName, metric) -> metricName.getProperty("what").getValue().contains(substring);
    }

    private void runReportCycle(AnodotReporterWrapper reporter) throws InterruptedException {
        reporter.start();
        Thread.sleep(1500);
        reporter.stop();
    }

}