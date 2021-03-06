package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.spec.MetricName;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.DWMetricNameParser;

/**
 * Created by tzachz on 2/21/17
 */
class Anodot3MetricNameConverter {

    private final AnodotGlobalProperties globalProperties;

    Anodot3MetricNameConverter(AnodotGlobalProperties globalProperties) {
        this.globalProperties = globalProperties;
    }

    MetricName toAnodot3Name(String name) {
        final DWMetricNameParser parser = new DWMetricNameParser(name, globalProperties);
        final MetricName.MetricNameBuilder builder = MetricName.builder(parser.getWhatProperty());
        parser.getProperties().forEach(builder::withPropertyValue);
        return builder.build();
    }

}
