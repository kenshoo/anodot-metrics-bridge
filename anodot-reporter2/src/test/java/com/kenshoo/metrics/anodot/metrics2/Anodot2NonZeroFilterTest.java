package com.kenshoo.metrics.anodot.metrics2;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.spec.MetricName;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot2NonZeroFilterTest {

    private final Anodot2NonZeroFilter filter = new Anodot2NonZeroFilter();
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

    @Test
    public void nonZeroCounterNotFilteredOut() throws Exception {
        final Counter counter = Metrics.newCounter(this.getClass(), "counter1");
        counter.inc();
        assertThat(filter.matches(METRIC_NAME, counter), is(true));
    }

    private <T> Gauge<T> gauge(final T value) {
        return new Gauge<T>() {
            @Override
            public T value() {
                return value;
            }
        };
    }
}