package com.kenshoo.metrics.anodot.metrics3;

import com.anodot.metrics.spec.MetricName;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.kenshoo.metrics.anodot.DWMetricNameParser;

import java.util.Map;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot3MetricNameConverter {

    private final AnodotGlobalProperties globalProperties;

    public Anodot3MetricNameConverter(AnodotGlobalProperties globalProperties) {
        this.globalProperties = globalProperties;
    }

    public MetricName toAnodot3Name(String name) {
        final DWMetricNameParser parser = new DWMetricNameParser(name, globalProperties);
        final MetricName.MetricNameBuilder builder = MetricName.builder(parser.getWhatProperty());
        for (Map.Entry<String, String> property : parser.getProperties().entrySet()) {
            builder.withPropertyValue(property.getKey(), property.getValue());
        }
        return builder.build();
    }

}
