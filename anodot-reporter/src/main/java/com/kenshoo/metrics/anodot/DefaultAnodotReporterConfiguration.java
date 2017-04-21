package com.kenshoo.metrics.anodot;

import java.util.Objects;

/**
 * Created by tzachz on 4/21/17
 */
public class DefaultAnodotReporterConfiguration implements AnodotReporterConfiguration {

    private final String token;
    private final int intervalSeconds;
    private final String uri;

    public DefaultAnodotReporterConfiguration(String token, int intervalSeconds, String uri) {
        this.token = token;
        this.intervalSeconds = intervalSeconds;
        this.uri = uri;
    }


    @Override
    public String getToken() {
        return token;
    }

    @Override
    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "DefaultAnodotReporterConfiguration{" +
                "token='" + token + '\'' +
                ", intervalSeconds=" + intervalSeconds +
                ", uri='" + uri + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultAnodotReporterConfiguration that = (DefaultAnodotReporterConfiguration) o;
        return intervalSeconds == that.intervalSeconds &&
                Objects.equals(token, that.token) &&
                Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, intervalSeconds, uri);
    }
}
