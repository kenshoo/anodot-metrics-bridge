package com.kenshoo.metrics.anodot.metrics2;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.AnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.AnodotReporterWrapper;
import com.kenshoo.metrics.anodot.EmptyAnodotGlobalProperties;
import com.yammer.metrics.core.AnodotMetricFilter;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.spec.MetricName;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;

/**
 * Created by tzachz on 4/21/17
 */
public class Anodot2ReporterBuilder {

    private static final Anodot2NonZeroFilter NON_ZERO_FILTER = new Anodot2NonZeroFilter();

    private final AnodotReporterConfiguration conf;
    private final List<AnodotMetricFilter> filters = Lists.<AnodotMetricFilter>newArrayList(NON_ZERO_FILTER);
    private AnodotGlobalProperties globalProperties = new EmptyAnodotGlobalProperties();

    public static Anodot2ReporterBuilder builderFor(AnodotReporterConfiguration conf) {
        return new Anodot2ReporterBuilder(conf);
    }

    public Anodot2ReporterBuilder withGlobalProperties(AnodotGlobalProperties globalProperties) {
        this.globalProperties = globalProperties;
        return this;
    }

    public Anodot2ReporterBuilder addFilter(AnodotMetricFilter filter) {
        this.filters.add(filter);
        return this;
    }

    public Anodot2ReporterBuilder turnZeroFilterOff() {
        this.filters.remove(NON_ZERO_FILTER);
        return this;
    }

    public AnodotReporterWrapper build(MetricsRegistry metricRegistry) {
        final AnodotMetricFilter filter = composeFiltersIntoOne();
        return new Anodot2ReporterFactory(conf, globalProperties, filter).anodot2Reporter(metricRegistry);
    }

    private AnodotMetricFilter composeFiltersIntoOne() {
        final ImmutableList<AnodotMetricFilter> filtersImmutable = ImmutableList.copyOf(filters);
        return new AnodotMetricFilter() {
            @Override
            public boolean matches(final MetricName metricName, final Metric metric) {
                return from(filtersImmutable).allMatch(new Predicate<AnodotMetricFilter>() {
                    @Override
                    public boolean apply(AnodotMetricFilter input) {
                        return input.matches(metricName, metric);
                    }
                });
            }
        };
    }

    private Anodot2ReporterBuilder(AnodotReporterConfiguration conf) {
        this.conf = conf;
    }
}
