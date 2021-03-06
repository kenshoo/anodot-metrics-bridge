package com.kenshoo.metrics.anodot.metrics2;

import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.DWMetricNameParser;

/**
 * Created by tzachz on 2/21/17
 */
class Anodot2MetricNameConverter {

    private final AnodotGlobalProperties globalProperties;

    Anodot2MetricNameConverter(AnodotGlobalProperties globalProperties) {
        this.globalProperties = globalProperties;
    }

    com.yammer.metrics.core.spec.MetricName toAnodot2Name(String name) {
        final DWMetricNameParser parser = new DWMetricNameParser(name, globalProperties);
        final com.yammer.metrics.core.spec.MetricName.MetricNameBuilder builder = com.yammer.metrics.core.spec.MetricName.builder(parser.getWhatProperty());
        parser.getProperties().forEach(builder::withPropertyValue);
        return builder.build();
    }

}
