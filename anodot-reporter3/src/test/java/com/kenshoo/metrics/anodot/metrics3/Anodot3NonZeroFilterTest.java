package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.spec.MetricName;
import com.codahale.metrics.Gauge;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot3NonZeroFilterTest {

    private final Anodot3NonZeroFilter filter = new Anodot3NonZeroFilter();
    private final MetricName METRIC_NAME = MetricName.builder("ignored").build();

    @Test
    public void zeroGaugesFilteredOut() throws Exception {
        assertThat(filter.matches(METRIC_NAME, gauge(0)), is(false));
        assertThat(filter.matches(METRIC_NAME, gauge(0F)), is(false));
        assertThat(filter.matches(METRIC_NAME, gauge(0D)), is(false));
        assertThat(filter.matches(METRIC_NAME, gauge(0L)), is(false));
    }

    @Test
    public void nonZeroGaugesNotFilteredOut() throws Exception {
        assertThat(filter.matches(METRIC_NAME, gauge(1)), is(true));
        assertThat(filter.matches(METRIC_NAME, gauge(1F)), is(true));
        assertThat(filter.matches(METRIC_NAME, gauge(1D)), is(true));
        assertThat(filter.matches(METRIC_NAME, gauge(1L)), is(true));
        assertThat(filter.matches(METRIC_NAME, gauge("1")), is(true));
    }

    private <T> Gauge<T> gauge(final T value) {
        return () -> value;
    }

}