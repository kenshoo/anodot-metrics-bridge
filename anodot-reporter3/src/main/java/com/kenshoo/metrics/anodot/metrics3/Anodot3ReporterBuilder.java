package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.AnodotMetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.AnodotReporterConfiguration;
import com.kenshoo.metrics.anodot.AnodotReporterWrapper;
import com.kenshoo.metrics.anodot.EmptyAnodotGlobalProperties;

import java.util.List;

/**
 * Created by tzachz on 4/21/17
 */
public class Anodot3ReporterBuilder {

    private static final Anodot3NonZeroFilter NON_ZERO_FILTER = new Anodot3NonZeroFilter();

    private final AnodotReporterConfiguration conf;
    private final List<AnodotMetricFilter> filters = Lists.newArrayList(NON_ZERO_FILTER);
    private AnodotGlobalProperties globalProperties = new EmptyAnodotGlobalProperties();

    public static Anodot3ReporterBuilder builderFor(AnodotReporterConfiguration conf) {
        return new Anodot3ReporterBuilder(conf);
    }

    public Anodot3ReporterBuilder withGlobalProperties(AnodotGlobalProperties globalProperties) {
        this.globalProperties = globalProperties;
        return this;
    }

    public Anodot3ReporterBuilder addFilter(AnodotMetricFilter filter) {
        this.filters.add(filter);
        return this;
    }

    public Anodot3ReporterBuilder turnZeroFilterOff() {
        this.filters.remove(NON_ZERO_FILTER);
        return this;
    }

    public AnodotReporterWrapper build(MetricRegistry metricRegistry) {
        final AnodotMetricFilter filter = composeFiltersIntoOne();
        return new Anodot3ReporterFactory(conf, globalProperties, filter).anodot3Reporter(metricRegistry);
    }

    private AnodotMetricFilter composeFiltersIntoOne() {
        final ImmutableList<AnodotMetricFilter> filtersImmutable = ImmutableList.copyOf(filters);
        return (metricName, metric) -> filtersImmutable.stream().allMatch(input -> input.matches(metricName, metric));
    }

    private Anodot3ReporterBuilder(AnodotReporterConfiguration conf) {
        this.conf = conf;
    }
}
