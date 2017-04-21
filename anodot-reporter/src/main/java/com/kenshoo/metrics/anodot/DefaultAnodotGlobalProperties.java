package com.kenshoo.metrics.anodot;

import com.google.common.collect.ImmutableMap;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by tzachz on 2/22/17
 */
public class DefaultAnodotGlobalProperties implements AnodotGlobalProperties {

    public static final String VERSION_PROPERTY = "version";
    public static final String BUILD_PROPERTY = "build";
    public static final String SERVER_PROPERTY = "server";

    private final String hostname;
    private final String version;
    private final String build;

    public DefaultAnodotGlobalProperties() {
        this("unknown", "unknown");
    }

    public DefaultAnodotGlobalProperties(String version, String build) {
        this(version, build, localMachineHostname());
    }

    public DefaultAnodotGlobalProperties(String version, String build, String hostname) {
        this.hostname = hostname;
        this.version = version;
        this.build = build;
    }

    @Override
    public Map<String, String> propertyMap() {
        return ImmutableMap.of(
                BUILD_PROPERTY, escapeDots(build),
                VERSION_PROPERTY, escapeDots(version),
                SERVER_PROPERTY, firstToken(hostname)
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
