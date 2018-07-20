package com.kenshoo.metrics.anodot.metrics2;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.kenshoo.metrics.anodot.AnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.AnodotReporterWrapper;
import com.kenshoo.metrics.anodot.DefaultAnodotReporterConfiguration;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.AnodotMetricFilter;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by tzachz on 4/24/17
 */
public class Anodot2ReporterBuilderTest {

    private final AnodotReporterConfiguration conf = new DefaultAnodotReporterConfiguration("t", 1, "http://localhost:8080/anodot");

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Test
    public void zeroesNotReportedByDefault() throws Exception {
        Metrics.newCounter(this.getClass(), "counter3333"); // create zero counter
        final AnodotReporterWrapper reporter = Anodot2ReporterBuilder.builderFor(conf).build(Metrics.defaultRegistry());
        runReportCycle(reporter);
        verify(exactly(0), postRequestedFor(urlEqualTo("/anodot?token=t")).withRequestBody(containing("counter3333")));
    }

    @Test
    public void zeroFilterTurnedOffMeansNoFilters() throws Exception {
        Metrics.newCounter(this.getClass(), "counter"); // create zero counter
        final AnodotReporterWrapper reporter = Anodot2ReporterBuilder.builderFor(conf).turnZeroFilterOff().build(Metrics.defaultRegistry());
        runReportCycle(reporter);
        verify(postRequestedFor(urlEqualTo("/anodot?token=t")));
    }

    @Test
    public void mustPassAllFilters() throws Exception {
        Metrics.newCounter(this.getClass(), "counter1111").inc();
        Metrics.newCounter(this.getClass(), "counter2222").inc();
        Metrics.newCounter(this.getClass(), "meter1111").inc();

        final AnodotReporterWrapper reporter = Anodot2ReporterBuilder.builderFor(conf)
                .addFilter(whatPropertyContains("count")) // both counters pass this one
                .addFilter(whatPropertyContains("1111"))  // but only the first passes this one
                .build(Metrics.defaultRegistry());

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
        Thread.sleep(1300);
        reporter.stop();
    }
}