package com.kenshoo.metrics.anodot;

import org.junit.Test;

import java.util.Map;

import static com.kenshoo.metrics.anodot.DefaultAnodotGlobalProperties.BUILD_PROPERTY;
import static com.kenshoo.metrics.anodot.DefaultAnodotGlobalProperties.SERVER_PROPERTY;
import static com.kenshoo.metrics.anodot.DefaultAnodotGlobalProperties.VERSION_PROPERTY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tzachz on 2/22/17
 */
public class DefaultAnodotGlobalPropertiesTest {

    @Test
    public void dotsClearedFromVersionAndBuild() throws Exception {
        final Map<String, String> properties = new DefaultAnodotGlobalProperties("v1.0.1", "1234.56").propertyMap();
        assertThat(properties.get(VERSION_PROPERTY), is("v1_0_1"));
        assertThat(properties.get(BUILD_PROPERTY), is("1234_56"));
    }

    @Test
    public void postfixesRemovedFromHostname() throws Exception {
        final Map<String, String> properties = new DefaultAnodotGlobalProperties("1", "1", "hostname.domain.com").propertyMap();
        assertThat(properties.get(SERVER_PROPERTY), is("hostname"));
    }

    @Test
    public void defaultVersionAndBuildAreUnknown() throws Exception {
        final Map<String, String> properties = new DefaultAnodotGlobalProperties().propertyMap();
        assertThat(properties.get(VERSION_PROPERTY), is("unknown"));
        assertThat(properties.get(BUILD_PROPERTY), is("unknown"));
    }

}