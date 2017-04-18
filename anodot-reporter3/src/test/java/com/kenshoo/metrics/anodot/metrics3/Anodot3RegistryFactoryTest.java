package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.AnodotMetricRegistry;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot3RegistryFactoryTest {

    private static final String NAME = "theMetric";

    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final Anodot3MetricNameConverter anodotMetricNameConverter = new Anodot3MetricNameConverter(new AnodotGlobalProperties());

    private final Anodot3RegistryFactory factory = new Anodot3RegistryFactory(anodotMetricNameConverter);

    private AnodotMetricRegistry anodotMetricRegistry;

    @Before
    public void init() throws Exception {
        this.anodotMetricRegistry = factory.anodot3Registry(metricRegistry);
    }

    @Test
    public void addingSameMetricAgainReturnsSameMetric() throws Exception {
        metricRegistry.counter(NAME).inc();
        final Counter c1 = getCounter(NAME);
        metricRegistry.counter(NAME).inc();
        final Counter c2 = getCounter(NAME);
        assertThat(c1, sameInstance(c2));
    }

    @Test
    public void concurrentAccessToMetricIsSafe() throws Exception {
        final AtomicReference<Optional<RuntimeException>> firstFailure = new AtomicReference<>(Optional.<RuntimeException>absent());
        final int threads = 20;

        runInParallel(new Runnable() {
            @Override
            public void run() {
                try {
                    metricRegistry.counter(NAME).inc();
                } catch (RuntimeException e) {
                    firstFailure.compareAndSet(Optional.<RuntimeException>absent(), Optional.of(e));
                }
            }
        }, threads);

        if (firstFailure.get().isPresent()) {
            throw firstFailure.get().get();
        }
        assertThat((int) getCounter(NAME).getCount(), is(threads));
    }

    private void runInParallel(Runnable task, int threads) throws InterruptedException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < 20; i++) {
            threadPool.submit(task);
        }
        threadPool.shutdown();
        threadPool.awaitTermination(500, TimeUnit.MILLISECONDS);
    }

    private Counter getCounter(String name) {
        return anodotMetricRegistry.getCounters().get(anodotMetricNameConverter.toAnodot3Name(name));
    }
}