package com.kenshoo.metrics.anodot.metrics2;

import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.yammer.metrics.core.AnodotMetricRegistry;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot2RegistryFactoryTest {

    private final Anodot2RegistryFactory factory = new Anodot2RegistryFactory(new Anodot2MetricNameConverter(new AnodotGlobalProperties()));
    private final MetricsRegistry registry = new MetricsRegistry();

    @Test
    public void allPropertiesIncludedInAnodotName() throws Exception {
        registry.newCounter(new MetricName("group", "type", "name", "scope"));
        final AnodotMetricRegistry anodot = factory.anodot2Registry(registry);
        assertThat(getFirstCounter(anodot).getProperty("what").getValue(), is("group_type_scope_name"));
    }

    @Test
    public void nullScopeIsSkipped() throws Exception {
        registry.newCounter(new MetricName("group", "type", "name"));
        final AnodotMetricRegistry anodot = factory.anodot2Registry(registry);
        assertThat(getFirstCounter(anodot).getProperty("what").getValue(), is("group_type_name"));
    }

    @Test
    public void metricsDifferingInScopeOnlyDoNotCauseDuplicateError() throws Exception {
        registry.newCounter(new MetricName("group", "type", "name", "scope1"));
        registry.newCounter(new MetricName("group", "type", "name", "scope2"));
        factory.anodot2Registry(registry); // should not throw exception
    }

    private com.yammer.metrics.core.spec.MetricName getFirstCounter(AnodotMetricRegistry anodot) {
        return anodot.getCounters().keySet().iterator().next();
    }

}