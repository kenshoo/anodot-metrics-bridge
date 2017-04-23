package com.kenshoo.metrics.anodot;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by tzachz on 4/21/17
 */
public class EmptyAnodotGlobalProperties implements AnodotGlobalProperties {

    @Override
    public Map<String, String> propertyMap() {
        return ImmutableMap.of();
    }
}
