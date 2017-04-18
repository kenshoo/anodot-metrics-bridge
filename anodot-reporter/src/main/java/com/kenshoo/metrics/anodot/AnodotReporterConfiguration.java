package com.kenshoo.metrics.anodot;

/**
 * Created by tzachz on 2/21/17
 */
public interface AnodotReporterConfiguration {

    String getToken();

    int getIntervalSeconds();

    String getUri();
}
