package com.kenshoo.metrics.anodot;

import com.google.common.collect.ImmutableMap;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by tzachz on 2/22/17
 */
public class AnodotGlobalProperties {

    private final String hostname;
    private final String version;
    private final String build;

    public AnodotGlobalProperties() {
        this("unknown", "unknown");
    }

    public AnodotGlobalProperties(String version, String build) {
        this(version, build, localMachineHostname());
    }

    public AnodotGlobalProperties(String version, String build, String hostname) {
        this.hostname = hostname;
        this.version = version;
        this.build = build;
    }

    public Map<String, String> propertyMap() {
        return ImmutableMap.of(
                AnodotProperties.BUILD_PROPERTY, escapeDots(build),
                AnodotProperties.VERSION_PROPERTY, escapeDots(version),
                AnodotProperties.SERVER_PROPERTY, firstToken(hostname)
        );
    }

    private String firstToken(String str) {
        return str.split("\\.")[0];
    }

    private String escapeDots(String str) {
        return str.replace(".", "_");
    }

    private static String localMachineHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "not-found";
        }
    }
}
