package com.kenshoo.metrics.anodot;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kenshoo.metrics.anodot.AnodotProperties.*;

/**
 * Created by tzachz on 2/21/17
 */
public class DWMetricNameParser {

    private static final Joiner UNDERSCORE_JOINER = Joiner.on("_");

    private final String whatProperty;
    private final Map<String, String> properties = new HashMap<>();

    public DWMetricNameParser(String metricName, AnodotGlobalProperties globalProperties) {
        final String[] parts = metricName.split("\\.");
        final int classIndex = getClassIndex(parts);
        final boolean isClassFirstOrLast = classIndex == 0 || classIndex == parts.length - 1;

        if (isClassFirstOrLast) {
            // not a standard package.class.metric format, falling back to naive conversion
            whatProperty = UNDERSCORE_JOINER.join(parts);
        } else {
            properties.put(CLASS_PROPERTY, parts[classIndex]);
            properties.put(PACKAGE_PROPERTY, UNDERSCORE_JOINER.join(take(parts, classIndex)));
            whatProperty = UNDERSCORE_JOINER.join(drop(parts, classIndex + 1));
        }
        properties.putAll(globalProperties.propertyMap());
    }

    private List<String> take(String[] arr, int n) {
        return Arrays.asList(arr).subList(0, n);
    }

    private List<String> drop(String[] arr, int n) {
        return Arrays.asList(arr).subList(n, arr.length);
    }

    private int getClassIndex(String[] parts) {
        for (int i = 0; i < parts.length; i++) {
            if (!isAllLowerCase(parts[i])) {
                return i;
            }
        }
        return 0;
    }

    private boolean isAllLowerCase(String text) {
        // StringUtils.isAllLowerCase returns false for digits, using this hack instead
        return text.toLowerCase().equals(text);
    }

    public String getWhatProperty() {
        return whatProperty;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
